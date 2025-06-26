package com.example.gnm18.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gnm18.R
import com.example.gnm18.RoomDb.Achievement
import com.example.gnm18.databinding.ItemAchievementBinding
import java.text.SimpleDateFormat
import java.util.*

class AchievementAdapter : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AchievementViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            with(binding) {
                // Set achievement icon
                achievementIcon.setImageResource(achievement.iconResource) // Make sure this ID matches your XML

                // Set achievement title and description
                achievementTitle.text = achievement.title
                achievementDescription.text = achievement.description

                // Format and display date earned
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val dateString = dateFormat.format(Date(achievement.dateEarned))
                achievementDate.text = itemView.context.getString(R.string.earned_on_date, dateString)

                // Visual treatment for earned vs locked achievements
                if (achievement.dateEarned > 0) {
                    // Achievement earned
                    achievementIcon.alpha = 1f
                    achievementTitle.alpha = 1f
                    achievementDescription.alpha = 0.8f
                    achievementDate.visibility = View.VISIBLE
                    lockIcon.visibility = View.GONE
                    root.strokeWidth = 0
                    root.setCardBackgroundColor(
                        itemView.context.getColor(R.color.achievement_earned_bg)
                    )
                } else {
                    // Achievement locked
                    achievementIcon.alpha = 0.5f
                    achievementTitle.alpha = 0.5f
                    achievementDescription.alpha = 0.5f
                    achievementDate.visibility = View.GONE
                    lockIcon.visibility = View.VISIBLE
                    root.strokeWidth = 2
                    root.setCardBackgroundColor(
                        itemView.context.getColor(R.color.achievement_locked_bg)
                    )
                }
            }
        }
    }

    private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}