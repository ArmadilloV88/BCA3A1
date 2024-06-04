package com.poe.clocksavvy
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.poe.clocksavvy.MainActivity.Companion.usersList
import java.io.File
import android.widget.Toast
import java.io.FileWriter
import java.io.IOException
import java.util.Locale
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.poe.clocksavvy.MainActivity.Companion.categoriesList
import java.text.SimpleDateFormat
import java.util.Calendar
import android.Manifest
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
class MainActivity : AppCompatActivity() {
    companion object {
        var autoLogin = false
        var userId: String? = null
        var usersList: MutableList<UserInfo> = mutableListOf()
        var contentsList: MutableList<String> = mutableListOf()
        var categoriesList: MutableList<Category> = mutableListOf()
        var timesheetsList: MutableList<Timesheet> = mutableListOf()
    }
    data class Category(
        val categoryId: String,
        val userId: String,
        val categoryName: String,
        val categoryDescription: String,
        val categoryType: String
    )
    data class Timesheet(
        val timesheetId: String,
        val categoryId: String,
        val date: String,
        val startTime: String,
        val endTime: String,
        val description: String
    )
    data class DailyGoal(
        val dailyGoalId: String,
        val maxDailyGoal: Int,
        val minDailyGoal: Int
    )
    data class UserInfo(
        val userId: String,
        val username: String,
        val password: String,
        val firstName: String,
        val lastName: String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.welcome_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("MainActivity", "onCreate() called")
        //deleteAllFiles()
        //Initialize the app and check if initialization succeeds
        val initializationResult = initializeApp()
        Log.d("MainActivity", "Initialization result: $initializationResult")
        Log.d("MainActivity", "AutoLogin: $autoLogin, UserID: $userId")

        // Check if initialization succeeded
        if (initializationResult) {
            // Navigate to the appropriate screen based on initialization result
            if (autoLogin && userId != null) {
                // Auto-login enabled and user ID available, navigate to the main screen
                navigateToMainScreen()
            } else {
                // Auto-login disabled or no user ID available, navigate to the welcome screen
                navigateToWelcomeScreen()
            }
        } else {
            // Initialization failed, display a toast message to the user
            Toast.makeText(this, "Initialization failed. Please try again later.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun initializeApp(): Boolean {
        // Check if all necessary files exist and have been loaded
        val allFilesExist = File(filesDir, "Users").exists() &&
                File(filesDir, "Categories").exists() &&
                File(filesDir, "Timesheets").exists() &&
                File(filesDir, "Contents").exists()

        // If any file doesn't exist, create it
        if (!allFilesExist) {
            createFilesIfNeeded()
        }

        // Load data from files
        loadUsersData()
        loadCategoriesData()
        loadTimesheetsData()
        loadContentsData()

        // Log loaded data for debugging
        Log.d("MainActivity", "Loaded users: $usersList")
        Log.d("MainActivity", "Loaded categories: $categoriesList")
        Log.d("MainActivity", "Loaded timesheets: $timesheetsList")
        Log.d("MainActivity", "Loaded contents: $contentsList")

        // Return true if initialization succeeded
        return allFilesExist
    }
    private fun loadUsersData() {
        val usersFile = File(filesDir, "Users")
        if (usersFile.exists()) {
            usersList = usersFile.readLines().mapNotNull { line ->
                val userData = line.split("+")
                if (userData.size == 5) {
                    UserInfo(
                        userId = userData[0],
                        username = userData[1],
                        password = userData[2],
                        firstName = userData[3],
                        lastName = userData[4]
                    )
                } else {
                    Log.e("MainActivity", "Invalid user data format: $line")
                    null
                }
            }.toMutableList()
            Log.d("MainActivity", "Loaded users data: $usersList")
        } else {
            Log.e("MainActivity", "Users file does not exist")
        }
    }
    private fun loadCategoriesData() {
        val categoriesFile = File(filesDir, "Categories")
        if (categoriesFile.exists()) {
            categoriesList = categoriesFile.readLines().map { line ->
                val categoryData = line.split("+")
                Category(
                    categoryId = categoryData[0],
                    userId = categoryData[1],
                    categoryName = categoryData[2],
                    categoryDescription = categoryData[3],
                    categoryType = categoryData[4]
                )
            }.toMutableList()
            Log.d("MainActivity", "Loaded categories data: $categoriesList")
        } else {
            Log.e("MainActivity", "Categories file does not exist")
        }
    }
    private fun loadTimesheetsData() {
        val timesheetsFile = File(filesDir, "Timesheets")
        if (timesheetsFile.exists()) {
            timesheetsList = timesheetsFile.readLines().map { line ->
                val timesheetData = line.split("+")
                Timesheet(
                    timesheetId = timesheetData[0],
                    categoryId = timesheetData[1],
                    date = timesheetData[2],
                    startTime = timesheetData[3],
                    endTime = timesheetData[4],
                    description = timesheetData[5]
                )
            }.toMutableList()
            Log.d("MainActivity", "Loaded timesheets data: $timesheetsList")
        } else {
            Log.e("MainActivity", "Timesheets file does not exist")
        }
    }

    private fun loadContentsData() {
        val contentsFile = File(filesDir, "Contents")
        if (contentsFile.exists()) {
            contentsList.clear() // Clear the existing contentsList before loading new data
            val lines = contentsFile.readLines()
            for (line in lines) {
                val parts = line.split("+")
                if (parts.size == 6) { // Ensure the line has all required parts
                    val timesheetId = parts[0]
                    val categoryId = parts[2].split("-")[1] // Extracting the category ID from the category part
                    val dailyGoalId = parts[3]
                    val maxDailyGoal = parts[4].toInt()
                    val minDailyGoal = parts[5].toInt()
                    // Add the goal data to the list
                    contentsList.add("$timesheetId+$categoryId+$dailyGoalId+$maxDailyGoal+$minDailyGoal")
                }
            }
            Log.d("GoalsActivity", "Loaded goals data: $contentsList")
        } else {
            Log.e("GoalsActivity", "Contents file does not exist")
        }
    }

    private fun isCategoryBelongsToUser(userId: String, categoryId: String): Boolean {
        return MainActivity.categoriesList.any { it.userId == userId && it.categoryId == categoryId }
    }


    private fun createFilesIfNeeded() {
        val usersFile = File(filesDir, "Users")
        if (!usersFile.exists()) {
            usersFile.createNewFile()
            Log.d("Initializer", "Users file created")
        }

        val categoriesFile = File(filesDir, "Categories")
        if (!categoriesFile.exists()) {
            categoriesFile.createNewFile()
            Log.d("Initializer", "Categories file created")
        }

        val timesheetsFile = File(filesDir, "Timesheets")
        if (!timesheetsFile.exists()) {
            timesheetsFile.createNewFile()
            Log.d("Initializer", "Timesheets file created")
        }

        val contentsFile = File(filesDir, "Contents")
        if (!contentsFile.exists()) {
            contentsFile.createNewFile()
            Log.d("Initializer", "Contents file created")
        }
    }


    private fun navigateToWelcomeScreen() {
        // Use Intent to navigate to the welcome page activity
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        // Finish the current activity to prevent navigating back to it
        finish()
    }

    private fun navigateToMainScreen() {
        // Use Intent to navigate to the main screen activity
        val intent = Intent(this, LoginActivity::class.java) // Replace HomeActivity with the appropriate activity
        startActivity(intent)
        // Finish the current activity to prevent navigating back to it
        finish()
    }

    fun deleteAllFiles() {
        val filesToDelete = arrayOf("Users", "Categories", "Timesheets", "Contents")
        for (fileName in filesToDelete) {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    println("File $fileName deleted successfully.")
                } else {
                    println("Failed to delete file $fileName.")
                }
            } else {
                println("File $fileName does not exist.")
            }
        }
    }


    private fun deleteCategoriesFile() {
        val categoriesFile = File(filesDir, "Categories")
        if (categoriesFile.exists()) {
            val deleted = categoriesFile.delete()
            if (deleted) {
                Log.d("MainActivity", "Categories file deleted successfully")
            } else {
                Log.e("MainActivity", "Failed to delete categories file")
            }
        } else {
            Log.e("MainActivity", "Categories file does not exist")
        }
    }

}//Main Application Activity handling Initializer and offline/online mode
class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        // Find and handle the login and register buttons
        val btnLoginPage = findViewById<Button>(R.id.btnLoginPage)
        val btnRegisterPage = findViewById<Button>(R.id.btnRegisterPage)

        btnLoginPage.setOnClickListener {
            // Handle login button click
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegisterPage.setOnClickListener {
            // Handle register button click
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}//Welcome activity that handles the welcome functionality
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        val emailEditText: EditText = findViewById(R.id.email)
        val firstNameEditText: EditText = findViewById(R.id.firstname)
        val lastNameEditText: EditText = findViewById(R.id.lastname)
        val passwordEditText: EditText = findViewById(R.id.password)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmpassword)
        val signUpButton: Button = findViewById(R.id.signupbutton)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Check if any field is empty
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // Inform the user that all fields are required
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email is valid
            if (!isValidEmail(email)) {
                // Inform the user that the email is invalid
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                // Inform the user that passwords do not match
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Continue with registration process
            // Generate a unique user ID
            val userId = generateUserId()

            // Check if the email is already registered
            if (!isEmailRegistered(email)) {
                // Create a new UserInfo object
                val userInfo = MainActivity.UserInfo(
                    userId.toString(), // Convert userId to String
                    email,
                    password,
                    firstName,
                    lastName
                )
                // Add the new user to the user list
                addUserToUserList(userInfo)

                // Inform the user that registration is successful
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                // Navigate to login page or perform any other action
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity
            } else {
                // Email is already registered, inform the user
                Toast.makeText(this, "Email already registered, please use a different email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun generateUserId(): Int {
        // Find the maximum user ID in the list
        val maxUserId = usersList.map { it.userId.toInt() }.maxOrNull() ?: 0
        // Increment the maximum user ID to get the new user ID
        return maxUserId + 1
    }

    private fun isEmailRegistered(email: String): Boolean {
        // Check if the email is already registered in the system
        return usersList.any { it.username == email }
    }

    private fun addUserToUserList(userInfo: MainActivity.UserInfo) {
        // Add the new user data to the user list
        usersList.add(userInfo)
        saveUsersData() // Save the updated user list to a file for persistence
    }

    private fun saveUsersData() {
        val usersFile = File(filesDir, "Users")
        val userDataLines = usersList.map { "${it.userId}+${it.username}+${it.password}+${it.firstName}+${it.lastName}" }
        try {
            usersFile.bufferedWriter().use { out ->
                userDataLines.forEach { line ->
                    out.write(line)
                    out.newLine()
                }
            }
            Log.d("EditProfileActivity", "User data saved: $userDataLines")
        } catch (e: IOException) {
            Log.e("EditProfileActivity", "Error saving user data: ${e.message}")
        }
    }
}//Register activity that handles the register functionality
class GenerateReportActivity : AppCompatActivity() {

    private lateinit var spinnerTimesheet: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnGenerate: Button
    private lateinit var reportDisplay: TextView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.generate_report)

        // Get the user ID from the intent extras
        userId = intent.getStringExtra("userId")

        // Initialize views
        spinnerTimesheet = findViewById(R.id.spinner_timesheet)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnGenerate = findViewById(R.id.btn_generate)
        reportDisplay = findViewById(R.id.report_display)

        // Populate spinners
        populateSpinners()

        // Button click listener
        btnGenerate.setOnClickListener {
            generateReport()
        }
    }

    private fun populateSpinners() {
        // Timesheets spinner
        val timesheetAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            MainActivity.timesheetsList
                .filter { timesheet ->
                    MainActivity.categoriesList
                        .filter { category -> category.userId == userId }
                        .any { category -> category.categoryId == timesheet.categoryId }
                }
                .map { it.timesheetId }
        )
        timesheetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimesheet.adapter = timesheetAdapter

        // Categories spinner
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            MainActivity.categoriesList.filter { it.userId == userId }.map { it.categoryName }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }

    private fun generateReport() {
        val selectedTimesheetId = spinnerTimesheet.selectedItem.toString()
        val selectedCategoryName = spinnerCategory.selectedItem.toString()

        // Find the selected timesheet and category
        val selectedTimesheet = MainActivity.timesheetsList.find { it.timesheetId == selectedTimesheetId }
        val selectedCategory = MainActivity.categoriesList.find { it.categoryName == selectedCategoryName && it.userId == userId }

        // Generate the report
        val report = StringBuilder()
        report.append("User ID: $userId\n")
        report.append("Timesheet ID: ${selectedTimesheet?.timesheetId}\n")
        report.append("Category Name: ${selectedCategory?.categoryName}\n")
        // Add more details to the report as needed

        // Display the report
        reportDisplay.text = report.toString()
    }
}
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        // Check if auto-login is enabled
        checkAutoLogin()

        // Find views
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val checkboxRememberMe = findViewById<CheckBox>(R.id.checkboxRememberMe)
        val btnForgotPassword = findViewById<Button>(R.id.btnForgotPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textSignUp = findViewById<Button>(R.id.textSignUp)

        // Set click listeners
        btnLogin.setOnClickListener {
            // Handle login button click
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val rememberMe = checkboxRememberMe.isChecked

            // Implement login logic with remember me functionality
            loginUser(email, password, rememberMe)
        }

        btnForgotPassword.setOnClickListener {
            // Handle forgot password button click
            navigateToForgotPasswordPage()
        }

        textSignUp.setOnClickListener {
            // Handle sign up button click
            onSignUpClick(it) // Call the onSignUpClick method with the view
        }
    }

    // Declare the onSignUpClick method
    fun onSignUpClick(view: View) {
        // Handle click event here
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun loginUser(email: String, password: String, rememberMe: Boolean) {
        // Search for the email in the users list
        val user = usersList.find { it.username == email }

        if (user != null) {
            // User found, check if the password matches
            val storedPassword = user.password
            if (password == storedPassword) {
                // Password matches, user logged in successfully
                Log.d("LoginActivity", "User $email logged in successfully")
                // Extract the user ID
                val userId = user.userId.toInt()
                Log.d("LoginActivity", "User ID: $userId")
                // Pass user ID to DashboardActivity
                navigateToDashboard(userId)
                finish() // Finish the LoginActivity

                // Save login credentials if "Remember me" is checked
                if (rememberMe && email.isNotBlank() && password.isNotBlank()) {
                    saveLoginCredentials(email, password)
                }
            } else {
                // Password does not match
                Log.d("LoginActivity", "Incorrect password for user $email")
                // Inform the user that the password is incorrect
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        } else {
            // User not found
            Log.d("LoginActivity", "User $email not found")
            // Inform the user that the username is not registered
            Toast.makeText(this, "Username not registered", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDashboard(userId: Int) {
        Log.d("LoginActivity", "Navigating to DashboardActivity with user ID: $userId")
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("userId", userId.toString()) // Convert userId to String
        startActivity(intent)
        finish()
    }

    private fun saveLoginCredentials(email: String, password: String) {
        val rememberMeFile = File(filesDir, "RememberMe.txt")
        try {
            rememberMeFile.writeText("True:$email|$password\n")
            Log.d("LoginActivity", "Login credentials saved for user $email")
        } catch (e: IOException) {
            Log.e("LoginActivity", "Error saving login credentials: ${e.message}")
        }
    }

    private fun navigateToForgotPasswordPage() {
        // Redirect to the forgot password page
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun checkAutoLogin() {
        val rememberMeFile = File(filesDir, "RememberMe.txt")
        if (rememberMeFile.exists()) {
            val content = rememberMeFile.readText().trim()
            if (content.startsWith("True")) {
                val userData = content.substringAfter(":").trim().split("|")
                if (userData.size == 2) {
                    val email = userData[0]
                    val password = userData[1]
                    // Attempt to login with the saved credentials
                    loginUser(email, password, true)
                }
            }
        }
    }
}//Login activity that handles the login functionality
class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_password_page)

        // Find views
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextNewPassword = findViewById<EditText>(R.id.editTextNewPassword)
        val editTextConfirmNewPassword = findViewById<EditText>(R.id.editTextConfirmNewPassword)
        val btnConfirmChangePassword = findViewById<Button>(R.id.btnConfirmChangePassword)

        // Set click listener for Confirm Change Password button
        btnConfirmChangePassword.setOnClickListener {
            // Handle button click
            val email = editTextEmail.text.toString()
            val newPassword = editTextNewPassword.text.toString()
            val confirmNewPassword = editTextConfirmNewPassword.text.toString()

            // Check if any field is empty
            if (email.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Change password logic
            changePassword(email, newPassword)
        }
    }

    private fun changePassword(email: String, newPassword: String) {
        // Find the index of the user by email
        val userIndex = usersList.indexOfFirst { it.username == email }

        if (userIndex != -1) {
            // Update the password for the user in the list
            val user = usersList[userIndex]
            val updatedUser = user.copy(password = newPassword)
            usersList[userIndex] = updatedUser

            // Save the updated user list to the file
            saveUsersData()

            // Inform the user that the password has been changed
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

            // Add any additional actions here, such as navigating to another activity
        } else {
            // User not found
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveUsersData() {
        val usersFile = File(filesDir, "Users")
        val userDataLines = usersList.map { "${it.userId}|${it.username}|${it.password}" }
        try {
            usersFile.bufferedWriter().use { out ->
                userDataLines.forEach { line ->
                    out.write(line)
                    out.newLine()
                }
            }
            Log.d("ForgotPasswordActivity", "User data saved: $userDataLines")
        } catch (e: IOException) {
            Log.e("ForgotPasswordActivity", "Error saving user data: ${e.message}")
        }
    }
}//Forget password Application Activity that handles the forget password functionality
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val userId = intent.getStringExtra("userId")
        Log.d("DashboardActivity", "Received User ID: $userId")

        if (userId != null) {
            // User ID is successfully retrieved, proceed with your logic
            setCardViewListeners()
        } else {
            // Handle case where user ID is not passed correctly
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setCardViewListeners() {
        val dashboardItems = findViewById<GridLayout>(R.id.dashboardItems)
        for (i in 0 until dashboardItems.childCount) {
            val cardView = dashboardItems.getChildAt(i) as CardView
            when (cardView.id) {
                R.id.home_card -> cardView.setOnClickListener { handleHomeClick() }
                R.id.categories_card -> cardView.setOnClickListener { handleCategoriesClick() }
                R.id.timesheets_card -> cardView.setOnClickListener { handleTimesheetsClick() }
                R.id.goals_card -> cardView.setOnClickListener { handleGoalsClick() }
                R.id.profile_card -> cardView.setOnClickListener { handleProfileClick() }
                R.id.statistics_card -> cardView.setOnClickListener { handleStatisticsClick() }
                else -> {
                    // Handle other cases if needed
                }
            }
        }
    }

    private fun handleHomeClick() {
        // Get the user ID from the intent extras
        val userId = intent.getStringExtra("userId")

        // Create an intent to launch the GenerateReportActivity
        val intent = Intent(this, GenerateReportActivity::class.java)

        // Pass the user ID to the GenerateReportActivity
        intent.putExtra("userId", userId)

        // Start the activity
        startActivity(intent)
    }


    private fun handleCategoriesClick() {
        val userId = intent.getStringExtra("userId")
        Log.d("DashboardActivity", "User ID for Categories: $userId")
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
    private fun handleTimesheetsClick() {
        // Handle timesheets button click
        val userId = intent.getStringExtra("userId") // Retrieve the User ID from the intent
        Log.d("DashboardActivity", "User ID for Timesheets: $userId") // Log the User ID
        if (userId != null) {
            val intent = Intent(this, TimesheetsActivity::class.java)
            intent.putExtra("userId", userId) // Pass the User ID to TimesheetsActivity
            startActivity(intent)
        } else {
            // Handle case where user ID is not available
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleGoalsClick() {
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, GoalsActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleProfileClick() {
        // Handle profile button click
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun handleStatisticsClick() {
        // Handle statistics button click
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }
}//Dashboard activity that handles the dashboard integration functionality
class TimesheetsActivity : AppCompatActivity() {
    private lateinit var userId: String // User ID obtained from intent or session
    private lateinit var timesheets: MutableList<MainActivity.Timesheet>
    private lateinit var noTimesheetsTextView: TextView
    private lateinit var timesheetsRecyclerView: RecyclerView
    private lateinit var categoriesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheets)

        // Retrieve user ID from intent or session
        userId = intent.getStringExtra("userId") ?: ""
        if (userId.isEmpty()) {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val addTimesheetButton: Button = findViewById(R.id.addTimesheetButton)
        addTimesheetButton.setOnClickListener {
            // Handle add timesheet button click
            navigateToCategoriesSelection()
        }

        noTimesheetsTextView = findViewById(R.id.noTimesheetsTextView)
        timesheetsRecyclerView = findViewById(R.id.timesheetsRecyclerView)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)

        loadTimesheets()
    }

    override fun onResume() {
        super.onResume()
        loadTimesheets()
    }

    private fun loadTimesheets() {
        timesheets = filterTimesheetsByUserId(userId).toMutableList()
        if (timesheets.isNotEmpty()) {
            showTimesheets()
            noTimesheetsTextView.visibility = View.GONE
        } else {
            showNoTimesheetsMessage()
        }
    }

    private fun showTimesheets() {
        timesheetsRecyclerView.visibility = View.VISIBLE
        categoriesRecyclerView.visibility = View.GONE

        // Initialize TimesheetAdapter with correct arguments
        val timesheetsAdapter = TimesheetAdapter(userId, timesheets,
            { timesheet -> editTimesheet(timesheet) },
            { timesheet -> deleteTimesheet(timesheet) })

        timesheetsRecyclerView.layoutManager = LinearLayoutManager(this)
        timesheetsRecyclerView.adapter = timesheetsAdapter
    }

    private fun showNoTimesheetsMessage() {
        noTimesheetsTextView.visibility = View.VISIBLE
        timesheetsRecyclerView.visibility = View.GONE
        categoriesRecyclerView.visibility = View.GONE
    }

    private fun filterTimesheetsByUserId(userId: String): List<MainActivity.Timesheet> {
        return MainActivity.timesheetsList.filter { timesheet ->
            MainActivity.categoriesList.any { category -> category.userId == userId && category.categoryId == timesheet.categoryId }
        }
    }

    private fun navigateToCategoriesSelection() {
        val intent = Intent(this, CategoriesSelectionActivity::class.java)
        intent.putExtra("userId", userId) // Assuming userId is obtained correctly
        startActivityForResult(intent, REQUEST_SELECT_CATEGORY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_CATEGORY && resultCode == Activity.RESULT_OK) {
            val selectedCategoryId = data?.getStringExtra("categoryId")
            if (selectedCategoryId != null) {
                // Filter timesheets based on selected category ID
                filterTimesheetsByCategoryId(selectedCategoryId)
            }
        }
    }

    private fun filterTimesheetsByCategoryId(categoryId: String) {
        timesheets = MainActivity.timesheetsList.filter { it.categoryId == categoryId }.toMutableList()
        if (timesheets.isNotEmpty()) {
            showTimesheets()
            noTimesheetsTextView.visibility = View.GONE
        } else {
            showNoTimesheetsMessage()
        }
    }

    private fun editTimesheet(timesheet: MainActivity.Timesheet) {
        // Launch ViewTimesheetActivity and pass the timesheet ID
        val intent = Intent(this, ViewTimesheetActivity::class.java)
        intent.putExtra("timesheetId", timesheet.timesheetId)
        startActivity(intent)
    }

    private fun deleteTimesheet(timesheet: MainActivity.Timesheet) {
        // Remove the timesheet from the list in memory
        val removedTimesheet = timesheets.find { it.timesheetId == timesheet.timesheetId }
        timesheets.removeAll { it.timesheetId == timesheet.timesheetId }

        // Delete timesheet from file
        deleteTimesheetFromFile(timesheet)

        // Update the timesheets data file
        saveTimesheetsData()

        // Notify the adapter of the change
        timesheetsRecyclerView.adapter?.notifyDataSetChanged()

        // Show a message to indicate successful deletion
        val message = "Timesheet deleted successfully"
        Toast.makeText(this@TimesheetsActivity, message, Toast.LENGTH_SHORT).show()

        Log.d("TimesheetsActivity", "Deleted timesheet: $removedTimesheet")
    }
    private fun saveTimesheetsData() {
        val fileName = "timesheets_data.txt"
        val file = File(filesDir, fileName)

        // Convert timesheets list to string format
        val timesheetsString = MainActivity.timesheetsList.joinToString("\n") { timesheet ->
            "${timesheet.timesheetId}+${timesheet.categoryId}+${timesheet.date}+${timesheet.startTime}+${timesheet.endTime}+${timesheet.description}"
        }

        // Write the timesheets data to the file
        file.writeText(timesheetsString)
    }

    private fun deleteTimesheetFromFile(timesheet: MainActivity.Timesheet) {
        val fileName = "timesheets_data.txt"
        val file = File(filesDir, fileName)

        if (!file.exists()) {
            // File does not exist, nothing to delete
            return
        }

        val timesheetString = "${timesheet.timesheetId}+${timesheet.categoryId}+${timesheet.date}+${timesheet.startTime}+${timesheet.endTime}+${timesheet.description}"
        val content = file.readText()

        // Remove the line corresponding to the timesheet entry
        val updatedContent = content.lines().filter { it != timesheetString }.joinToString("\n")

        // Write the modified content back to the file
        file.writeText(updatedContent)
    }

    companion object {
        private const val REQUEST_SELECT_CATEGORY = 123
    }
}
class CategoriesSelectionActivity : AppCompatActivity() {
    private lateinit var userId: String // User ID obtained from intent or session
    private lateinit var categories: List<MainActivity.Category>
    private lateinit var categoriesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_selection)

        // Retrieve user ID from intent or session
        userId = intent.getStringExtra("userId") ?: ""
        if (userId.isEmpty()) {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load categories
        loadCategories()
    }

    private fun loadCategories() {
        // Filter categories specific to the user ID
        categories = MainActivity.categoriesList.filter { it.userId == userId }.toMutableList()

        // Set up click listener for category selection
        val categoryAdapter = CategoryAdapter(categories) { categoryId ->
            handleCategorySelection(categoryId)
        }
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun handleCategorySelection(categoryId: String) {
        // Start AddTimesheetActivity and pass the selected category ID
        val intent = Intent(this, AddTimesheetActivity::class.java)
        intent.putExtra("categoryId", categoryId)
        startActivity(intent)
    }

    // Inner class for CategoryAdapter
    inner class CategoryAdapter(
        private val categories: List<MainActivity.Category>,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_simple, parent, false)
            return CategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val category = categories[position]
            holder.bind(category)
        }

        override fun getItemCount(): Int {
            return categories.size
        }

        inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
            private val selectButton: Button = itemView.findViewById(R.id.selectButton)

            fun bind(category: MainActivity.Category) {
                categoryNameTextView.text = category.categoryName

                // Set click listener for select button
                selectButton.setOnClickListener {
                    onItemClick(category.categoryId)
                }
            }
        }
    }
}
class AddTimesheetActivity : AppCompatActivity() {

    private lateinit var categoryId: String // Assuming you pass the categoryId from previous activity
    private lateinit var dateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var imageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_timesheet)

        // Retrieve categoryId from intent extras
        categoryId = intent.getStringExtra("categoryId") ?: ""

        dateEditText = findViewById(R.id.dateEditText)
        startTimeEditText = findViewById(R.id.startTimeEditText)
        endTimeEditText = findViewById(R.id.endTimeEditText)

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveTimesheet()
        }

        // Set click listeners for date and time EditTexts
        dateEditText.setOnClickListener {
            showDatePicker()
        }

        startTimeEditText.setOnClickListener {
            showTimePicker(startTimeEditText)
        }

        endTimeEditText.setOnClickListener {
            showTimePicker(endTimeEditText)
        }

        imageView = findViewById(R.id.imageView)

        val pickImageButton = findViewById<Button>(R.id.btnPickImage)
        pickImageButton.setOnClickListener {
            openGallery()
        }

    }

    private fun saveTimesheet() {
        val timesheetId = generateTimesheetId() // Generate unique timesheetId
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)

        // Get input values
        val date = dateEditText.text.toString()
        val startTime = startTimeEditText.text.toString()
        val endTime = endTimeEditText.text.toString()
        val description = descriptionEditText.text.toString()

        // Create timesheet string
        val timesheetData = "$timesheetId+$categoryId+$date+$startTime+$endTime+$description\n"

        // Save timesheet to the file
        val timesheetsFile = File(filesDir, "Timesheets")
        timesheetsFile.appendText(timesheetData)

        // Add timesheet to the generic list
        val timesheet = MainActivity.Timesheet(timesheetId, categoryId, date, startTime, endTime, description)
        MainActivity.timesheetsList.add(timesheet)

        // Finish activity and return to previous activity
        finish()
    }
    //opens gallery for photo selection
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val PERMISSIONS_REQUEST_READ_STORAGE = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            imageView.setImageURI(selectedImageUri)
        }
    }

    private fun checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_READ_STORAGE)
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateTimesheetId(): String {
        // Generate unique timesheetId based on existing timesheets
        val existingIds = MainActivity.timesheetsList.map { it.timesheetId.toInt() }
        val newId = existingIds.maxOrNull()?.plus(1) ?: 1
        return newId.toString()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                dateEditText.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth))
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }
}//Timesheets activity add on that handles adding the entries the file system//Timesheets activity add on that handles adding the entries the file system
class TimesheetAdapter(
    private val userId: String,
    private val timesheets: List<MainActivity.Timesheet>,
    private val onEditClick: (MainActivity.Timesheet) -> Unit,
    private val onDeleteClick: (MainActivity.Timesheet) -> Unit
) : RecyclerView.Adapter<TimesheetAdapter.TimesheetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timesheet, parent, false)
        return TimesheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimesheetViewHolder, position: Int) {
        val timesheet = timesheets[position]
        holder.bind(timesheet)
        holder.editButton.setOnClickListener {
            onEditClick(timesheet)
        }
        holder.deleteButton.setOnClickListener {
            onDeleteClick(timesheet)
        }
    }

    override fun getItemCount(): Int {
        return timesheets.size
    }

    inner class TimesheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timesheetDescriptionTextView: TextView = itemView.findViewById(R.id.timesheetDescriptionTextView)
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(timesheet: MainActivity.Timesheet) {
            val category = getCategoryFromCategoryId(timesheet.categoryId)
            categoryNameTextView.text = category?.categoryName ?: "Unknown Category"
            timesheetDescriptionTextView.text = timesheet.description
        }

        private fun getCategoryFromCategoryId(categoryId: String): MainActivity.Category? {
            return MainActivity.categoriesList.find { it.categoryId == categoryId && it.userId == userId }
        }
    }
}// Timesheets adapter that allows the timesheets to be displayed
class GoalsActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var timesheetsList: List<MainActivity.Timesheet>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        // Retrieve user ID from intent
        userId = intent.getStringExtra("userId") ?: ""
        Log.d("GoalsActivity", "User ID: $userId")

        // Load timesheets for the user
        loadTimesheetsForUser(userId)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.goalsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Display the loaded timesheets
        displayTimesheets()
    }

    private fun loadTimesheetsForUser(userId: String) {
        // Step 1: Filter categories by user ID
        val userCategories = MainActivity.categoriesList.filter { it.userId == userId }

        // Step 2: Extract category IDs
        val categoryIds = userCategories.map { it.categoryId }

        // Step 3: Filter timesheets by category IDs
        timesheetsList = MainActivity.timesheetsList.filter { timesheet ->
            categoryIds.contains(timesheet.categoryId)
        }
        Log.d("GoalsActivity", "Loaded timesheets: $timesheetsList")
    }

    private fun displayTimesheets() {
        if (timesheetsList.isEmpty()) {
            // Display a message to prompt users to add a timesheet
            val message = "No timesheets available. Please add a timesheet."
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            Log.d("GoalsActivity", "No timesheets available.")
        } else {
            // Initialize and set up the adapter
            adapter = GoalsAdapter(timesheetsList) { timesheet -> onTimesheetSelected(timesheet) }
            recyclerView.adapter = adapter
            Log.d("GoalsActivity", "Timesheets displayed.")
        }
    }

    private fun onTimesheetSelected(timesheet: MainActivity.Timesheet) {
        // Handle the selection of a timesheet
        // Navigate to the ViewTimesheetActivity and pass the timesheet ID
        val intent = Intent(this, ViewTimesheetActivity::class.java)
        intent.putExtra("timesheetId", timesheet.timesheetId)
        startActivity(intent)
        Log.d("GoalsActivity", "Selected timesheet: ${timesheet.timesheetId}")
    }
}
class GoalsAdapter(
    private val timesheetsList: List<MainActivity.Timesheet>,
    private val onViewClick: (MainActivity.Timesheet) -> Unit
) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timesheet_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val timesheet = timesheetsList[position]
        holder.bind(timesheet)
        holder.viewButton.setOnClickListener {
            onViewClick(timesheet)
        }
    }

    override fun getItemCount(): Int {
        return timesheetsList.size
    }

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val viewButton: Button = itemView.findViewById(R.id.viewButton)

        fun bind(timesheet: MainActivity.Timesheet) {
            descriptionTextView.text = "Description: ${timesheet.description}"
        }
    }
}
class ViewTimesheetActivity : AppCompatActivity() {
    private lateinit var timesheetId: String
    private lateinit var dailyGoalEditText: EditText
    private lateinit var maxDailyGoalEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var recyclerViewGoals: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timesheet)

        timesheetId = intent.getStringExtra("timesheetId") ?: ""
        val timesheet = MainActivity.timesheetsList.find { it.timesheetId == timesheetId }

        if (timesheet != null) {
            // Update UI elements with timesheet details
            findViewById<TextView>(R.id.textViewTimesheetId).text = "Timesheet ID: ${timesheet.timesheetId}"
            findViewById<TextView>(R.id.textViewCategory).text = "Category: ${getCategoryName(timesheet.categoryId)}"

            // Set up editable fields
            dailyGoalEditText = findViewById(R.id.editTextDailyGoal)
            maxDailyGoalEditText = findViewById(R.id.editTextMaxDailyGoal)
            dateEditText = findViewById(R.id.editTextDate)
            startTimeEditText = findViewById(R.id.editTextStartTime)
            endTimeEditText = findViewById(R.id.editTextEndTime)
            descriptionEditText = findViewById(R.id.editTextDescription)

            val dailyGoalInfo = getDailyGoalInfoForTimesheet(timesheetId)
            dailyGoalEditText.setText(dailyGoalInfo.first.toString())
            maxDailyGoalEditText.setText(dailyGoalInfo.second.toString())
            dateEditText.setText(timesheet.date)
            startTimeEditText.setText(timesheet.startTime)
            endTimeEditText.setText(timesheet.endTime)
            descriptionEditText.setText(timesheet.description)

            // Set up onClick listeners for date and time fields
            setDateTimeFieldListeners()

            // Load and display goals associated with the timesheet
            recyclerViewGoals = findViewById(R.id.recyclerViewGoals)
            recyclerViewGoals.layoutManager = LinearLayoutManager(this)
            recyclerViewGoals.adapter = GoalsAdapter(getGoalsForTimesheet(timesheetId)) { }

            // Set up click listeners for buttons
            findViewById<Button>(R.id.buttonDelete).setOnClickListener { deleteTimesheet(timesheet) }
            findViewById<Button>(R.id.buttonSave).setOnClickListener { saveTimesheetChanges(timesheet) }
        } else {
            // Handle case where timesheet with the provided ID was not found
        }
    }

    private fun getCategoryName(categoryId: String): String {
        val category = MainActivity.categoriesList.find { it.categoryId == categoryId }
        return category?.categoryName ?: "Unknown"
    }

    private fun getDailyGoalInfoForTimesheet(timesheetId: String): Pair<Int, Int> {
        val content = MainActivity.contentsList.find { it.startsWith("$timesheetId+") }
        if (content != null) {
            val parts = content.split("+")
            val dailyGoalId = parts[3].toInt()
            val maxDailyGoal = parts[4].toInt()
            val minDailyGoal = parts[5].toInt()
            return Pair(maxDailyGoal, minDailyGoal)
        }
        return Pair(0, 0) // Return default values if no daily goal info found
    }

    private fun getGoalsForTimesheet(timesheetId: String): List<MainActivity.Timesheet> {
        return MainActivity.contentsList.filter { it.startsWith("$timesheetId+") }.map {
            val parts = it.split("+")
            MainActivity.Timesheet(
                timesheetId = parts[0],
                categoryId = parts[1],
                date = parts[2],
                startTime = parts[3],
                endTime = parts[4],
                description = parts[5]
            )
        }
    }

    private fun deleteTimesheet(timesheet: MainActivity.Timesheet) {
        // Remove the timesheet from the timesheetsList
        MainActivity.timesheetsList.remove(timesheet)
        // Update the Timesheets file
        updateTimesheetsFile()

        // Notify the user about the deletion
        Toast.makeText(this, "Timesheet deleted", Toast.LENGTH_SHORT).show()
        // Finish the activity and return to the dashboard
        finish()
    }

    private fun updateTimesheetsFile() {
        val timesheetsFile = File(filesDir, "Timesheets")
        if (timesheetsFile.exists()) {
            // Write the updated timesheets data back to the file
            timesheetsFile.bufferedWriter().use { out ->
                MainActivity.timesheetsList.forEach { timesheet ->
                    out.write("${timesheet.timesheetId}+${timesheet.categoryId}+${timesheet.date}+${timesheet.startTime}+${timesheet.endTime}+${timesheet.description}\n")
                }
            }
            Log.d("MainActivity", "Timesheets file updated successfully")
        } else {
            Log.e("MainActivity", "Timesheets file does not exist")
        }
    }

    private fun saveTimesheetChanges(timesheet: MainActivity.Timesheet) {
        // Update the Timesheets file
        updateTimesheetsFile()

        // Finish the activity and return to the dashboard
        finish()
    }

    private fun setDateTimeFieldListeners() {
        // Set up onClick listeners for date and time fields
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Display selected date in the EditText
            val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, monthOfYear + 1, year)
            dateEditText.setText(formattedDate)
        }

        dateEditText.setOnClickListener {
            // Show DatePickerDialog when date EditText is clicked
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // Display selected time in the EditText
            val formattedTime = "%02d:%02d".format(hourOfDay, minute)
            if (startTimeEditText.isFocused) {
                startTimeEditText.setText(formattedTime)
            } else if (endTimeEditText.isFocused) {
                endTimeEditText.setText(formattedTime)
            }
        }

        startTimeEditText.setOnClickListener {
            // Show TimePickerDialog when start time EditText is clicked
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                timePickerListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }

        endTimeEditText.setOnClickListener {
            // Show TimePickerDialog when end time EditText is clicked
            val calendar = Calendar.getInstance()
            val timePickerDialog = TimePickerDialog(
                this,
                timePickerListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }
    }
}
class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Example data
        val data = listOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50)

        // Get the max value for scaling
        val maxValue = data.maxOrNull() ?: 50  // Avoid division by zero
        val scaleFactor = 300.0 / maxValue  // Assuming 300dp is the max height of your container

        // Find the container in the layout
        val barsContainer = findViewById<LinearLayout>(R.id.bars_container)

        // Clear existing views if any
        barsContainer.removeAllViews()

        // Dynamically create bars based on data
        for (value in data) {
            val barHeight = (value * scaleFactor).toInt()  // Convert to Int, as layout params require it

            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    barHeight,  // Directly use barHeight for the height of the bar
                    1f
                ).also {
                    it.setMargins(5, 0, 5, 0)  // Apply uniform margins, no need to adjust bottom margin
                }
                setBackgroundColor(Color.YELLOW)
            }

            barsContainer.addView(bar)
        }

        setupYAxis()

        // Setup the done button
        setupDoneButton()
    }

    private fun setupYAxis() {
        val yAxisContainer = findViewById<LinearLayout>(R.id.y_axis)
        yAxisContainer.removeAllViews()  // Clear existing labels if any

        // Generate a list from 0 to 50 with steps of 5
        val intervals = (0..50 step 5).toList().reversed()  // Reverse the order of intervals

        for (interval in intervals) {
            val label = TextView(this).apply {
                text = interval.toString()
                textSize = 12f  // Set text size
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).also {
                    it.gravity = Gravity.RIGHT  // Align text to the right
                }
            }
            yAxisContainer.addView(label)
        }
    }

    private fun setupDoneButton() {
        val doneButton = findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener {
            finish()  // Finish this activity to go back to the previous DashboardActivity in the stack
        }
    }
}
class CategoriesActivity : AppCompatActivity() {
    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var userId: String // User ID obtained from intent or session
    private lateinit var categories: MutableList<MainActivity.Category>
    private lateinit var noCategoriesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Retrieve user ID from intent or session
        userId = intent.getStringExtra("userId") ?: ""
        if (userId.isEmpty()) {
            // Handle invalid user ID
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            // Handle add category button click
            navigateToAddCategory()
        }

        noCategoriesTextView = findViewById(R.id.noCategoriesTextView)

        loadCategories()
    }

    override fun onResume() {
        super.onResume()
        Log.d("CategoriesActivity", "onResume called")
        loadCategories()
    }

    private fun loadCategories() {
        categories = filterCategoriesByUserId(userId).toMutableList()
        if (categories.isNotEmpty()) {
            // Display categories in the RecyclerView
            showCategories()
            // Hide the "No categories available" message
            noCategoriesTextView.visibility = View.GONE
        } else {
            // Show a message indicating no categories found
            showNoCategoriesMessage()
        }

        Log.d("CategoriesActivity", "Loaded categories: $categories")
    }

    private fun showCategories() {
        val recyclerView: RecyclerView = findViewById(R.id.categoriesRecyclerView)
        recyclerView.visibility = View.VISIBLE

        // Initialize CategoryAdapter with correct arguments
        categoriesAdapter = CategoryAdapter(categories) { categoryId ->
            deleteCategory(categoryId)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = categoriesAdapter
    }

    private fun showNoCategoriesMessage() {
        noCategoriesTextView.visibility = View.VISIBLE
    }

    private fun filterCategoriesByUserId(userId: String): List<MainActivity.Category> {
        // Load categories data from the file and filter by user ID
        val filteredCategories = MainActivity.categoriesList.filter { it.userId == userId }
        Log.d("CategoriesActivity", "Filtered categories by user ID ($userId): $filteredCategories")
        return filteredCategories
    }
    private fun navigateToAddCategory() {
        // Navigate to AddCategoryActivity to add a new category
        val intent = Intent(this, AddCategoryActivity::class.java)
        intent.putExtra("userId", userId) // Pass the userId along with the intent
        startActivity(intent)
    }

    // Function to delete a category
    private fun deleteCategory(categoryId: String) {
        // Remove the category from the list in memory
        val removedCategory = categories.find { it.categoryId == categoryId }
        categories.removeAll { it.categoryId == categoryId }

        // Update the categories data file
        saveCategoriesData()

        // Notify the adapter of the change
        categoriesAdapter.notifyDataSetChanged()

        // Show a message to indicate successful deletion
        Toast.makeText(this, "Category deleted successfully", Toast.LENGTH_SHORT).show()

        Log.d("CategoriesActivity", "Deleted category: $removedCategory")
    }

    private fun saveCategoriesData() {
        val categoriesFile = File(filesDir, "Categories")
        val categoryDataLines = categories.map { "${it.categoryId}+${it.userId}+${it.categoryName}+${it.categoryDescription}+${it.categoryType}" }
        try {
            categoriesFile.bufferedWriter().use { out ->
                categoryDataLines.forEach { line ->
                    out.write(line)
                    out.newLine()
                }
            }
            Log.d("CategoriesActivity", "Categories data saved: $categoryDataLines")
        } catch (e: IOException) {
            Log.e("CategoriesActivity", "Error saving categories data: ${e.message}")
        }
    }

    // Inner class for CategoryAdapter
    class CategoryAdapter(
        private val categories: List<MainActivity.Category>,
        private val onDeleteCategory: (String) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
            return CategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val category = categories[position]
            holder.bind(category)
        }

        override fun getItemCount(): Int {
            return categories.size
        }

        inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
            private val deleteButton: Button = itemView.findViewById(R.id.deleteCategoryButton)

            fun bind(category: MainActivity.Category) {
                categoryNameTextView.text = category.categoryName

                // Set click listener for delete button
                deleteButton.setOnClickListener {
                    val categoryId = categories[adapterPosition].categoryId
                    onDeleteCategory(categoryId)
                }
            }
        }
    }
}
class AddCategoryActivity : AppCompatActivity() {
    private lateinit var dateEditText: EditText
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            // Handle null or empty user ID
            Toast.makeText(this, "User ID is null or empty", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setContentView(R.layout.activity_add_category)

        dateEditText = findViewById(R.id.dateEditText)

        // Handle Date EditText click to show DatePickerDialog
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle Save button click
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            // Retrieve category information
            val categoryNameEditText: EditText = findViewById(R.id.categoryNameEditText)
            val categoryDescriptionEditText: EditText = findViewById(R.id.categoryDescriptionEditText)

            val categoryName = categoryNameEditText.text.toString()
            val categoryDescription = categoryDescriptionEditText.text.toString()
            val date = dateEditText.text.toString()

            // Check if any field is empty
            if (categoryName.isEmpty() || categoryDescription.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate a unique category ID
            val categoryId = generateUniqueId()

            // Save category data
            val categoryData = "$categoryId+$userId+$categoryName+$categoryDescription+$date"
            saveCategory(categoryData)

            // Redirect back to DashboardActivity
            navigateToDashboard()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                dateEditText.setText(sdf.format(selectedDate.time))
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun generateUniqueId(): String {
        val categoriesList = MainActivity.categoriesList
        // If no categories exist yet, start with ID 1
        if (categoriesList.isEmpty()) return "1"
        // Find the highest existing category ID and increment by 1
        val highestId = categoriesList.maxByOrNull { it.categoryId.toIntOrNull() ?: 0 }?.categoryId?.toIntOrNull() ?: 0
        return (highestId + 1).toString()
    }

    private fun saveCategory(categoryData: String) {
        try {
            val categoriesFile = File(filesDir, "Categories")
            // Ensure categoryData has the correct format
            if (categoryData.count { it == '+' } != 4) {
                Log.e("AddCategoryActivity", "Error saving category data: Invalid format")
                return
            }
            // Open a FileWriter in append mode to add category data to the file
            FileWriter(categoriesFile, true).use { writer ->
                writer.write("$categoryData\n") // Add a new line to separate categories
            }
            // Parse and add the new category to the list
            val newCategory = parseCategory(categoryData)
            MainActivity.categoriesList.add(newCategory)
            Log.d("AddCategoryActivity", "Category data saved successfully: $categoryData")
            // Inform the user about successful category addition
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("AddCategoryActivity", "Error saving category data: ${e.message}")
            e.printStackTrace()
            // Inform the user about the error
            Toast.makeText(this, "Failed to add category. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseCategory(categoryData: String): MainActivity.Category {
        val categoryFields = categoryData.split("+")
        return MainActivity.Category(
            categoryId = categoryFields.getOrElse(0) { "" },
            userId = categoryFields.getOrElse(1) { "" },
            categoryName = categoryFields.getOrElse(2) { "" },
            categoryDescription = categoryFields.getOrElse(3) { "" },
            categoryType = categoryFields.getOrElse(4) { "" }
        )
    }

    private fun navigateToDashboard() {
        // Retrieve the user ID from the intent
        val userId = intent.getStringExtra("userId")

        // Navigate to DashboardActivity with the user ID
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)

        // Finish this activity to prevent returning to it on back press
        finish()
    }

}
class EditCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        // Retrieve categoryId from intent extras
        val categoryId = intent.getStringExtra("categoryId")
        if (categoryId.isNullOrEmpty()) {
            // Handle invalid categoryId
            Toast.makeText(this, "Invalid category ID", Toast.LENGTH_SHORT).show()
            finish() // Close the activity
            return
        }

        // Initialize views
        val categoryNameEditText: EditText = findViewById(R.id.categoryNameEditText)
        val categoryDescriptionEditText: EditText = findViewById(R.id.categoryDescriptionEditText)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Load category data based on categoryId
        val category = getCategoryById(categoryId)

        // Populate EditTexts with category data
        categoryNameEditText.setText(category?.categoryName)
        categoryDescriptionEditText.setText(category?.categoryDescription)

        // Handle Save button click
        saveButton.setOnClickListener {
            // Retrieve updated category information
            val updatedName = categoryNameEditText.text.toString()
            val updatedDescription = categoryDescriptionEditText.text.toString()

            // Validate input
            if (updatedName.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update category
            val success = updateCategory(categoryId, updatedName, updatedDescription)

            // Check if the update was successful
            if (success) {
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Failed to update category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to load category data based on categoryId
    private fun getCategoryById(categoryId: String): MainActivity.Category? {
        // Find the category with the given categoryId
        return categoriesList.find { it.categoryId == categoryId }
    }

    private fun updateCategory(categoryId: String, name: String, description: String): Boolean {
        // Find the index of the category with the given categoryId
        val index = categoriesList.indexOfFirst { it.categoryId == categoryId }
        // Check if the category exists
        if (index != -1) {
            // Update the category's name and description
            categoriesList[index] = categoriesList[index].copy(categoryName = name, categoryDescription = description)

            // Save the updated list of categories to the file
            saveCategoriesToFile()

            // Return true to indicate a successful update
            return true
        }

        // Return false if the category was not found
        return false
    }

    private fun saveCategoriesToFile() {
        try {
            FileWriter(File(filesDir, "Categories"), false).use { writer ->
                for (category in categoriesList) {
                    val categoryData = "${category.categoryId}+${category.userId}+${category.categoryName}+${category.categoryDescription}+${category.categoryType}"
                    writer.write("$categoryData\n")
                }
            }
            Log.d("CategoryAdapter", "Categories saved successfully")
        } catch (e: IOException) {
            Log.e("CategoryAdapter", "Error saving categories: ${e.message}")
            e.printStackTrace()
        }
    }
}
class EditProfileActivity : AppCompatActivity() {
    private lateinit var user: MainActivity.UserInfo
    private lateinit var usersList: MutableList<MainActivity.UserInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Get user ID from intent
        val userId = intent.getStringExtra("userId") ?: return

        // Initialize usersList and load user data
        initUsersList()

        // Find the user by ID
        user = usersList.find { it.userId == userId } ?: run {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Find EditText fields
        val editUsername: EditText = findViewById(R.id.editUsername)
        val editPassword: EditText = findViewById(R.id.editPassword)
        val editFirstName: EditText = findViewById(R.id.editFirstName)
        val editLastName: EditText = findViewById(R.id.editLastName)

        // Populate EditText fields with user data
        editUsername.setText(user.username)
        editPassword.setText(user.password)
        editFirstName.setText(user.firstName)
        editLastName.setText(user.lastName)

        // Set up onClickListeners for buttons (save, delete, logout)
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveUserData(
                editUsername.text.toString(),
                editPassword.text.toString(),
                editFirstName.text.toString(),
                editLastName.text.toString()
            )
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            deleteUser()
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            logoutUser()
        }
    }

    private fun initUsersList() {
        usersList = mutableListOf()
        try {
            val file = File(filesDir, "users.txt")
            if (file.exists()) {
                file.readLines().forEach { line ->
                    val userData = line.split("+")
                    if (userData.size == 5) {
                        usersList.add(MainActivity.UserInfo(
                            userId = userData[0],
                            username = userData[1],
                            password = userData[2],
                            firstName = userData[3],
                            lastName = userData[4]
                        ))
                    } else {
                        Log.e("EditProfileActivity", "Skipping invalid user data line: $line")
                    }
                }
            } else {
                Log.e("EditProfileActivity", "User data file does not exist.")
            }
        } catch (e: IOException) {
            Log.e("EditProfileActivity", "Error reading user data file", e)
        }
    }

    private fun saveUserData(username: String, password: String, firstName: String, lastName: String) {
        val updatedUser = MainActivity.UserInfo(
            userId = user.userId,
            username = username.ifBlank { user.username },
            password = password.ifBlank { user.password },
            firstName = firstName.ifBlank { user.firstName },
            lastName = lastName.ifBlank { user.lastName }
        )

        val index = usersList.indexOfFirst { it.userId == user.userId }
        if (index != -1) {
            usersList[index] = updatedUser
            user = updatedUser
        }

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
    }

    private fun deleteUser() {
        usersList.removeAll { it.userId == user.userId }
        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun logoutUser() {
        // Clear shared preferences
        val sharedPreferences = getSharedPreferences("YourSharedPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        Log.d("EditProfileActivity", "Shared preferences cleared.")

        // Redirect to WelcomeActivity
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        Log.d("EditProfileActivity", "Redirecting to WelcomeActivity.")

        // Finish the current activity
        finish()
        Log.d("EditProfileActivity", "Finishing current activity.")

        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        Log.d("EditProfileActivity", "Logged out successfully.")
    }
}