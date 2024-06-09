package com.poe.clocksavvy
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
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
import android.content.ContentValues.TAG
import android.graphics.Color
import android.provider.CalendarContract
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.poe.clocksavvy.MainActivity.Companion.isOnlineMode
import com.poe.clocksavvy.MainActivity.Companion.timesheetsList
class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    companion object {
        var isOnlineMode = false
        var autoLogin = false
        var userId: String? = null
        var usersList: MutableList<UserInfo> = mutableListOf()
        var contentsList: MutableList<String> = mutableListOf()
        var categoriesList: MutableList<Category> = mutableListOf()
        var timesheetsList: MutableList<Timesheet> = mutableListOf()
    }

    data class Category(
        val categoryId: String = "",
        val userId: String = "",
        val categoryName: String = "",
        val categoryDescription: String = "",
        val categoryDate: String =""
    )

    data class Timesheet(
        val timesheetId: String = "",
        val categoryId: String = "",
        val date: String = "",
        val startTime: String = "",
        val endTime: String = "",
        val description: String = ""
    )

    data class UserInfo(
        val userId: String = "",
        val username: String = "",
        val password: String = "",
        val firstName: String = "",
        val lastName: String = ""
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)
        EdgeToEdgeUtils.enableEdgeToEdge(this)
        FirebaseApp.initializeApp(this)
        try {
            // Initialize Firebase Database
            database = FirebaseDatabase.getInstance("https://opsc-poe-part3-4b9f9-default-rtdb.europe-west1.firebasedatabase.app")
            Log.d("MainActivity", "Firebase Database initialized: ${database.app.name}")

            // Initialize Firebase Authentication
            auth = FirebaseAuth.getInstance()
            Log.d("MainActivity", "Firebase Auth initialized: ${auth.app.name}")
        } catch (e: Exception) {
            Log.e("MainActivity", "Firebase initialization failed: ${e.message}")
            Toast.makeText(this, "Firebase initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("MainActivity", "onCreate() called")

        // Initialize the app and check if initialization succeeds
        initializeApp()
    }

    private fun loadOfflineData() {
        Log.d("MainActivity", "Loading offline mode")
        val allFilesExist = listOf("Users", "Categories", "Timesheets", "Contents").all { File(filesDir, it).exists() }

        if (!allFilesExist) {
            createFilesIfNeeded()
        }
        loadUsersData()
        loadCategoriesData()
        loadTimesheetsData()
        loadContentsData()
    }

    private fun loadOnlineData() {
        Log.d("MainActivity", "Loading online mode")
        val allFilesExist = listOf("Users", "Categories", "Timesheets", "Contents").all { File(filesDir, it).exists() }
        Log.d("MainActivity", "Checking file existence: $allFilesExist")

        if (allFilesExist) {
            Log.d("MainActivity", "Files found. Performing a hard reset")
            deleteAllFiles()
        }

        val databaseReference = database.reference

        // Load Users data
        databaseReference.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserInfo::class.java)
                    user?.let { usersList.add(it) }
                }
                Log.d("MainActivity", "Loaded users data from Firebase Database: $usersList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to load users data from Firebase Database: ${error.message}")
            }
        })

        // Load Categories data
        databaseReference.child("Categories").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    category?.let { categoriesList.add(it) }
                }
                Log.d("MainActivity", "Loaded categories data from Firebase Database: $categoriesList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to load categories data from Firebase Database: ${error.message}")
            }
        })

        // Load Timesheets data
        databaseReference.child("Timesheets").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timesheetsList.clear()
                for (timesheetSnapshot in snapshot.children) {
                    Log.d("MainActivity", "Timesheet snapshot: ${timesheetSnapshot.value}") // Log the snapshot value
                    if (timesheetSnapshot.exists()) {
                        val timesheet = timesheetSnapshot.getValue(Timesheet::class.java)
                        timesheet?.let { timesheetsList.add(it) }
                    }
                }
                Log.d("MainActivity", "Loaded timesheets data from Firebase Database: $timesheetsList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to load timesheets data from Firebase Database: ${error.message}")
            }
        })

        // Load Contents data
        databaseReference.child("Contents").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contentsList.clear()
                for (contentsSnapshot in snapshot.children) {
                    val contentsData = contentsSnapshot.getValue(String::class.java)
                    contentsData?.let { contentsList.add(it) }
                }
                Log.d("MainActivity", "Loaded Contents data from Firebase Database: $contentsList")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to load contents data from Firebase Database: ${error.message}")
            }
        })
    }

    private fun isDatabaseAvailable(callback: (Boolean) -> Unit) {
        val databaseReference = database.reference
        val pingReference = databaseReference.child("ping")

        pingReference.setValue("ping").addOnCompleteListener { writeTask ->
            if (writeTask.isSuccessful) {
                pingReference.get().addOnCompleteListener { readTask ->
                    if (readTask.isSuccessful) {
                        Log.d("MainActivity", "Database ping successful")
                        callback(true)
                    } else {
                        Log.e("MainActivity", "Database ping read failed: ${readTask.exception?.message}")
                        callback(false)
                    }
                }.addOnFailureListener { readError ->
                    Log.e("MainActivity", "Database ping read failed: ${readError.message}")
                    callback(false)
                }
            } else {
                Log.e("MainActivity", "Database ping write failed: ${writeTask.exception?.message}")
                callback(false)
            }
        }.addOnFailureListener { writeError ->
            Log.e("MainActivity", "Database ping write failed: ${writeError.message}")
            callback(false)
        }
    }

    private fun initializeApp() {
        isDatabaseAvailable { isAvailable ->
            isOnlineMode = isAvailable
            if (isOnlineMode) {
                loadOnlineData()
                createFilesIfNeeded()
            } else {
                loadOfflineData()
            }

            Log.d("MainActivity", "Initialization result: true")
            Log.d("MainActivity", "AutoLogin: $autoLogin, UserID: $userId")

            // Navigate to the appropriate screen based on initialization result
            if (autoLogin && userId != null) {
                Log.d("MainActivity", "AutoLogin passed")
                navigateToMainScreen()
            } else {
                Log.d("MainActivity", "AutoLogin failed")
                navigateToWelcomeScreen()
            }
        }
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
                    categoryDate = categoryData[4]
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
            contentsList.clear()
            val lines = contentsFile.readLines()
            for (line in lines) {
                val parts = line.split("+")
                if (parts.size == 6) {
                    val timesheetId = parts[0]
                    val categoryId = parts[2].split("-")[1]
                    val dailyGoalId = parts[3]
                    val maxDailyGoal = parts[4].toInt()
                    val minDailyGoal = parts[5].toInt()
                    contentsList.add("$timesheetId+$categoryId+$dailyGoalId+$maxDailyGoal+$minDailyGoal")
                }
            }
            Log.d("MainActivity", "Loaded contents data: $contentsList")
        } else {
            Log.e("MainActivity", "Contents file does not exist")
        }
    }

    private fun createFilesIfNeeded() {
        val fileNames = arrayOf("Users", "Categories", "Timesheets", "Contents", "RememberMe.txt")
        for (fileName in fileNames) {
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
                Log.d("MainActivity", "$fileName file created")
            }
        }
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun deleteAllFiles() {
        val filesToDelete = arrayOf("Users", "Categories", "Timesheets", "Contents")
        for (fileName in filesToDelete) {
            val file = File(filesDir, fileName)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Log.d("MainActivity", "File $fileName deleted successfully.")
                } else {
                    Log.e("MainActivity", "Failed to delete file $fileName.")
                }
            } else {
                Log.d("MainActivity", "File $fileName does not exist.")
            }
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

            Log.d("RegisterActivity", "Sign Up Button clicked")
            Log.d("RegisterActivity", "Input Data - Email: $email, First Name: $firstName, Last Name: $lastName")

            // Check if any field is empty
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                // Inform the user that all fields are required
                Log.d("RegisterActivity", "All fields are required")
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the email is valid
            if (!isValidEmail(email)) {
                // Inform the user that the email is invalid
                Log.d("RegisterActivity", "Invalid email address")
                Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != confirmPassword) {
                // Inform the user that passwords do not match
                Log.d("RegisterActivity", "Passwords do not match")
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

                // Save user data to Firebase Database
                addUserToDatabase(userInfo)

                // Inform the user that registration is successful
                Log.d("RegisterActivity", "Registration successful")
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                // Navigate to login page or perform any other action
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity
            } else {
                // Email is already registered, inform the user
                Log.d("RegisterActivity", "Email already registered, please use a different email")
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
        val newUserId = maxUserId + 1
        Log.d("RegisterActivity", "Generated user ID: $newUserId")
        return newUserId
    }

    private fun isEmailRegistered(email: String): Boolean {
        // Check if the email is already registered in the system
        val isRegistered = usersList.any { it.username == email }
        Log.d("RegisterActivity", "Email $email is already registered: $isRegistered")
        return isRegistered
    }

    private fun addUserToUserList(userInfo: MainActivity.UserInfo) {
        // Add the new user data to the user list
        usersList.add(userInfo)
        Log.d("RegisterActivity", "User added to local list: $userInfo")
        saveUsersData() // Save the updated user list to a file for persistence
    }

    private fun addUserToDatabase(user: MainActivity.UserInfo) {
        if (MainActivity.isOnlineMode) {
            val databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
            databaseReference.child(user.userId).setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "User added to Firebase Database successfully: $user")
                    Toast.makeText(this, "User added to database successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle failure, log error message
                    Log.e("RegisterActivity", "Failed to add user to Firebase Database: ${e.message}")
                    Toast.makeText(this, "Failed to add user to database: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("RegisterActivity", "Offline mode, user data will not be saved to Firebase Database")
            Toast.makeText(this, "User data will be saved locally, no internet connection", Toast.LENGTH_SHORT).show()
        }
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
            Log.d("RegisterActivity", "User data saved locally")
        } catch (e: IOException) {
            // Handle failure, log error message
            Log.e("RegisterActivity", "Error saving user data: ${e.message}")
            Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    categoriesList
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
            categoriesList.filter { it.userId == userId }.map { it.categoryName }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
    }

    private fun generateReport() {
        val selectedTimesheetId = spinnerTimesheet.selectedItem.toString()
        val selectedCategoryName = spinnerCategory.selectedItem.toString()

        // Find the selected timesheet and category
        val selectedTimesheet = MainActivity.timesheetsList.find { it.timesheetId == selectedTimesheetId }
        val selectedCategory = categoriesList.find { it.categoryName == selectedCategoryName && it.userId == userId }

        // Find the user
        val user = usersList.find { it.userId == userId }

        // Generate the report
        val report = StringBuilder()
        report.append("Made by: (${user?.username}) ${user?.firstName} ${user?.lastName}\n\n")
        report.append("Category Information:\n")
        report.append("Category Name: ${selectedCategory?.categoryName}\n")
        report.append("Category Description: ${selectedCategory?.categoryDescription}\n")
        report.append("Category Date: ${selectedCategory?.categoryDate}\n\n")
        report.append("Timesheet Information:\n")
        report.append("Timesheet Date: ${selectedTimesheet?.date}\n")
        report.append("Start Time: ${selectedTimesheet?.startTime}\n")
        report.append("End Time: ${selectedTimesheet?.endTime}\n")
        report.append("Description: ${selectedTimesheet?.description}\n")

        // Display the report
        reportDisplay.text = report.toString()
    }


}//Used to generate the report
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
            // Password matches, user logged in successfully
            Log.d("LoginActivity", "User $email logged in successfully")
            // Extract the user ID
            val userId = user.userId.toInt()
            Log.d("LoginActivity", "User ID: $userId")
            // Save user data to Realtime Database
            saveUserDataToDatabase(user)
            // Pass user ID to DashboardActivity
            navigateToDashboard(userId)
            finish() // Finish the LoginActivity
            // Save login credentials if "Remember me" is checked
            if (rememberMe && email.isNotBlank() && password.isNotBlank()) {
                saveLoginCredentials(email, password)
            }
        }else {
            // User not found
            Log.d("LoginActivity", "User $email not found")
            // Inform the user that the username is not registered
            Toast.makeText(this, "Username not registered", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveUserDataToDatabase(user: MainActivity.UserInfo) {
        val db = Firebase.database
        val usersRef = db.getReference("users")
        usersRef.child(user.userId).setValue(user)
            .addOnSuccessListener { Log.d(TAG, "User data saved to Realtime Database: $user") }
            .addOnFailureListener { e -> Log.w(TAG, "Error saving user data to Realtime Database", e) }
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
            // Save the updated user data to Realtime Database
            saveUserDataToDatabase(updatedUser)
            // Inform the user that the password has been changed
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
            // Add any additional actions here, such as navigating to another activity
        } else {
            // User not found
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveUserDataToDatabase(user: MainActivity.UserInfo) {
        val db = Firebase.database
        val usersRef = db.getReference("users")
        usersRef.child(user.userId).setValue(user)
            .addOnSuccessListener { Log.d(TAG, "User data saved to Realtime Database: $user") }
            .addOnFailureListener { e -> Log.w(TAG, "Error saving user data to Realtime Database", e) }
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

        // Set logout button listener
        val logoutButton = findViewById<CardView>(R.id.logout_card)
        logoutButton.setOnClickListener {
            handleLogout()
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
                R.id.statistics_card -> cardView.setOnClickListener { handleStatisticsClick() }
                else -> {
                    // Handle other cases if needed
                }
            }
        }
    }

    private fun handleHomeClick() {
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, GenerateReportActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleCategoriesClick() {
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleTimesheetsClick() {
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, TimesheetsActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleGoalsClick() {
        val userId = intent.getStringExtra("userId")
        val intent = Intent(this, GoalsActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleStatisticsClick() {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }

    private fun handleLogout() {
        // Set RememberMe.txt to False
        val rememberMeFile = File(filesDir, "RememberMe.txt")
        try {
            rememberMeFile.writeText("False\n")
            Log.d("DashboardActivity", "RememberMe set to False")
        } catch (e: IOException) {
            Log.e("DashboardActivity", "Error updating RememberMe file: ${e.message}")
        }

        // Clear user data (optional, depending on your app's logic)

        // Re-initialize the app
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}//Dashboard activity that handles the dashboard integration functionality
private val categoryIdToUserIdMap: HashMap<String, String> = HashMap()
class AddTimesheetActivity : AppCompatActivity() {
    private lateinit var categoryId: String
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
        dateEditText.setOnClickListener {
            showDatePicker()
        }

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
        try {
            val timesheetId = generateTimesheetId()
            val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
            val date = dateEditText.text.toString()
            val startTime = startTimeEditText.text.toString()
            val endTime = endTimeEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val timesheet = MainActivity.Timesheet(
                timesheetId = timesheetId,
                categoryId = categoryId,
                date = date,
                startTime = startTime,
                endTime = endTime,
                description = description
            )

            Log.d("AddTimesheetActivity", "Timesheet to be saved: $timesheet")

            // Check if the app is in online mode
            if (isOnlineMode) {
                val databaseReference = FirebaseDatabase.getInstance().reference.child("Timesheets").child(timesheetId)
                databaseReference.setValue(timesheet)
                    .addOnSuccessListener {
                        Log.d("AddTimesheetActivity", "Timesheet added successfully")
                        Toast.makeText(this@AddTimesheetActivity, "Timesheet added successfully", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    }
                    .addOnFailureListener {
                        Log.e("AddTimesheetActivity", "Failed to add timesheet: ${it.message}", it)
                        Toast.makeText(this@AddTimesheetActivity, "Failed to add timesheet", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Offline mode: save to file and list
                timesheetsList.add(timesheet)
                saveTimesheetsToFile()
                Log.d("AddTimesheetActivity", "Timesheet added to local storage")
                Toast.makeText(this@AddTimesheetActivity, "Timesheet added to local storage", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save timesheet: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("AddTimesheetActivity", "Error saving timesheet: ${e.message}", e)
        }
    }

    private fun navigateToDashboard() {
        val categoryId = intent.getStringExtra("categoryId")
        Log.d("AddTimesheetActivity", "Navigating to DashboardActivity with Category ID: $categoryId")

        // Check if the categoryId to userId map contains the categoryId
        if (categoryId != null && categoryIdToUserIdMap.containsKey(categoryId)) {
            val userId = categoryIdToUserIdMap[categoryId]
            Log.d("AddTimesheetActivity", "Retrieved User ID: $userId")

            // Pass the userId to DashboardActivity
            val intent = Intent(this@AddTimesheetActivity, DashboardActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            finish()
        } else {
            Log.e("AddTimesheetActivity", "Failed to retrieve User ID: Category ID not found in local map")
            Toast.makeText(this@AddTimesheetActivity, "Failed to retrieve User ID", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to save timesheets to a file in offline mode
    private fun saveTimesheetsToFile() {
        try {
            val file = File(filesDir, "Timesheets")
            file.writeText(Gson().toJson(timesheetsList))
        } catch (e: IOException) {
            Log.e("AddTimesheetActivity", "Error saving timesheets to file: ${e.message}", e)
        }
    }


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
                val selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth)
                dateEditText.setText(selectedDate)
                addToDeviceCalendar(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun addToDeviceCalendar(selectedDate: String) {
        val calendar = Calendar.getInstance()
        val dateParts = selectedDate.split("-")
        calendar.set(dateParts[0].toInt(), dateParts[1].toInt() - 1, dateParts[2].toInt())

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis)
            .putExtra(CalendarContract.Events.TITLE, "Your event title")
            .putExtra(CalendarContract.Events.DESCRIPTION, "Your event description")
            .putExtra(CalendarContract.Events.EVENT_LOCATION, "Event location")
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        startActivity(intent)
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
class CategoriesSelectionActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var categories: List<MainActivity.Category>
    private lateinit var categoriesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_selection)

        userId = intent.getStringExtra("userId") ?: ""
        if (userId.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCategories()
    }

    private fun loadCategories() {
        categories = MainActivity.categoriesList.filter { it.userId == userId }
        val categoryAdapter = CategoryAdapter(categories) { categoryId ->
            handleCategorySelection(categoryId)
        }
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun handleCategorySelection(categoryId: String) {
        categoryIdToUserIdMap[categoryId] = userId
        val intent = Intent(this, AddTimesheetActivity::class.java)
        intent.putExtra("categoryId", categoryId)
        startActivity(intent)
    }

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
                selectButton.setOnClickListener {
                    onItemClick(category.categoryId)
                }
            }
        }
    }
}
class TimesheetsActivity : AppCompatActivity() {
    private lateinit var userId: String
    private lateinit var timesheets: MutableList<MainActivity.Timesheet>
    private lateinit var noTimesheetsTextView: TextView
    private lateinit var timesheetsRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheets)

        userId = intent.getStringExtra("userId") ?: ""
        Log.d("TimesheetsActivity", "User ID: $userId")
        if (userId.isEmpty()) {
            Log.e("TimesheetsActivity", "Invalid user ID")
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        databaseReference = FirebaseDatabase.getInstance().reference.child("Timesheets").child(userId)

        val addTimesheetButton: Button = findViewById(R.id.addTimesheetButton)
        addTimesheetButton.setOnClickListener {
            navigateToCategoriesSelection()
        }

        noTimesheetsTextView = findViewById(R.id.noTimesheetsTextView)
        timesheetsRecyclerView = findViewById(R.id.timesheetsRecyclerView)

        loadTimesheets()
    }

    private fun loadTimesheets() {
        Log.d("TimesheetsActivity", "Loading timesheets for user ID: $userId")
        if (MainActivity.isOnlineMode) {
            loadOnlineTimesheets()
        } else {
            loadOfflineTimesheets()
        }
    }

    private fun loadOnlineTimesheets() {
        val categoryRef = FirebaseDatabase.getInstance().reference.child("Categories")
        categoryRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryIds = mutableListOf<String>()
                for (categorySnapshot in snapshot.children) {
                    val categoryId = categorySnapshot.child("categoryId").getValue(String::class.java) ?: ""
                    if (categoryId.isNotEmpty()) {
                        categoryIds.add(categoryId)
                    }
                }
                Log.d("TimesheetsActivity", "Category IDs loaded: $categoryIds")
                fetchTimesheetsForCategories(categoryIds)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TimesheetsActivity", "Failed to load category IDs: ${error.message}")
                Toast.makeText(this@TimesheetsActivity, "Failed to load category IDs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTimesheetsForCategories(categoryIds: List<String>) {
        Log.d("TimesheetsActivity", "Fetching timesheets for categories: $categoryIds")
        timesheets = mutableListOf() // Clear the existing timesheets list

        val timesheetRef = FirebaseDatabase.getInstance().reference.child("Timesheets")
        val query = timesheetRef.orderByChild("categoryId").startAt(categoryIds.first()).endAt(categoryIds.last() + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (timesheetSnapshot in snapshot.children) {
                    val timesheet = timesheetSnapshot.getValue(MainActivity.Timesheet::class.java)
                    timesheet?.let { timesheets.add(it) }
                }
                if (timesheets.isNotEmpty()) {
                    showTimesheets()
                    noTimesheetsTextView.visibility = View.GONE
                } else {
                    showNoTimesheetsMessage()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TimesheetsActivity", "Failed to load timesheets: ${error.message}")
                Toast.makeText(this@TimesheetsActivity, "Failed to load timesheets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadOfflineTimesheets() {
        val categoryFile = File(filesDir, "Categories")
        if (categoryFile.exists()) {
            val categoryIds = categoryFile.readLines().mapNotNull { line ->
                val parts = line.split("+")
                if (parts.size >= 2 && parts[1] == MainActivity.userId) {
                    parts[0]
                } else {
                    null
                }
            }
            Log.d("TimesheetsActivity", "Category IDs loaded: $categoryIds")
            fetchTimesheetsForCategories(categoryIds)
        } else {
            Log.e("TimesheetsActivity", "Categories file does not exist")
            Toast.makeText(this, "Categories file does not exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimesheets() {
        timesheetsRecyclerView.visibility = View.VISIBLE
        Log.d("TimesheetsActivity", "Showing timesheets")
        val timesheetsAdapter = TimesheetAdapter(userId, timesheets,
            { timesheet -> editTimesheet(timesheet) },
            { timesheet -> deleteTimesheet(timesheet) })
        timesheetsRecyclerView.layoutManager = LinearLayoutManager(this)
        timesheetsRecyclerView.adapter = timesheetsAdapter
    }

    private fun showNoTimesheetsMessage() {
        Log.d("TimesheetsActivity", "No timesheets to show")
        noTimesheetsTextView.visibility = View.VISIBLE
        timesheetsRecyclerView.visibility = View.GONE
    }

    private fun navigateToCategoriesSelection() {
        val intent = Intent(this, CategoriesSelectionActivity::class.java)
        intent.putExtra("userId", userId)
        startActivityForResult(intent, REQUEST_SELECT_CATEGORY)
    }

    private fun editTimesheet(timesheet: MainActivity.Timesheet) {
        // Ensure the timesheet ID is valid before launching the activity
        val timesheetId = timesheet.timesheetId
        if (timesheetId.isNotEmpty()) {
            val intent = Intent(this, ViewTimesheetActivity::class.java)
            intent.putExtra("timesheetId", timesheetId)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid timesheet ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTimesheet(timesheet: MainActivity.Timesheet) {
        val timesheetId = timesheet.timesheetId

        if (MainActivity.isOnlineMode) {
            // Online mode: delete from the database
            val databaseReference = FirebaseDatabase.getInstance().reference.child("Timesheets").child(timesheetId)
            databaseReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@TimesheetsActivity, "Timesheet deleted successfully", Toast.LENGTH_SHORT).show()
                    timesheets.remove(timesheet) // Remove from the list
                    showTimesheets() // Update the UI
                }
                .addOnFailureListener {
                    Toast.makeText(this@TimesheetsActivity, "Failed to delete timesheet", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Offline mode: delete from the file and list
            val file = File(filesDir, "Timesheets")
            if (file.exists()) {
                val timesheetsFromFile = file.readLines().toMutableList()
                val timesheetString = timesheet.toString() // Assuming Timesheet has a proper toString() method
                if (timesheetsFromFile.remove(timesheetString)) {
                    file.writeText(timesheetsFromFile.joinToString("\n"))
                    Toast.makeText(this@TimesheetsActivity, "Timesheet deleted successfully", Toast.LENGTH_SHORT).show()
                    timesheets.remove(timesheet) // Remove from the list
                    showTimesheets() // Update the UI
                } else {
                    Toast.makeText(this@TimesheetsActivity, "Timesheet not found in file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@TimesheetsActivity, "Timesheet file does not exist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_SELECT_CATEGORY = 123
    }
}
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
            return categoriesList.find { it.categoryId == categoryId && it.userId == userId }
        }
    }
}// Timesheets adapter that allows the timesheets to be displayed
class GoalsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        // Retrieve user ID from intent
        val userId = intent.getStringExtra("userId") ?: ""
        Log.d("GoalsActivity", "User ID: $userId")

        // Load timesheets for the user
        loadTimesheetsForUser(userId)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.goalsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadTimesheetsForUser(userId: String) {
        if (MainActivity.isOnlineMode) {
            // Load timesheets from the database
            loadTimesheetsFromDatabase(userId)
        } else {
            // Load timesheets from offline storage
            loadTimesheetsFromOfflineStorage(userId)
        }
    }

    private fun loadTimesheetsFromDatabase(userId: String) {
        val categoryRef = FirebaseDatabase.getInstance().reference.child("Categories")
        categoryRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryIds = mutableListOf<String>()
                for (categorySnapshot in snapshot.children) {
                    val categoryId = categorySnapshot.child("categoryId").getValue(String::class.java) ?: ""
                    if (categoryId.isNotEmpty()) {
                        categoryIds.add(categoryId)
                    }
                }
                Log.d("GoalsActivity", "Category IDs loaded: $categoryIds")
                fetchTimesheetsForCategories(categoryIds)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GoalsActivity", "Failed to load category IDs: ${error.message}")
                Toast.makeText(this@GoalsActivity, "Failed to load category IDs", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTimesheetsForCategories(categoryIds: List<String>) {
        Log.d("GoalsActivity", "Fetching timesheets for categories: $categoryIds")
        val timesheetsList = mutableListOf<MainActivity.Timesheet>() // Initialize the timesheets list

        val timesheetRef = FirebaseDatabase.getInstance().reference.child("Timesheets")
        val query = timesheetRef.orderByChild("categoryId").startAt(categoryIds.first()).endAt(categoryIds.last() + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (timesheetSnapshot in snapshot.children) {
                    val timesheet = timesheetSnapshot.getValue(MainActivity.Timesheet::class.java)
                    timesheet?.let { timesheetsList.add(it) }
                }
                displayTimesheets(timesheetsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("GoalsActivity", "Failed to load timesheets: ${error.message}")
                Toast.makeText(this@GoalsActivity, "Failed to load timesheets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadTimesheetsFromOfflineStorage(userId: String) {
        // Find category IDs for the given user ID
        val categoryIds = MainActivity.categoriesList
            .filter { it.userId == userId }
            .map { it.categoryId }

        // Filter timesheets using category IDs
        val timesheetsList = MainActivity.timesheetsList
            .filter { categoryIds.contains(it.categoryId) }

        // Display the filtered timesheets
        displayTimesheets(timesheetsList)
    }


    private fun displayTimesheets(timesheetsList: List<MainActivity.Timesheet>) {
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
        // Navigate to the ViewTimesheetActivity and pass the timesheet ID
        val intent = Intent(this, ViewTimesheetActivity::class.java)
        intent.putExtra("timesheetId", timesheet.timesheetId)
        startActivity(intent)
        Log.d("GoalsActivity", "Selected timesheet: ${timesheet.timesheetId}")
    }
}//Goals Activity used to display the goals and allows you to edit them too
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

    override fun getItemCount(): Int = timesheetsList.size

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
        val category = categoriesList.find { it.categoryId == categoryId }
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

        // Log user ID retrieval
        Log.d("CategoriesActivity", "User ID: $userId")

        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            // Handle add category button click
            navigateToAddCategory()
        }

        noCategoriesTextView = findViewById(R.id.noCategoriesTextView)

        loadCategories()
    }

    private fun loadCategories() {
        if (MainActivity.isOnlineMode) {
            loadOnlineCategories()
        } else {
            loadOfflineCategories()
        }
    }

    private fun loadOnlineCategories() {
        val categoryRef = FirebaseDatabase.getInstance().reference.child("Categories")
        categoryRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories = mutableListOf()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(MainActivity.Category::class.java)
                    category?.let { categories.add(it) }
                }
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

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoriesActivity", "Failed to load categories: ${error.message}")
                Toast.makeText(this@CategoriesActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadOfflineCategories() {
        val categoryFile = File(filesDir, "Categories")
        if (categoryFile.exists()) {
            val categoryDataLines = categoryFile.readLines()
            categories = categoryDataLines.mapNotNull { line ->
                val parts = line.split("+")
                if (parts.size >= 2 && parts[1] == userId) {
                    MainActivity.Category(parts[0], parts[1], parts[2], parts[3], parts[4])
                } else {
                    null
                }
            }.toMutableList()

            if (categories.isNotEmpty()) {
                // Display categories in the RecyclerView
                showCategories()
                // Hide the "No categories available" message
                noCategoriesTextView.visibility = View.GONE
            } else {
                // Show a message indicating no categories found
                showNoCategoriesMessage()
            }
            Log.d("CategoriesActivity", "Loaded categories from local storage: $categories")
        } else {
            Log.e("CategoriesActivity", "Categories file does not exist")
            Toast.makeText(this, "Categories file does not exist", Toast.LENGTH_SHORT).show()
        }
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

        // Log the deleted category
        Log.d("CategoriesActivity", "Deleted category: $removedCategory")

        // If online mode, delete category from Firebase Database
        if (MainActivity.isOnlineMode) {
            deleteCategoryFromDatabase(categoryId)
        }
    }

    private fun deleteCategoryFromDatabase(categoryId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Categories")
        databaseReference.child(categoryId).removeValue()
            .addOnSuccessListener {
                Log.d("CategoriesActivity", "Category deleted from Firebase Database successfully")
            }
            .addOnFailureListener { e ->
                // Handle failure, log error message
                Log.e("CategoriesActivity", "Failed to delete category from Firebase Database: ${e.message}")
                Toast.makeText(this, "Failed to delete category from database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveCategoriesData() {
        val categoriesFile = File(filesDir, "Categories")
        val categoryDataLines = categories.map { "${it.categoryId}+${it.userId}+${it.categoryName}+${it.categoryDescription}+${it.categoryDate}" }
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
            val categoryData = "$categoryId+$categoryName+$categoryDescription+$date"
            saveCategory(categoryData)
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

    // Function to save category data
    private fun saveCategory(categoryData: String) {
        try {
            val userId = intent.getStringExtra("userId") ?: ""
            // Log the user ID
            Log.d("AddCategoryActivity", "User ID: $userId")

            val categoriesFile = File(filesDir, "Categories")
            // Ensure categoryData has the correct format
            if (categoryData.count { it == '+' } != 3) {
                Log.e("AddCategoryActivity", "Error saving category data: Invalid format")
                return
            }
            // Parse categoryData
            val categoryFields = categoryData.split("+")
            val categoryId = categoryFields[0]
            val categoryName = categoryFields[1]
            val categoryDescription = categoryFields[2]
            val categoryDate = categoryFields[3]

            // Create a new category object with the retrieved fields
            val newCategory = MainActivity.Category(categoryId, userId, categoryName, categoryDescription, categoryDate)

            // Save the category locally
            MainActivity.categoriesList.add(newCategory)
            Log.d("AddCategoryActivity", "Category data saved locally: $categoryData")
            // Inform the user about successful category addition
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()

            // If online mode, save category data to Firebase Database
            if (MainActivity.isOnlineMode) {
                saveCategoryToDatabase(newCategory)
            }

            // Navigate back to DashboardActivity with userId
            val intent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("userId", userId)
            }
            startActivity(intent)
            finish() // Optional: Finish the current activity if you don't want to keep it in the back stack
        } catch (e: IOException) {
            Log.e("AddCategoryActivity", "Error saving category data: ${e.message}")
            e.printStackTrace()
            // Inform the user about the error
            Toast.makeText(this, "Failed to add category. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to parse category data
    private fun parseCategory(categoryData: String): MainActivity.Category {
        val categoryFields = categoryData.split("+")
        return MainActivity.Category(
            categoryId = categoryFields.getOrElse(0) { "" },
            categoryName = categoryFields.getOrElse(1) { "" },
            categoryDescription = categoryFields.getOrElse(2) { "" },
            categoryDate = categoryFields.getOrElse(3) { "" }
        )
    }

    // Function to save category to Firebase Database
    private fun saveCategoryToDatabase(category: MainActivity.Category) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Categories")
        databaseReference.child(category.categoryId).setValue(category)
            .addOnSuccessListener {
                Log.d("AddCategoryActivity", "Category added to Firebase Database successfully: $category")
            }
            .addOnFailureListener { e ->
                // Handle failure, log error message
                Log.e("AddCategoryActivity", "Failed to add category to Firebase Database: ${e.message}")
                Toast.makeText(this, "Failed to add category to database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
                    val categoryData = "${category.categoryId}+${category.userId}+${category.categoryName}+${category.categoryDescription}+${category.categoryDate}"
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