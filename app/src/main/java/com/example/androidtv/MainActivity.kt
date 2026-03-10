package com.example.androidtv

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.tv.material3.ExperimentalTvMaterial3Api
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)

    private lateinit var gridView: GridView
    val list = ArrayList<Model>()
    lateinit var adapter: GridViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.grid_view)
        adapter = GridViewAdapter(this, list)
        gridView.adapter = adapter

        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val addButton = findViewById<Button>(R.id.add_device)
        addButton.setOnClickListener { addDeviceDialog() }

        val meetingButton = findViewById<Button>(R.id.meeting_button)

        meetingButton.setOnClickListener {
            val intent = Intent(this, MeetingActivity::class.java)
            startActivity(intent)
        }

        val searchView = findViewById<SearchView>(R.id.search_devices)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                fetchDevices(query.orEmpty())
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                fetchDevices(text.orEmpty())
                return true
            }
        })

        fetchDevices()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]

            when (selectedItem) {

                is Camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putExtra("device_id", selectedItem.id)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Microphone -> {
                    val intent = Intent(this, MicrophoneActivity::class.java)
                    intent.putExtra("device_id", selectedItem.id)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Keyboard -> {
                    val intent = Intent(this, KeyboardActivity::class.java)
                    intent.putExtra("device_id", selectedItem.id)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Mouse -> {
                    val intent = Intent(this, MouseActivity::class.java)
                    intent.putExtra("device_id", selectedItem.id)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }
            }
        }
    }
    override fun onResume(){
        super.onResume()
        (gridView.adapter as BaseAdapter).notifyDataSetChanged()
    }

    private fun addDeviceDialog(){
        val dialogView = layoutInflater.inflate(R.layout.popup_menu, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.new_device_name)
        val spinner = dialogView.findViewById<Spinner>(R.id.dropdown_menu)

        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TypeENUM.values())

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add new device")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = nameInput.text.toString()
            val type = spinner.selectedItem as TypeENUM

            if (name.isEmpty()){
                nameInput.error = "Name required"
                return@setOnClickListener
            }

            lifecycleScope.launch{
                try {
                    val response = DevicesClient.instance.createDevice(CreateDeviceRequest(name = name, type = type.name))
                    if(response.isSuccessful){
                        dialog.dismiss()
                        fetchDevices()
                    }else{
                        Toast.makeText(this@MainActivity, "Error creating device", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Server error", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    fun fetchDevices(searchInput: String = ""){
        lifecycleScope.launch {
            try {
                val query = if (searchInput.isEmpty()) null else searchInput
                val response = DevicesClient.instance.getDevices(query)
                if (response.isSuccessful) {
                    val apiDevices = response.body()

                    if (apiDevices != null){
                        list.clear()
                        for (device in apiDevices){
                            val model = when(device.type){
                                "CAMERA" -> Camera(device.id, device.name, R.drawable.ic_action_name, device.active)
                                "MICROPHONE" -> Microphone(device.id, device.name, R.drawable.ic_microphone, device.active)
                                "MOUSE" -> Mouse(device.id, device.name, R.drawable.ic_mouse, device.active)
                                "KEYBOARD" -> Keyboard(device.id, device.name, R.drawable.ic_keyboard, device.active)
                                else -> null
                            }
                            model?.let { list.add(it) }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
enum class TypeENUM{
    CAMERA,
    MICROPHONE,
    MOUSE,
    KEYBOARD
}
open class Model(val id: Int, val name: String, val image: Int, var active: Boolean = false)

class GridViewAdapter(context: Context, list: ArrayList<Model>): ArrayAdapter<Model?>(context, 0, list as List<Model?>){
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var itemView = view

        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
        }

        val model: Model? = getItem(position)
        val image = itemView!!.findViewById<ImageView>(R.id.active)

        if(model!!.active){
            image.setImageResource(R.drawable.green)
        }else{
            image.setImageResource(R.drawable.red)
        }

        val textView = itemView!!.findViewById<TextView>(R.id.text_view)
        val imageView = itemView.findViewById<ImageView>(R.id.image_view)

        textView.text = model!!.name
        imageView.setImageResource(model.image)
        return itemView
    }
}

class Microphone(id: Int, name: String, model: Int, active: Boolean = false) : Model(id, name, model, active)

class Camera(id: Int,name: String, model: Int, active: Boolean = false) : Model(id, name, model, active)

class Keyboard(id: Int,name: String, model: Int, active: Boolean = false) : Model(id, name, model, active)

class Mouse(id: Int,name: String, model: Int, active: Boolean = false) : Model(id, name, model, active)