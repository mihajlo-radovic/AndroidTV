package com.example.androidtv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.cardview.widget.CardView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.example.androidtv.ui.theme.AndroidTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)

    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.grid_view)
        val settingsButton = findViewById<ImageButton>(R.id.settings_button)

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val list = ArrayList<Model>()

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

        val adapter = GridViewAdapter(this, list)
        gridView.adapter = adapter

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
}


open class Model(val id: Int, val name: String, val image: Int)

class GridViewAdapter(context: Context, list: ArrayList<Model>): ArrayAdapter<Model?>(context, 0, list as List<Model?>){
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var itemView = view

        if(itemView == null){
            itemView = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false)
        }

        val model: Model? = getItem(position)
        val textView = itemView!!.findViewById<TextView>(R.id.text_view)
        val imageView = itemView.findViewById<ImageView>(R.id.image_view)

        textView.text = model!!.name
        imageView.setImageResource(model.image)
        return itemView
    }
}

class Microphone(id: Int, name: String, model: Int) : Model(id, name, model){
}

class Camera(id: Int,name: String, model: Int) : Model(id, name, model){
}

class Keyboard(id: Int,name: String, model: Int) : Model(id, name, model){
}

class Mouse(id: Int,name: String, model: Int) : Model(id, name, model){
}
