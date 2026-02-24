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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.tv.material3.ExperimentalTvMaterial3Api

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)

    private lateinit var gridView: GridView
    val list = ArrayList<Model>()
    lateinit var adapter: GridViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.grid_view)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        list.add(Camera(1, "Camera", R.drawable.ic_action_name))
        list.add(Microphone(2, "Microphone", R.drawable.ic_microphone))
        list.add(Mouse(3, "Mouse", R.drawable.ic_mouse))
        list.add(Keyboard(4, "Keyboard", R.drawable.ic_keyboard))

        list.add(Camera(5, "Camera 2", R.drawable.ic_action_name))
        list.add(Microphone(6, "Microphone 2", R.drawable.ic_microphone))
        list.add(Mouse(7, "Mouse 2", R.drawable.ic_mouse))
        list.add(Keyboard(8, "Keyboard 2", R.drawable.ic_keyboard))

        list.add(Camera(9, "Camera 3", R.drawable.ic_action_name))
        list.add(Microphone(10, "Microphone 3", R.drawable.ic_microphone))
        list.add(Mouse(11, "Mouse 3", R.drawable.ic_mouse))
        list.add(Keyboard(12, "Keyboard 3", R.drawable.ic_keyboard))

        adapter = GridViewAdapter(this, list)
        gridView.adapter = adapter

        val addButton = findViewById<Button>(R.id.add_device)
        addButton.setOnClickListener { addDeviceDialog() }

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = list[position]

            when (selectedItem) {

                is Camera -> {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Microphone -> {
                    val intent = Intent(this, MicrophoneActivity::class.java)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Keyboard -> {
                    val intent = Intent(this, KeyboardActivity::class.java)
                    intent.putExtra("device_name", selectedItem.name)
                    startActivity(intent)
                }

                is Mouse -> {
                    val intent = Intent(this, MouseActivity::class.java)
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

        AlertDialog.Builder(this)
            .setTitle("Add new device")
            .setView(dialogView)
            .setPositiveButton("Add"){_,_,->
                val name = nameInput.text.toString()
                val type = spinner.selectedItem as TypeENUM

                if(name.isNotEmpty()){
                    val newId = if (list.isEmpty()) 1 else list.maxOf { it.id } +1
                    val newDevice = Model.addDevice(newId, name, type)

                    list.add(newDevice)
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
//val type ENUM

enum class TypeENUM{
    CAMERA,
    MICROPHONE,
    MOUSE,
    KEYBOARD
}
open class Model(val id: Int, val name: String, val image: Int, type: TypeENUM){
    companion object{
        fun addDevice(id: Int, name: String, type: TypeENUM): Model{
            return when(type){
                TypeENUM.CAMERA -> Camera(id, name, R.drawable.ic_action_name)
                TypeENUM.MICROPHONE -> Microphone(id, name, R.drawable.ic_microphone)
                TypeENUM.MOUSE -> Mouse(id, name, R.drawable.ic_mouse)
                TypeENUM.KEYBOARD -> Keyboard(id, name, R.drawable.ic_keyboard)
            }
        }
    }
}

class GridViewAdapter(context: Context, list: ArrayList<Model>): ArrayAdapter<Model?>(context, 0, list as List<Model?>){
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var itemView = view

        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
        }

        val model: Model? = getItem(position)

        //novo
        val image = itemView!!.findViewById<ImageView>(R.id.active)

        val preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val isOn = preferences.getBoolean(model!!.name, false)

        if(isOn){
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

class Microphone(id: Int, name: String, model: Int,) : Model(id, name, model, TypeENUM.MICROPHONE){
}

class Camera(id: Int,name: String, model: Int) : Model(id, name, model, TypeENUM.CAMERA){
}

class Keyboard(id: Int,name: String, model: Int) : Model(id, name, model, TypeENUM.KEYBOARD){
}

class Mouse(id: Int,name: String, model: Int) : Model(id, name, model, TypeENUM.MOUSE){
}
