package com.example.lab3_and.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3_and.R
import com.example.lab3_and.models.Item
import com.example.lab3_and.db.ItemDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAdapter(
    private var itemList: List<Item>,
    private val itemDao: ItemDao // Додаємо DAO для роботи з базою
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton) // Кнопка видалення
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTextView.text = item.title
        holder.descriptionTextView.text = item.description

        // Обробка натискання на кнопку видалення
        holder.deleteButton.setOnClickListener {
            val itemToDelete = itemList[position]
            CoroutineScope(Dispatchers.IO).launch {
                itemDao.delete(itemToDelete) // Видаляємо з бази
                // Оновлюємо список на головному потоці
                withContext(Dispatchers.Main) {
                    val updatedList = itemList.toMutableList()
                    updatedList.removeAt(position)
                    itemList = updatedList
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    // Оновлення даних у адаптері
    fun updateData(items: List<Item>) {
        itemList = items
        notifyDataSetChanged()
    }
}