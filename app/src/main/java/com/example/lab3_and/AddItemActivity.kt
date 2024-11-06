package com.example.lab3_and

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.lab3_and.db.ItemDatabase
import com.example.lab3_and.models.Item
import kotlinx.coroutines.launch

class AddItemActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAddItem: Button

    private val database by lazy { ItemDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_add_item)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        buttonAddItem = findViewById(R.id.buttonAddItem)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonAddItem.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                // Додаємо новий елемент у базу даних
                addItemToDatabase(title, description)
            }

        }
    }
    private fun addItemToDatabase(title: String, description: String) {
        lifecycleScope.launch {
            val newItem = Item(title = title, description = description)
            database.itemDao().insert(newItem)

            // Повертаємось на головну активність після додавання
            finish() // закриває поточну активність
        }
    }
}