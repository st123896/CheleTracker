package com.example.gnm18


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.gnm18.Models.CategoryViewModel
import com.example.gnm18.Models.ExpenseViewModel
import com.example.gnm18.RoomDb.Expense
import com.example.gnm18.databinding.ActivityAddExpenseBinding
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Handles expense creation and saving
 * - Manages expense form with validation
 * - Supports photo capture and storage
 * - Date picker for expense date selection
 * - Dynamic category spinner population
 * - File provider for camera operations
 */

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectedDate: Calendar = Calendar.getInstance()
    private var photoPath: String? = null
    private var userId: Int = 0
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Setup toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        setupNavigation()
        userId = getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getInt("user_id", 0)

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupCategorySpinner()
        setupDatePicker()
        setupPhotoButton()

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }
    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.nav_expenses -> {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, ExpenseListActivity::class.java))
                }
                R.id.nav_categories -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "categories")
                    })
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "budget")
                    })
                }
                R.id.nav_graph -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "graph")
                    })
                }
                R.id.nav_achievements -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("open_fragment", "achievements")
                    })
                }
                R.id.nav_logout -> {
                    getSharedPreferences("user_prefs", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.nav_report -> {
                    startActivity(Intent(this, ReportActivity::class.java))
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, ExpenseNotificationActivity::class.java))
                    true
                }
                R.id.nav_notification -> {
                    startActivity(Intent(this, ExpenseNotificationActivity::class.java))
                    true
                }
            }
            drawerLayout.closeDrawer(navView)
            true
        }
    }
    private fun setupCategorySpinner() {
        categoryViewModel.getCategoriesByUser(userId).observe(this) { categories ->
            val adapter = ArrayAdapter(
                this,
                R.layout.spinner_item,
                categories.map { it.name }
            )
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter
        }
    }

    private fun setupDatePicker() {
        binding.tvSelectedDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    binding.tvSelectedDate.setText(SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                        .format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // In AddExpenseActivity
    private fun setupPhotoButton() {
        binding.btnAddPhoto.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Photo")
            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> takePhoto()
                    options[item] == "Choose from Gallery" -> chooseFromGallery()
                    options[item] == "Cancel" -> dialog.dismiss()
                }
            }
            builder.show()
        }
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                val photoFile = createImageFile()
                photoFile?.let {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        it
                    )
                    photoPath = it.absolutePath
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            } catch (ex: Exception) {
                Toast.makeText(this, "Error creating file: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    photoPath?.let { path ->
                        val bitmap = BitmapFactory.decodeFile(path)
                        binding.ivExpensePhoto.setImageBitmap(bitmap)
                        binding.ivExpensePhoto.visibility = View.VISIBLE
                    }
                }
            }
            REQUEST_IMAGE_GALLERY -> {
                if (resultCode == RESULT_OK && data != null) {
                    val selectedImage = data.data
                    selectedImage?.let { uri ->
                        try {
                            val inputStream = contentResolver.openInputStream(uri)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.ivExpensePhoto.setImageBitmap(bitmap)
                            binding.ivExpensePhoto.visibility = View.VISIBLE

                            // Save to app's directory to have permanent access
                            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            val file = File.createTempFile(
                                "JPEG_${timeStamp}_",
                                ".jpg",
                                storageDir
                            ).apply {
                                parentFile?.mkdirs()
                            }

                            FileOutputStream(file).use { out ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            }
                            photoPath = file.absolutePath
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }




    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) // Fixed: changed HMS to HHmmss
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Create parent directories if they don't exist
            parentFile?.mkdirs()
        }
    }

    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString()
        val categoryPosition = binding.spinnerCategory.selectedItemPosition

        if (amountText.isEmpty() || description.isEmpty() || categoryPosition == -1) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull() ?: 0.0
        categoryViewModel.getCategoriesByUser(userId).observe(this) { categories ->
            if (categories.isNotEmpty() && categoryPosition < categories.size) {
                val category = categories[categoryPosition]
                val expense = Expense(
                    amount = amount,
                    date = selectedDate.timeInMillis,
                    description = description,
                    categoryId = category.id,
                    userId = userId,
                    photoPath = photoPath
                )

                expenseViewModel.insert(expense)
                Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_GALLERY = 2
    }

}