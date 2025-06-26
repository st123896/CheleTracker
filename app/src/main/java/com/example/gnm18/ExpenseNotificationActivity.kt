package com.example.gnm18

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gnm18.Adapter.ExpenseAdapter
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.databinding.ActivityExpenseNotificationBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Displays list of user expenses
 * - Shows expense items in RecyclerView
 */
class ExpenseNotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseNotificationBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private val channelId = "expense_notifications"
    private val notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

        // Get current user ID
        val userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getInt("user_id", 0)

        // Observe expenses from database
        expenseViewModel.getExpensesWithCategoryByUser(userId).observe(this) { expenses ->
            if (expenses.isNotEmpty()) {
                val latestExpense = expenses.last() // Get the most recent expense

                // Format date and time
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val dateStr = dateFormat.format(Date(latestExpense.expense.date))
                val timeStr = timeFormat.format(Date(latestExpense.expense.date))

                // Update UI with the latest expense
                binding.tvCategory.text = latestExpense.categoryName ?: "Uncategorized"
                binding.tvAmount.text = getString(R.string.amount_format, "%.2f".format(latestExpense.expense.amount))
                binding.tvDate.text = dateStr
                binding.tvTime.text = timeStr

                // Show notification
                showNotification(
                    latestExpense.categoryName ?: "Uncategorized",
                    latestExpense.expense.amount,
                    dateStr,
                    timeStr
                )
            }
        }

        createNotificationChannel()

        binding.btnViewDetails.setOnClickListener {
            // Navigate to expense details or list
            startActivity(Intent(this, ExpenseListActivity::class.java))
            finish()
        }

        binding.btnDismiss.setOnClickListener {
            finish()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(category: String, amount: Double, date: String, time: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
                return
            }
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.new_expense_added))
            .setContentText(getString(R.string.expense_content_text, category, "%.2f".format(amount)))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.expense_details, category, "%.2f".format(amount), date, time)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can now show the notification
            expenseViewModel.getExpensesWithCategoryByUser(
                getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getInt("user_id", 0)
            ).observe(this) { expenses ->
                if (expenses.isNotEmpty()) {
                    val latestExpense = expenses.last()
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(latestExpense.expense.date))
                    val timeStr = timeFormat.format(Date(latestExpense.expense.date))

                    showNotification(
                        latestExpense.categoryName ?: "Uncategorized",
                        latestExpense.expense.amount,
                        dateStr,
                        timeStr
                    )
                }
            }
        }
    }
}