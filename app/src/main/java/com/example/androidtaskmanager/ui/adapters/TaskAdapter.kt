package com.example.androidtaskmanager.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtaskmanager.R
import com.example.androidtaskmanager.databinding.ItemTaskBinding
import com.example.androidtaskmanager.models.Task
import kotlinx.datetime.LocalDate

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], onItemClick)
    }

    override fun getItemCount() = tasks.size

    fun updateList(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(task: Task, onItemClick: (Task) -> Unit) {
            tvTitle.text = task.Title
            tvDeadline.text = "Срок: ${task.deadline}"
            tvStatus.text = task.Status.Name

            itemView.setOnClickListener {
                onItemClick(task)
            }
        }
    }
}