package com.example.lab3_and

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3_and.adapters.MyAdapter
import com.example.lab3_and.api.ApiService
import com.example.lab3_and.db.ItemDatabase
import com.example.lab3_and.models.Item
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var database: ItemDatabase
    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var buttonAdd: Button
    private lateinit var apiService: ApiService

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        database = ItemDatabase.getDatabase(this)

        buttonAdd = findViewById(R.id.addButton)


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)



        lifecycleScope.launch {
            val items = database.itemDao().getAllItems()
            adapter = MyAdapter(items, database.itemDao())
            recyclerView.adapter = adapter
        }

        fabAddItem = findViewById(R.id.fabAddItem)

        fabAddItem.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)

        }

        buttonAdd.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/") // Базовий URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)

            addNewItem()
        }

        loadItems()

    }
    private fun loadItems() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                database.itemDao().getAllItems()
            }
            adapter.updateData(items)
        }
    }
    private fun addNewItem() {
        lifecycleScope.launch {
            try {
                // Отримуємо один елемент з API
                val apiItem = apiService.getItem()  // Цей об'єкт має поля title та completed

                // Перетворюємо отриману відповідь у модель Item для збереження в базі даних
                val itemFromApi = Item(
                    title = apiItem.title,
                    description = apiItem.completed.toString()  // Можна перетворити completed на String
                )


                // Додаємо новий елемент до бази даних
                database.itemDao().insert(itemFromApi)

                // Оновлюємо дані в RecyclerView
                val updatedItems = database.itemDao().getAllItems()
                adapter.updateData(updatedItems)

                Toast.makeText(this@MainActivity, "Елемент додано!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Обробка помилок при запиті до API
                Toast.makeText(this@MainActivity, "Помилка при додаванні елемента: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}