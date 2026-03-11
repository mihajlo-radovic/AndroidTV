package main

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"
	"os"
	"strings"
	"time"

	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/recover"
	_ "modernc.org/sqlite"
)

type DeviceType string

const (
	TypeCamera     DeviceType = "CAMERA"
	TypeMicrophone DeviceType = "MICROPHONE"
	TypeMouse      DeviceType = "MOUSE"
	TypeKeyboard   DeviceType = "KEYBOARD"
)

var allowedTypes = map[DeviceType]struct{}{
	TypeCamera:     {},
	TypeMicrophone: {},
	TypeMouse:      {},
	TypeKeyboard:   {},
}

type Device struct {
	ID          int64      `json:"id"`
	Name        string     `json:"name"`
	Type        DeviceType `json:"type"`
	Active      bool       `json:"active"`
	Resolution  *string    `json:"resolution,omitempty"`
	CapsLock    *bool      `json:"capsLock,omitempty"`
	Volume      *int       `json:"volume,omitempty"`
	Orientation *string    `json:"orientation,omitempty"`
	CreatedAt   string     `json:"createdAt"`
	UpdatedAt   string     `json:"updatedAt"`
}

type createDeviceReq struct {
	Name string `json:"name"`
	Type string `json:"type"`
}

type setActiveReq struct {
	Active bool `json:"active"`
}

type updateDeviceReq struct {
	Resolution  *string `json:"resolution"`
	CapsLock    *bool   `json:"capsLock"`
	Volume      *int    `json:"volume"`
	Orientation *string `json:"orientation"`
}

func main() {
	addr := envOr("ADDR", ":8080")
	dbPath := envOr("DB_PATH", "./devices.db")

	db, err := openDB(dbPath)
	if err != nil {
		log.Fatalf("db open: %v", err)
	}
	defer func() { _ = db.Close() }()

	if err := migrate(db); err != nil {
		log.Fatalf("db migrate: %v", err)
	}

	app := fiber.New(fiber.Config{
		ReadTimeout:  5 * time.Second,
		WriteTimeout: 5 * time.Second,
		IdleTimeout:  30 * time.Second,
		ErrorHandler: func(c *fiber.Ctx, err error) error {
			code := fiber.StatusInternalServerError
			var fe *fiber.Error
			if errors.As(err, &fe) {
				code = fe.Code
			}
			return c.Status(code).JSON(fiber.Map{
				"error": err.Error(),
			})
		},
	})

	app.Use(recover.New())

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"ok": true})
	})

	app.Get("/devices", func(c *fiber.Ctx) error {
		ctx, cancel := context.WithTimeout(c.Context(), 3*time.Second)
		defer cancel()

		devs, err := listDevices(ctx, db, c.Query("searchInput"))
		if err != nil {
			return fiber.NewError(fiber.StatusInternalServerError, fmt.Sprintf("list devices: %v", err))
		}
		return c.JSON(devs)
	})

	app.Post("/devices", func(c *fiber.Ctx) error {
		var req createDeviceReq
		if err := c.BodyParser(&req); err != nil {
			return fiber.NewError(fiber.StatusBadRequest, "invalid JSON body")
		}

		name := strings.TrimSpace(req.Name)
		if name == "" || len(name) > 100 {
			return fiber.NewError(fiber.StatusBadRequest, "name must be 1..100 chars")
		}

		dt, err := parseDeviceType(req.Type)
		if err != nil {
			return fiber.NewError(fiber.StatusBadRequest, err.Error())
		}

		ctx, cancel := context.WithTimeout(c.Context(), 3*time.Second)
		defer cancel()

		dev, err := createDevice(ctx, db, name, dt)
		if err != nil {
			return fiber.NewError(fiber.StatusInternalServerError, fmt.Sprintf("create device: %v", err))
		}

		return c.Status(fiber.StatusCreated).JSON(dev)
	})

	app.Patch("/devices/:id/active", func(c *fiber.Ctx) error {
		id, err := c.ParamsInt("id")
		if err != nil || id <= 0 {
			return fiber.NewError(fiber.StatusBadRequest, "invalid id")
		}

		var req setActiveReq
		if err := c.BodyParser(&req); err != nil {
			return fiber.NewError(fiber.StatusBadRequest, "invalid JSON body")
		}

		ctx, cancel := context.WithTimeout(c.Context(), 5*time.Second)
		defer cancel()

		dev, err := setDeviceActive(ctx, db, int64(id), req.Active)
		if err != nil {
			if errors.Is(err, sql.ErrNoRows) {
				return fiber.NewError(fiber.StatusNotFound, "device not found")
			}
			return fiber.NewError(fiber.StatusInternalServerError, fmt.Sprintf("set active: %v", err))
		}

		return c.JSON(dev)
	})

	app.Patch("/devices/:id", func(c *fiber.Ctx) error {
		id, err := c.ParamsInt("id")
		if err != nil || id <= 0 {
			return fiber.NewError(fiber.StatusBadRequest, "invalid id")
		}

		var req updateDeviceReq
		if err := c.BodyParser(&req); err != nil {
			return fiber.NewError(fiber.StatusBadRequest, "invalid JSON body")
		}

		ctx, cancel := context.WithTimeout(c.Context(), 3*time.Second)
		defer cancel()

		dev, err := updateDevice(ctx, db, int64(id), req)
		if err != nil {
			if errors.Is(err, sql.ErrNoRows) {
				return fiber.NewError(fiber.StatusNotFound, "device not found")
			}
			return fiber.NewError(fiber.StatusInternalServerError, fmt.Sprintf("update device: %v", err))
		}

		return c.JSON(dev)
	})

	log.Printf("listening on %s (db=%s)", addr, dbPath)
	if err := app.Listen(addr); err != nil {
		log.Fatalf("listen: %v", err)
	}
}

func envOr(k, def string) string {
	v := strings.TrimSpace(os.Getenv(k))
	if v == "" {
		return def
	}
	return v
}

func openDB(path string) (*sql.DB, error) {
	dsn := fmt.Sprintf("file:%s?_pragma=busy_timeout(5000)&_pragma=journal_mode(WAL)&_pragma=foreign_keys(ON)", path)
	db, err := sql.Open("sqlite", dsn)
	if err != nil {
		return nil, err
	}
	db.SetMaxOpenConns(10)
	db.SetMaxIdleConns(10)
	db.SetConnMaxLifetime(5 * time.Minute)

	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()
	if err := db.PingContext(ctx); err != nil {
		_ = db.Close()
		return nil, err
	}
	return db, nil
}

func migrate(db *sql.DB) error {
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	stmts := []string{
		`CREATE TABLE IF NOT EXISTS devices (
			id INTEGER PRIMARY KEY AUTOINCREMENT,
			name TEXT NOT NULL,
			type TEXT NOT NULL CHECK (type IN ('CAMERA','MICROPHONE','MOUSE','KEYBOARD')),
			active INTEGER NOT NULL DEFAULT 0 CHECK (active IN (0,1)),
			created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
			updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now'))
		);`,
		`CREATE INDEX IF NOT EXISTS idx_devices_type ON devices(type);`,
		`CREATE INDEX IF NOT EXISTS idx_devices_type_active ON devices(type, active);`,
		`CREATE TRIGGER IF NOT EXISTS trg_devices_updated_at
			AFTER UPDATE ON devices
			FOR EACH ROW
			BEGIN
				UPDATE devices SET updated_at = strftime('%Y-%m-%dT%H:%M:%fZ','now') WHERE id = OLD.id;
			END;`,
	}

	for _, s := range stmts {
		if _, err := db.ExecContext(ctx, s); err != nil {
			return err
		}
	}

	newColumns := []struct {
		column     string
		definition string
	}{
		{"resolution", "TEXT"},
		{"caps_lock", "INTEGER"},
		{"volume", "INTEGER"},
		{"orientation", "TEXT"},
	}

	for _, col := range newColumns {
		if err := addColumnIfNotExists(db, "devices", col.column, col.definition); err != nil {
			return fmt.Errorf("add column %s: %w", col.column, err)
		}
	}

	return nil
}

func addColumnIfNotExists(db *sql.DB, table, column, definition string) error {
	ctx, cancel := context.WithTimeout(context.Background(), 3*time.Second)
	defer cancel()

	rows, err := db.QueryContext(ctx, fmt.Sprintf("PRAGMA table_info(%s)", table))
	if err != nil {
		return err
	}
	defer func() { _ = rows.Close() }()

	for rows.Next() {
		var cid int
		var name, colType string
		var notNull int
		var dfltValue sql.NullString
		var pk int
		if err := rows.Scan(&cid, &name, &colType, &notNull, &dfltValue, &pk); err != nil {
			return err
		}
		if name == column {
			return nil
		}
	}
	if err := rows.Err(); err != nil {
		return err
	}

	_, err = db.ExecContext(ctx, fmt.Sprintf("ALTER TABLE %s ADD COLUMN %s %s", table, column, definition))
	return err
}

func parseDeviceType(s string) (DeviceType, error) {
	t := DeviceType(strings.ToUpper(strings.TrimSpace(s)))
	if _, ok := allowedTypes[t]; !ok {
		return "", fmt.Errorf("type must be one of: CAMERA, MICROPHONE, MOUSE, KEYBOARD")
	}
	return t, nil
}

func scanDevice(row interface {
	Scan(...any) error
}) (Device, error) {
	var d Device
	var activeInt int
	var typ string
	var capsLockInt sql.NullInt64
	var resolution sql.NullString
	var volume sql.NullInt64
	var orientation sql.NullString

	if err := row.Scan(
		&d.ID, &d.Name, &typ, &activeInt,
		&resolution, &capsLockInt, &volume, &orientation,
		&d.CreatedAt, &d.UpdatedAt,
	); err != nil {
		return Device{}, err
	}

	d.Type = DeviceType(typ)
	d.Active = activeInt == 1

	if resolution.Valid {
		d.Resolution = &resolution.String
	}
	if capsLockInt.Valid {
		v := capsLockInt.Int64 == 1
		d.CapsLock = &v
	}
	if volume.Valid {
		v := int(volume.Int64)
		d.Volume = &v
	}
	if orientation.Valid {
		d.Orientation = &orientation.String
	}

	return d, nil
}

func listDevices(ctx context.Context, db *sql.DB, search string) ([]Device, error) {
	query := `
		SELECT id, name, type, active, resolution, caps_lock, volume, orientation, created_at, updated_at
		FROM devices
	`
	var args []any

	if s := strings.TrimSpace(search); s != "" {
		query += ` WHERE name LIKE ? OR type LIKE ?`
		pattern := "%" + s + "%"
		args = append(args, pattern, pattern)
	}

	query += ` ORDER BY type, id`

	rows, err := db.QueryContext(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer func() { _ = rows.Close() }()

	var out []Device
	for rows.Next() {
		d, err := scanDevice(rows)
		if err != nil {
			return nil, err
		}
		out = append(out, d)
	}
	if err := rows.Err(); err != nil {
		return nil, err
	}
	return out, nil
}

func createDevice(ctx context.Context, db *sql.DB, name string, typ DeviceType) (Device, error) {
	res, err := db.ExecContext(ctx, `INSERT INTO devices(name, type, active) VALUES(?,?,0)`, name, string(typ))
	if err != nil {
		return Device{}, err
	}
	id, err := res.LastInsertId()
	if err != nil {
		return Device{}, err
	}
	return getDevice(ctx, db, id)
}

func getDevice(ctx context.Context, db *sql.DB, id int64) (Device, error) {
	row := db.QueryRowContext(ctx, `
		SELECT id, name, type, active, resolution, caps_lock, volume, orientation, created_at, updated_at
		FROM devices
		WHERE id = ?
	`, id)
	return scanDevice(row)
}

func setDeviceActive(ctx context.Context, db *sql.DB, id int64, active bool) (Device, error) {
	conn, err := db.Conn(ctx)
	if err != nil {
		return Device{}, err
	}
	defer func() { _ = conn.Close() }()

	if _, err := conn.ExecContext(ctx, `BEGIN IMMEDIATE`); err != nil {
		return Device{}, err
	}

	committed := false
	defer func() {
		if !committed {
			_, _ = conn.ExecContext(context.Background(), `ROLLBACK`)
		}
	}()

	var typ string
	err = conn.QueryRowContext(ctx, `SELECT type FROM devices WHERE id = ?`, id).Scan(&typ)
	if err != nil {
		return Device{}, err
	}

	if active {
		if _, err := conn.ExecContext(ctx, `UPDATE devices SET active = 0 WHERE type = ? AND active = 1`, typ); err != nil {
			return Device{}, err
		}
		if _, err := conn.ExecContext(ctx, `UPDATE devices SET active = 1 WHERE id = ?`, id); err != nil {
			return Device{}, err
		}
	} else {
		if _, err := conn.ExecContext(ctx, `UPDATE devices SET active = 0 WHERE id = ?`, id); err != nil {
			return Device{}, err
		}
	}

	if _, err := conn.ExecContext(ctx, `COMMIT`); err != nil {
		return Device{}, err
	}
	committed = true

	return getDevice(ctx, db, id)
}

func updateDevice(ctx context.Context, db *sql.DB, id int64, req updateDeviceReq) (Device, error) {
	var setClauses []string
	var args []any

	if req.Resolution != nil {
		setClauses = append(setClauses, "resolution = ?")
		args = append(args, *req.Resolution)
	}
	if req.CapsLock != nil {
		v := 0
		if *req.CapsLock {
			v = 1
		}
		setClauses = append(setClauses, "caps_lock = ?")
		args = append(args, v)
	}
	if req.Volume != nil {
		setClauses = append(setClauses, "volume = ?")
		args = append(args, *req.Volume)
	}
	if req.Orientation != nil {
		setClauses = append(setClauses, "orientation = ?")
		args = append(args, *req.Orientation)
	}

	if len(setClauses) == 0 {
		return getDevice(ctx, db, id)
	}

	args = append(args, id)
	query := fmt.Sprintf("UPDATE devices SET %s WHERE id = ?", strings.Join(setClauses, ", "))

	res, err := db.ExecContext(ctx, query, args...)
	if err != nil {
		return Device{}, err
	}

	n, err := res.RowsAffected()
	if err != nil {
		return Device{}, err
	}
	if n == 0 {
		return Device{}, sql.ErrNoRows
	}

	return getDevice(ctx, db, id)
}
