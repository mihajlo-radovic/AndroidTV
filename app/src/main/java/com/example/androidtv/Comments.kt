package com.example.androidtv


/*

settings button
<ImageButton
        android:id="@+id/settings_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="40dp"
        android:layout_marginStart="850dp"
        android:layout_marginBottom="490dp"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:contentDescription="Open Settings"
        android:src="@drawable/ic_settings" />


        <ImageButton
        android:id="@+id/settings_button"
        android:layout_width="70dp"
        android:layout_height="45dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="5dp"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:contentDescription="Open Settings"
        android:src="@drawable/ic_settings"/>

        <com.google.android.material.button.MaterialButton
        android:id="@+id/settings_button"
        android:layout_width="70dp"
        android:layout_height="45dp"
        android:contentDescription="Open Settings"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:text=""
        app:icon="@drawable/ic_settings"
        app:iconPadding="0dp"
        app:iconGravity="textStart"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="5dp"/>

add button
<ImageButton
        android:id="@+id/add_device"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="120dp"
        android:layout_marginStart="770dp"
        android:layout_marginBottom="490dp"
        android:focusable="true"
        android:focusableInTouchMode="true"
        android:contentDescription="Add new device"
        android:src="@drawable/ic_plus"
        tools:listItem="@layout/popup_menu"/>

popup menu xml button
<Button
        android:id="@+id/add_new_device"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp"
        android:layout_marginStart="300dp"
        android:layout_marginEnd="300dp"
        android:text="Add new device"
        />
 */

/*
Card view

<androidx.cardview.widget.CardView
        android:id="@+id/card1"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        android:layout_marginStart="70dp"
        android:layout_marginTop="70dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card2"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card1"
        app:layout_constraintTop_toTopOf="@id/card1"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card3"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card2"
        app:layout_constraintTop_toTopOf="@id/card2"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card4"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card3"
        app:layout_constraintTop_toTopOf="@id/card3"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card5"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card4"
        app:layout_constraintTop_toTopOf="@id/card4"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card6"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        android:layout_marginStart="70dp"
        android:layout_marginTop="300dp"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card7"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card6"
        app:layout_constraintTop_toTopOf="@id/card6"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card8"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card7"
        app:layout_constraintTop_toTopOf="@id/card7"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card9"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card8"
        app:layout_constraintTop_toTopOf="@id/card8"
        android:layout_marginStart="24dp"/>

    <androidx.cardview.widget.CardView
        android:id="@+id/card10"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintWidth_percent = "0.15"
        app:layout_constraintHeight_percent= "0.30"
        app:layout_constraintStart_toEndOf="@id/card9"
        app:layout_constraintTop_toTopOf="@id/card9"
        android:layout_marginStart="24dp"/>


        <TextView
        android:id="@+id/enable_camera"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="100dp"
        android:layout_marginStart="40dp"
        android:layout_marginBottom="390dp"
        android:layout_marginEnd="600dp"
        android:text="Enable Camera"
        android:textColor="@color/cardview_light_background"
        android:textStyle="bold" />

    <Button
        android:id="@+id/toggle_camera_button"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:text="Toggle Camera"
        app:layout_constraintTop_toBottomOf="@+id/enable_camera"
        tools:layout_editor_absoluteX="0dp" />


        android:layout_marginTop="100dp"
        android:layout_marginStart="390dp"
        android:layout_marginBottom="390dp"
        android:layout_marginEnd="400dp"
 */