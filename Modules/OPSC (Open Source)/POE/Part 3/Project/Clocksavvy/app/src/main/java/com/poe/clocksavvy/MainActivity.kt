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
import com.poe.clocksavvy.MainActivity.Companion.contentsList
import com.poe.clocksavvy.MainActivity.Companion.isOnlineMode
import com.poe.clocksavvy.MainActivity.Companion.timesheetsList
import android.widget.LinearLayout
import android.widget.ProgressBar
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
        var date: String = "",
        var startTime: String = "",
        var endTime: String = "",
        var description: String = ""
    ) {
        fun toStringFormatted(): String {
            return "$timesheetId+$categoryId+$date+$startTime+$endTime+$description"
        }
    }

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
            categoriesList = categoriesFile.readLines().mapNotNull { line ->
                val categoryData = line.split("+")
                if (categoryData.size >= 5) {
                    Category(
                        categoryId = categoryData[0],
                        userId = categoryData[1],
                        categoryName = categoryData[2],
                        categoryDescription = categoryData[3],
                        categoryDate = categoryData[4]
                    )
                } else {
                    Log.e("MainActivity", "Invalid category data format: $line")
                    null // Skip invalid data
                }
            }.toMutableList()
            Log.d("MainActivity", "Loaded categories data: $categoriesList")
        } else {
            Log.e("MainActivity", "Categories file does not exist")
        }
    }

    private fun loadTimesheetsData() {
        val timesheetsFile = File(filesDir, "Timesheets")
        if (timesheetsFile.exists()) {
            timesheetsList = mutableListOf()
            timesheetsFile.forEachLine { line ->
                val timesheetData = line.split("+")
                if (timesheetData.size >= 6) { // Ensure there are at least 6 elements
                    timesheetsList.add(
                        Timesheet(
                            timesheetId = timesheetData[0],
                            categoryId = timesheetData[1],
                            date = timesheetData[2],
                            startTime = timesheetData[3],
                            endTime = timesheetData[4],
                            description = timesheetData[5]
                        )
                    )
                } else {
                    Log.e("MainActivity", "Invalid timesheet data: $line")
                }
            }
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
    private fun deleteContentsFile() {
        val contentsFile = File(filesDir, "Contents")
        if (contentsFile.exists()) {
            contentsFile.delete()
            Log.d("MainActivity", "Contents file deleted successfully")
        } else {
            Log.e("MainActivity", "Contents file does not exist")
        }
    }
    fun deleteCategoriesFile() {
        val categoriesFile = File(filesDir, "Categories")
        if (categoriesFile.exists()) {
            val isDeleted = categoriesFile.delete()
            if (isDeleted) {
                println("Categories file deleted successfully")
            } else {
                println("Failed to delete categories file")
            }
        } else {
            println("Categories file does not exist")
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
            //saveUserDataToDatabase(user)
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
    /*private fun saveUserDataToDatabase(user: MainActivity.UserInfo) {
        val db = Firebase.database
        val usersRef = db.getReference("Users")
        usersRef.child(user.userId).setValue(user)
            .addOnSuccessListener { Log.d(TAG, "User data saved to Realtime Database: $user") }
            .addOnFailureListener { e -> Log.w(TAG, "Error saving user data to Realtime Database", e) }
    }*/

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
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()

        val userId = intent.getStringExtra("userId")
        Log.d("DashboardActivity", "Received User ID: $userId")

        if (userId != null) {
            // User ID is successfully retrieved, proceed with your logic
            setCardViewListeners()
        } else {
            // Handle case where user ID is not passed correctly
            Log.e("DashboardActivity", "User ID is null")
            Toast.makeText(this, "An error occurred. Redirecting to welcome page...", Toast.LENGTH_SHORT).show()

            // Redirect the user to the welcome page
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
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
        Log.d("DashboardActivity", "Handle Home Click: User ID - $userId")
        val intent = Intent(this, GenerateReportActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleCategoriesClick() {
        val userId = intent.getStringExtra("userId")
        Log.d("DashboardActivity", "Handle Categories Click: User ID - $userId")
        val intent = Intent(this, CategoriesActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleTimesheetsClick() {
        val userId = intent.getStringExtra("userId")
        Log.d("DashboardActivity", "Handle Timesheets Click: User ID - $userId")
        val intent = Intent(this, TimesheetsActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    private fun handleGoalsClick() {
        if (isOnlineMode) {
            val userId = intent.getStringExtra("userId")
            Log.d("DashboardActivity", "Handle Goals Click: User ID - $userId")
            val intent = Intent(this, GoalsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        } else {
            // Display message when app is in offline mode
            Toast.makeText(this, "Feature unavailable in offline mode", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleStatisticsClick() {
        if (isOnlineMode) {
            // Retrieve the userId from where you have it stored
            val userId = intent.getStringExtra("userId")
            Log.d("DashboardActivity", "Handle stats Click: User ID - $userId")
            // Start the StatisticsActivity and pass the userId as an intent extra
            val intent = Intent(this, StatisticsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        } else {
            // Display message when app is in offline mode
            Toast.makeText(this, "Feature unavailable in offline mode", Toast.LENGTH_SHORT).show()
        }
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
        Log.d(
            "AddTimesheetActivity",
            "Navigating to DashboardActivity with Category ID: $categoryId"
        )

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
            Log.e(
                "AddTimesheetActivity",
                "Failed to retrieve User ID: Category ID not found in local map"
            )
            Toast.makeText(
                this@AddTimesheetActivity,
                "Failed to retrieve User ID",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Function to save timesheets to a file in offline mode
    private fun saveTimesheetsToFile() {
        try {
            val file = File(filesDir, "Timesheets")
            val stringBuilder = StringBuilder()

            for (timesheet in timesheetsList) {
                val timesheetData =
                    "${timesheet.timesheetId}+${timesheet.categoryId}+${timesheet.date}+${timesheet.startTime}+${timesheet.endTime}+${timesheet.description}"
                stringBuilder.append("$timesheetData\n")
            }

            file.writeText(stringBuilder.toString())
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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_STORAGE
            )
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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

    fun fetchTimesheetsForCategories(categoryIds: List<String>) {
        Log.d("TimesheetsActivity", "Fetching timesheets for categories: $categoryIds")
        timesheets = mutableListOf() // Clear the existing timesheets list

        if (categoryIds.isEmpty()) {
            Log.e("TimesheetsActivity", "Category IDs list is empty.")
            Toast.makeText(this, "No categories selected", Toast.LENGTH_SHORT).show()
            return
        }

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
        try {
            val file = File(filesDir, "Timesheets")
            if (file.exists()) {
                timesheets = mutableListOf()

                file.readLines().forEach { line ->
                    val parts = line.split("+")
                    if (parts.size == 6) {
                        val timesheetId = parts[0]
                        val categoryId = parts[1]
                        val date = parts[2]
                        val startTime = parts[3]
                        val endTime = parts[4]
                        val description = parts[5]

                        val timesheet = MainActivity.Timesheet(
                            timesheetId = timesheetId,
                            categoryId = categoryId,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            description = description
                        )

                        timesheets.add(timesheet)
                    } else {
                        Log.e("TimesheetsActivity", "Invalid timesheet format in line: $line")
                    }
                }

                if (timesheets.isNotEmpty()) {
                    showTimesheets()
                    noTimesheetsTextView.visibility = View.GONE
                } else {
                    showNoTimesheetsMessage()
                }
            } else {
                Log.e("TimesheetsActivity", "Timesheets file does not exist")
                Toast.makeText(this, "Timesheets file does not exist", Toast.LENGTH_SHORT).show()
                showNoTimesheetsMessage()
            }
        } catch (e: IOException) {
            Log.e("TimesheetsActivity", "Error loading timesheets from file: ${e.message}", e)
            Toast.makeText(this, "Error loading timesheets", Toast.LENGTH_SHORT).show()
            showNoTimesheetsMessage()
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
                val timesheetString = timesheet.toStringFormatted() // Use toStringFormatted() method
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
        if (isOnlineMode) {
            fetchCategoriesFromDatabase(userId) { fetchedCategories ->
                categories = fetchedCategories
                val categoryAdapter = CategoryAdapter(categories) { categoryId ->
                    handleCategorySelection(categoryId)
                }
                categoriesRecyclerView.adapter = categoryAdapter
            }
        } else {
            categories = MainActivity.categoriesList.filter { it.userId == userId }
            val categoryAdapter = CategoryAdapter(categories) { categoryId ->
                handleCategorySelection(categoryId)
            }
            categoriesRecyclerView.adapter = categoryAdapter
        }
    }

    // Function to fetch categories from Firebase Database
    private fun fetchCategoriesFromDatabase(userId: String, callback: (List<MainActivity.Category>) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Categories")
        databaseReference.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val categories = mutableListOf<MainActivity.Category>()
                    for (snapshot in dataSnapshot.children) {
                        val category = snapshot.getValue(MainActivity.Category::class.java)
                        category?.let { categories.add(it) }
                    }
                    callback(categories)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("loadCategories", "Failed to fetch categories: ${databaseError.message}")
                    Toast.makeText(this@CategoriesSelectionActivity, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            })
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
        if (categoryIds.isEmpty()) {
            Log.e("GoalsActivity", "No category IDs available to fetch timesheets for")
            Toast.makeText(this@GoalsActivity, "No category IDs available to fetch timesheets for", Toast.LENGTH_SHORT).show()
            return
        }

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
    private lateinit var minDailyGoalEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var startTimeEditText: EditText
    private lateinit var endTimeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var recyclerViewGoals: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_timesheet)

        timesheetId = intent.getStringExtra("timesheetId") ?: ""

        // Check for online mode
        if (MainActivity.isOnlineMode) {
            fetchTimesheetFromDatabase(timesheetId)
        } else {
            fetchTimesheetFromFile(timesheetId)
        }
    }

    private fun fetchTimesheetFromDatabase(timesheetId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Timesheets")
        databaseReference.child(timesheetId).get().addOnSuccessListener { dataSnapshot ->
            val timesheet = dataSnapshot.getValue(MainActivity.Timesheet::class.java)
            if (timesheet != null) {
                displayTimesheetDetails(timesheet)
                Log.d("ViewTimesheetActivity", "Timesheet fetched successfully from database")
            } else {
                Toast.makeText(this, "Timesheet not found in database", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch timesheet from database", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchTimesheetFromFile(timesheetId: String) {
        val file = File(filesDir, "Timesheets")
        if (file.exists()) {
            val timesheetsFromFile = file.readLines().mapNotNull { line ->
                val parts = line.split("+")
                if (parts.size == 6 && parts[0] == timesheetId) {
                    MainActivity.Timesheet(
                        timesheetId = parts[0],
                        categoryId = parts[1],
                        date = parts[2],
                        startTime = parts[3],
                        endTime = parts[4],
                        description = parts[5]
                    )
                } else {
                    null
                }
            }
            if (timesheetsFromFile.isNotEmpty()) {
                displayTimesheetDetails(timesheetsFromFile[0])
                Log.d("ViewTimesheetActivity", "Timesheet fetched successfully from file")
            } else {
                Toast.makeText(this, "Timesheet not found in file", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Timesheets file does not exist", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayTimesheetDetails(timesheet: MainActivity.Timesheet) {
        findViewById<TextView>(R.id.textViewTimesheetId).text = "Timesheet ID: ${timesheet.timesheetId}"
        findViewById<TextView>(R.id.textViewCategory).text = "Category: ${getCategoryName(timesheet.categoryId)}"

        dailyGoalEditText = findViewById(R.id.editTextMinDailyGoal)
        maxDailyGoalEditText = findViewById(R.id.editTextMaxDailyGoal)
        minDailyGoalEditText = findViewById(R.id.editTextMinDailyGoal)
        dateEditText = findViewById(R.id.editTextDate)
        startTimeEditText = findViewById(R.id.editTextStartTime)
        endTimeEditText = findViewById(R.id.editTextEndTime)
        descriptionEditText = findViewById(R.id.editTextDescription)

        val dailyGoalInfo = getDailyGoalInfoForTimesheet(timesheet.timesheetId)
        dailyGoalEditText.setText(dailyGoalInfo.first.toString())
        maxDailyGoalEditText.setText(dailyGoalInfo.second.toString())
        dateEditText.setText(timesheet.date)
        startTimeEditText.setText(timesheet.startTime)
        endTimeEditText.setText(timesheet.endTime)
        descriptionEditText.setText(timesheet.description)

        setDateTimeFieldListeners()

        recyclerViewGoals = findViewById(R.id.recyclerViewGoals)
        recyclerViewGoals.layoutManager = LinearLayoutManager(this)
        recyclerViewGoals.adapter = GoalsAdapter(getGoalsForTimesheet(timesheet.timesheetId)) { }

        findViewById<Button>(R.id.buttonDelete).setOnClickListener { deleteTimesheet(timesheet) }
        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            updateProgress()
            saveTimesheetChanges(timesheet)
        }
    }

    private fun getCategoryName(categoryId: String): String {
        val category = MainActivity.categoriesList.find { it.categoryId == categoryId }
        return category?.categoryName ?: "Unknown"
    }

    private fun getDailyGoalInfoForTimesheet(timesheetId: String): Pair<Int, Int> {
        val content = MainActivity.contentsList.find { it.startsWith("$timesheetId+") }
        return if (content != null) {
            val parts = content.split("-")
            if (parts.size >= 2) {
                val dailyGoalParts = parts[1].split("+")
                if (dailyGoalParts.size >= 2) {
                    val maxDailyGoal = dailyGoalParts[dailyGoalParts.size - 1].toInt()
                    val minDailyGoal = dailyGoalParts[dailyGoalParts.size - 2].toInt()
                    Pair(maxDailyGoal, minDailyGoal)
                } else {
                    Log.e("ViewTimesheetActivity", "Unexpected daily goal format: $content")
                    Pair(0, 0)
                }
            } else {
                Log.e("ViewTimesheetActivity", "Unexpected content format: $content")
                Pair(0, 0)
            }
        } else {
            Pair(0, 0)
        }
    }

    private fun getGoalsForTimesheet(timesheetId: String): List<MainActivity.Timesheet> {
        val filteredContents = MainActivity.contentsList.filter { it.startsWith("$timesheetId+") }
        return filteredContents.map {
            val parts = it.split("+")
            val goalParts = parts[1].split("-")
            val minMaxGoals = goalParts[1].split("+")
            MainActivity.Timesheet(
                timesheetId = parts[0],
                categoryId = goalParts[0],
                date = parts[2],
                startTime = parts[3],
                endTime = parts[4],
                description = minMaxGoals.last()
            )
        }
    }

    private fun deleteTimesheet(timesheet: MainActivity.Timesheet) {
        if (MainActivity.isOnlineMode) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Timesheets")
            databaseReference.child(timesheet.timesheetId).removeValue()
                .addOnSuccessListener {
                    MainActivity.timesheetsList.remove(timesheet)
                    Toast.makeText(this, "Timesheet deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete timesheet", Toast.LENGTH_SHORT).show()
                }
        } else {
            MainActivity.timesheetsList.remove(timesheet)
            updateTimesheetsFile()
            Toast.makeText(this, "Timesheet deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateTimesheetsFile() {
        val timesheetsFile = File(filesDir, "Timesheets")
        if (timesheetsFile.exists()) {
            timesheetsFile.bufferedWriter().use { out ->
                MainActivity.timesheetsList.forEach { timesheet ->
                    val line = "${timesheet.timesheetId}+${timesheet.categoryId}+${timesheet.date}+${timesheet.startTime}+${timesheet.endTime}+${timesheet.description}\n"
                    out.write(line)
                    Log.d("MainActivity", "Timesheet saved: $line")
                }
            }
            Log.d("MainActivity", "Timesheets file updated successfully")
        } else {
            Log.e("MainActivity", "Timesheets file does not exist")
        }
    }

    private fun updateProgress() {
        // Initialize max and min daily goals to zero if EditTexts are null
        initializeMaxAndMinToZero()

        // Calculate progress percentage
        val progressPercentage = calculateProgressPercentage()

        // Display progress status with calculated percentage
        displayProgressStatus(progressPercentage.toString())
    }

    private fun calculateProgressPercentage(): Int {
        val minDailyGoal = minDailyGoalEditText.text.toString().toIntOrNull() ?: 0
        val maxDailyGoal = maxDailyGoalEditText.text.toString().toIntOrNull() ?: 1
        return if (maxDailyGoal != 0) {
            (minDailyGoal.toDouble() / maxDailyGoal.toDouble() * 100).toInt()
        } else {
            0 // Handle division by zero by setting progress to 0
        }
    }

    private fun displayProgressStatus(progressPercentage: String) {
        val progressStatusTextView = findViewById<TextView>(R.id.textViewProgressStatus)
        progressStatusTextView.text = "Progress Status: "

        val progressPercentageTextView = findViewById<TextView>(R.id.textViewProgressPercentage)

        // Parse progress percentage to determine status
        val progress = progressPercentage.toIntOrNull() ?: 0

        // Set text color and status based on progress percentage
        val textColor: Int
        val status: String
        when {
            progress < 50 -> {
                status = "Not good"
                textColor = ContextCompat.getColor(this, R.color.not_good)
            }
            progress in 50..60 -> {
                status = "Fair progress"
                textColor = ContextCompat.getColor(this, R.color.fair_progress)
            }
            else -> {
                status = "Strong progress"
                textColor = ContextCompat.getColor(this, R.color.strong_progress)
            }
        }

        // Apply text color and status to progress status TextView
        progressStatusTextView.text = "Progress Status: $status"
        progressStatusTextView.setTextColor(textColor)

        // Apply text color to progress percentage TextView
        progressPercentageTextView.setTextColor(textColor)
        progressPercentageTextView.text = "Progress Percentage: $progressPercentage%"
    }

    private fun initializeMaxAndMinToZero() {
        if (maxDailyGoalEditText.text.isNullOrEmpty()) {
            maxDailyGoalEditText.setText("0")
        }
        if (minDailyGoalEditText.text.isNullOrEmpty()) {
            minDailyGoalEditText.setText("0")
        }
    }

    private fun setDateTimeFieldListeners() {
        setDateFieldListener(dateEditText)
        setTimeFieldListener(startTimeEditText)
        setTimeFieldListener(endTimeEditText)
    }

    private fun setDateFieldListener(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(calendar.time))
        }

        editText.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setTimeFieldListener(editText: EditText) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            editText.setText(timeFormat.format(calendar.time))
        }

        editText.setOnClickListener {
            TimePickerDialog(
                this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun constructContentData(timesheet: MainActivity.Timesheet, maxDailyGoal: Int, minDailyGoal: Int): String {
        val dailyGoalId = generateUniqueDailyGoalId()
        return if (dailyGoalId.isNotBlank()) {
            "${timesheet.timesheetId}+0-${timesheet.categoryId}+$dailyGoalId+$maxDailyGoal+$minDailyGoal"
        } else {
            "${timesheet.timesheetId}+0-${timesheet.categoryId}++"
        }
    }

    private fun saveTimesheetChanges(timesheet: MainActivity.Timesheet) {
        val updatedTimesheet = timesheet.copy(
            date = dateEditText.text.toString(),
            startTime = startTimeEditText.text.toString(),
            endTime = endTimeEditText.text.toString(),
            description = descriptionEditText.text.toString()
        )

        // Retrieve max and min daily goals from EditText fields
        val maxDailyGoal = maxDailyGoalEditText.text.toString().toIntOrNull() ?: 0
        val minDailyGoal = minDailyGoalEditText.text.toString().toIntOrNull() ?: 0

        if (MainActivity.isOnlineMode) {
            val dailyGoalId = generateUniqueDailyGoalId()
            saveContentToDatabase(updatedTimesheet, "0", timesheet.categoryId, dailyGoalId, maxDailyGoal, minDailyGoal)
            saveTimesheetToDatabase(updatedTimesheet)
            Log.d("ViewTimesheetActivity", "Changes saved in online mode")
        } else {
            val contentData = constructContentData(timesheet, maxDailyGoal, minDailyGoal)
            updateContentsFile(contentData)
            updateTimesheetsFile()
            Log.d("ViewTimesheetActivity", "Changes saved in offline mode")
        }

        val index = MainActivity.timesheetsList.indexOfFirst { it.timesheetId == timesheet.timesheetId }
        if (index != -1) {
            MainActivity.timesheetsList[index] = updatedTimesheet
            Toast.makeText(this, "Timesheet updated", Toast.LENGTH_SHORT).show()
            Log.d("ViewTimesheetActivity", "Timesheet updated")
        } else {
            Toast.makeText(this, "Timesheet not found", Toast.LENGTH_SHORT).show()
            Log.e("ViewTimesheetActivity", "Timesheet not found")
        }
    }

    private fun saveContentToDatabase(timesheet: MainActivity.Timesheet, photoId: String, category: String, dailyGoalId: String, maxDailyGoal: Int, minDailyGoal: Int) {
        val contentData = if (dailyGoalId.isNotBlank()) {
            "${timesheet.timesheetId}+$photoId-$category+$dailyGoalId+$maxDailyGoal+$minDailyGoal"
        } else {
            "${timesheet.timesheetId}+$photoId-$category++"
        }
        Log.d("ViewTimesheetActivity", "Content data to be saved: $contentData")

        val databaseReference = FirebaseDatabase.getInstance().getReference("Contents")
        databaseReference.child(timesheet.timesheetId).setValue(contentData)
            .addOnSuccessListener {
                Log.d("ViewTimesheetActivity", "Content saved to database")
                // Update local contents list
                val updatedContent = "${timesheet.timesheetId}+$photoId-$category+$dailyGoalId+$maxDailyGoal+$minDailyGoal"
                val index = MainActivity.contentsList.indexOfFirst { it.startsWith("${timesheet.timesheetId}+") }
                if (index != -1) {
                    MainActivity.contentsList[index] = updatedContent
                } else {
                    MainActivity.contentsList.add(updatedContent)
                }
                // Update contents file
                updateContentsFile(updatedContent)
            }
            .addOnFailureListener {
                Log.e("ViewTimesheetActivity", "Failed to save content to database")
            }
    }

    private fun saveTimesheetToDatabase(timesheet: MainActivity.Timesheet) {
        Log.d("ViewTimesheetActivity", "Timesheet data to be saved: $timesheet")

        val databaseReference = FirebaseDatabase.getInstance().getReference("Timesheets")
        databaseReference.child(timesheet.timesheetId).setValue(timesheet)
            .addOnSuccessListener {
                Log.d("ViewTimesheetActivity", "Timesheet saved to database")
            }
            .addOnFailureListener {
                Log.e("ViewTimesheetActivity", "Failed to save timesheet to database")
            }
    }

    private fun updateContentsFile(contentData: String) {
        val contentsFile = File(filesDir, "Contents")
        if (contentsFile.exists()) {
            val existingContents = contentsFile.readLines().toMutableList()
            val index = existingContents.indexOfFirst { it.startsWith(contentData.split("+")[0]) }
            if (index != -1) {
                existingContents[index] = contentData
            } else {
                existingContents.add(contentData)
            }
            contentsFile.writeText(existingContents.joinToString("\n"))
            Log.d("ViewTimesheetActivity", "Content saved: $contentData")
            Log.d("MainActivity", "Contents file updated successfully")

            // Read contents file again for confirmation
            val updatedContents = contentsFile.readLines()
            Log.d("MainActivity", "Contents file content after update: $updatedContents")
        } else {
            Log.e("MainActivity", "Contents file does not exist")
        }
    }

    private fun generateUniqueDailyGoalId(): String {
        val existingIds = MainActivity.contentsList.mapNotNull {
            val parts = it.split("+")
            parts.getOrNull(3)
        }.toSet()
        var newId = (1..Int.MAX_VALUE).firstOrNull { it.toString() !in existingIds } ?: 0
        return newId.toString()
    }
}
class StatisticsActivity : AppCompatActivity() {
    data class StatisticsData(
        val minGoalsArray: IntArray,
        val maxGoalsArray: IntArray,
        val timesheetDates: List<String>,
        val labels: List<String>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Retrieve userId from intent extras
        val userId = intent.getStringExtra("userId") ?: return

        // Fetch user data based on userId
        val (minGoalsArray, maxGoalsArray, timesheetDates, labels) = fetchUserDataAndPrepareChartData(userId)

        // Show chart with the prepared data
        showChart(minGoalsArray, maxGoalsArray, labels)

        // Find the "Done" button
        val doneButton = findViewById<Button>(R.id.done_button)
        // Set click listener to the "Done" button
        doneButton.setOnClickListener {
            // Create intent to go back to the DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            // Put userId as an extra in the intent
            intent.putExtra("userId", userId)
            // Start DashboardActivity with the intent
            startActivity(intent)
        }
    }


    private fun fetchUserDataAndPrepareChartData(userId: String): StatisticsData {
        Log.d("StatisticsActivity", "Fetching data for userId: $userId")

        val userCategories = categoriesList.filter { it.userId == userId }.map { it.categoryId }
        Log.d("StatisticsActivity", "User categories: $userCategories")

        // Log timesheetsList to see if it contains data
        Log.d("StatisticsActivity", "All timesheets: $timesheetsList")

        val userTimesheets = timesheetsList.filter { userCategories.contains(it.categoryId) }
        Log.d("StatisticsActivity", "User timesheets: $userTimesheets")

        val minGoals = mutableListOf<Int>()
        val maxGoals = mutableListOf<Int>()
        val timesheetDates = mutableListOf<String>()
        val labels = mutableListOf<String>()

        // Fetch min and max goals for each data point
        for (timesheet in userTimesheets) {
            val content = contentsList.find { it.startsWith("${timesheet.timesheetId}+") }
            Log.d("StatisticsActivity", "Content for timesheetId ${timesheet.timesheetId}: $content")

            content?.let {
                val goalsParts = it.split("-")[1].split("+")
                Log.d("StatisticsActivity", "Goals parts: $goalsParts")
                if (goalsParts.size >= 4) {
                    val maxGoal = goalsParts[2].toIntOrNull()
                    val minGoal = goalsParts[3].toIntOrNull()

                    minGoal?.let { minGoals.add(it) }
                    maxGoal?.let { maxGoals.add(it) }
                    timesheetDates.add(timesheet.date) // Add date to the list
                    labels.add(timesheet.date) // Use timesheet date as label
                    Log.d("StatisticsActivity", "Added minGoal: $minGoal, maxGoal: $maxGoal, date: ${timesheet.date}")
                }
            }
        }

        return StatisticsData(minGoals.toIntArray(), maxGoals.toIntArray(), timesheetDates, labels)
    }

    private fun showStatistics(minGoals: List<Int>, maxGoals: List<Int>, timesheetDates: List<String>, userTimesheets: List<MainActivity.Timesheet>) {
        // Convert lists to arrays for Intent extra
        val minGoalsArray = minGoals.toIntArray()
        val maxGoalsArray = maxGoals.toIntArray()

        // Log the final min and max goals
        Log.d("DashboardActivity", "Final minGoalsArray: ${minGoalsArray.contentToString()}")
        Log.d("DashboardActivity", "Final maxGoalsArray: ${maxGoalsArray.contentToString()}")
        timesheetDates.forEach { date ->
            Log.d("DashboardActivity", "Date: $date")
        }

        // Start StatisticsActivity and pass the min and max goals arrays as extras
        val intent = Intent(this, StatisticsActivity::class.java)
        intent.putExtra("MIN_GOALS", minGoalsArray)
        intent.putExtra("MAX_GOALS", maxGoalsArray)
        intent.putStringArrayListExtra("TIMESHEET_DATES", ArrayList(timesheetDates))

        // Pass data points and labels for chart blocks
        val labels = userTimesheets.map { "Label ${it.timesheetId}" } // Replace with your own labels
        intent.putStringArrayListExtra("LABELS", ArrayList(labels))

        Log.d("DashboardActivity", "Starting StatisticsActivity with labels: $labels")

        startActivity(intent)
    }

    private fun showDummyStatistics() {
        val dummyData = List(10) { (1..50).random() } // Generate random data
        val dummyMinGoals = List(10) { (1..10).random() } // Generate random min goals data
        val dummyMaxGoals = List(10) { (11..20).random() } // Generate random max goals data

        Log.d("DashboardActivity", "Showing dummy statistics with data: $dummyData")
        Log.d("DashboardActivity", "Dummy min goals: $dummyMinGoals")
        Log.d("DashboardActivity", "Dummy max goals: $dummyMaxGoals")

        val intent = Intent(this, StatisticsActivity::class.java)
        intent.putExtra("USER_DATA", dummyData.toIntArray())
        intent.putExtra("MIN_GOALS", dummyMinGoals.toIntArray())
        intent.putExtra("MAX_GOALS", dummyMaxGoals.toIntArray())

        startActivity(intent)
    }

    private fun showChart(minGoalsArray: IntArray, maxGoalsArray: IntArray, timesheetDates: List<String>) {
        Log.d("StatisticsActivity", "minGoalsArray: ${minGoalsArray.joinToString()}")
        Log.d("StatisticsActivity", "maxGoalsArray: ${maxGoalsArray.joinToString()}")
        Log.d("StatisticsActivity", "timesheetDates: ${timesheetDates.joinToString()}")

        val barsContainer = findViewById<LinearLayout>(R.id.bars_container)
        barsContainer.removeAllViews()

        val maxValue = maxGoalsArray.maxOrNull() ?: 10  // Avoid division by zero
        val scaleFactor = 300.0 / maxValue  // Assuming 300dp is the max height of your container

        for (i in minGoalsArray.indices) {
            val minGoal = minGoalsArray[i]
            val maxGoal = maxGoalsArray[i]
            Log.d("StatisticsActivity", "Result - Min: $minGoal, Max: $maxGoal")

            val minBarHeight = (minGoal * scaleFactor).toInt()
            val maxBarHeight = (maxGoal * scaleFactor).toInt()

            val barContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(5, 0, 5, 0)
                }
                gravity = Gravity.BOTTOM
            }

            val maxBar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(30, maxBarHeight).apply {
                    setMargins(0, 0, 5, 0)  // Adjust margins as needed
                }
                setBackgroundColor(Color.RED)
            }

            val minBar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(30, minBarHeight).apply {
                    setMargins(0, 0, 5, 0)  // Adjust margins as needed
                }
                setBackgroundColor(Color.GREEN)
            }

            barContainer.addView(maxBar)
            barContainer.addView(minBar)

            val labelView = TextView(this).apply {
                text = timesheetDates[i] + " " // Set label to corresponding timesheet date with space
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER
                    topMargin = 10 // Set top margin for the label
                }
                setTextColor(Color.WHITE)  // Set text color to white
                textSize = 10f // Set smaller font size
            }

            val barWithLabel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                addView(barContainer)
                addView(labelView)
            }

            barsContainer.addView(barWithLabel)
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

    private fun filterCategoriesByUserId(userId: String): List<MainActivity.Category> {
        // Load categories data from the file and filter by user ID
        val filteredCategories = MainActivity.categoriesList.filter { it.userId == userId }
        Log.d("CategoriesActivity", "Filtered categories by user ID ($userId): $filteredCategories")
        return filteredCategories
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

    private fun navigateToAddCategory() {
        // Navigate to AddCategoryActivity to add a new category
        val intent = Intent(this, AddCategoryActivity::class.java)
        intent.putExtra("userId", userId) // Pass the userId along with the intent
        startActivity(intent)
    }
    // Function to delete a category
    private fun deleteCategory(categoryId: String) {
        // Remove the category from the list in memory
        // If online mode, delete category from Firebase Database
        if (MainActivity.isOnlineMode) {
            deleteCategoryFromDatabase(categoryId)
        }
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
            private val editButton: Button = itemView.findViewById(R.id.editButton)

            fun bind(category: MainActivity.Category) {
                categoryNameTextView.text = category.categoryName

                // Set click listener for delete button
                deleteButton.setOnClickListener {
                    val categoryId = categories[adapterPosition].categoryId
                    onDeleteCategory(categoryId)
                }

                // Set click listener for edit button
                editButton.setOnClickListener {
                    val intent = Intent(itemView.context, EditCategoryActivity::class.java).apply {
                        putExtra("categoryId", category.categoryId)
                    }
                    itemView.context.startActivity(intent)
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

            val newCategory = parseCategory(categoryData)

            // Save the category locally
            MainActivity.categoriesList.add(newCategory)
            Log.d("AddCategoryActivity", "Category data saved locally: $categoryData")
            // Inform the user about successful category addition
            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()

            if (MainActivity.isOnlineMode) {
                // If online mode, save category data to Firebase Database
                saveCategoryToDatabase(newCategory, userId)
            } else {
                // If offline mode, save category data to file
                saveCategoryToFile(newCategory, userId)
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

    // Function to save category data to file
    private fun saveCategoryToFile(category: MainActivity.Category, userId: String) {
        try {
            val categoryFile = File(filesDir, "Categories")
            val categoryData = "${category.categoryId}+$userId+${category.categoryName}+${category.categoryDescription}+${category.categoryDate}"
            categoryFile.appendText("$categoryData\n")
        } catch (e: IOException) {
            Log.e("AddCategoryActivity", "Error saving category data to file: ${e.message}")
            e.printStackTrace()
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
    private fun saveCategoryToDatabase(category: MainActivity.Category, userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Categories")
        // Update the userId in the category data before saving to the database
        val categoryWithUserId = category.copy(userId = userId)
        databaseReference.child(category.categoryId).setValue(categoryWithUserId)
            .addOnSuccessListener {
                Log.d("AddCategoryActivity", "Category added to Firebase Database successfully: $categoryWithUserId")
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
            val category = getCategoryById(categoryId)
            val userId = category?.userId ?: ""
            val success = updateCategory(categoryId, updatedName, updatedDescription, userId)

            // Check if the update was successful
            if (success) {
                Toast.makeText(this, "Category updated successfully", Toast.LENGTH_SHORT).show()
                returnToDashboard(userId) // Return to dashboard
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

    private fun updateCategory(categoryId: String, name: String, description: String, userId: String): Boolean {
        // Check if the app is in online mode
        if (isOnlineMode) {
            // If online mode, update the category in the database
            updateCategoryInDatabase(categoryId, name, description, userId)
            return true // Return true since the update is initiated
        } else {
            // If offline mode, update the category in the list and file
            val index = categoriesList.indexOfFirst { it.categoryId == categoryId }
            if (index != -1) {
                // Update the category's name and description in the list
                categoriesList[index] = categoriesList[index].copy(
                    categoryName = name,
                    categoryDescription = description
                )
                // Save the updated list of categories to the file
                saveCategoriesToFile()
                // Navigate back to DashboardActivity with the user ID
                returnToDashboard(userId)
                return true // Return true since the update is successful
            } else {
                return false // Return false if the category was not found
            }
        }
    }

    private fun updateCategoryInDatabase(categoryId: String, name: String, description: String, userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Categories")
        val updatedData = hashMapOf<String, Any>(
            "categoryName" to name,
            "categoryDescription" to description,
            "userId" to userId // Ensure userId is part of the updated data
        )

        // Update the category in the database
        databaseReference.child(categoryId).updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d("EditCategoryActivity", "Category updated in Firebase Database successfully")
                // Navigate back to DashboardActivity with the user ID
                returnToDashboard(userId)
            }
            .addOnFailureListener { e ->
                // Handle failure, log error message
                Log.e("EditCategoryActivity", "Failed to update category in Firebase Database: ${e.message}")
                Toast.makeText(this, "Failed to update category in database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun returnToDashboard(userId: String) {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            putExtra("userId", userId)
        }
        startActivity(intent)
        finish() // Close the current activity
    }

    private fun saveCategoriesToFile() {
        try {
            FileWriter(File(filesDir, "Categories"), false).use { writer ->
                for (category in categoriesList) {
                    val categoryData =
                        "${category.categoryId}+${category.userId}+${category.categoryName}+${category.categoryDescription}+${category.categoryDate}"
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
/****************************************************************************************
 *    anon (2021) Kotlin arraylist - javatpoint, www.javatpoint.com. Available at: https://www.javatpoint.com/kotlin-arraylist (Accessed: 01 June 2024).


Where array list was used

 ***************************************************************************************

 ***************************************************************************************
 *    Arandilla, A. (2021) Android simple registration and login application tutorial with source code, SourceCodester. Available at: https://www.sourcecodester.com/android/12151/android-simple-registration-and-login-application.html (Accessed: 04 May 2024).

where the functionality for register and login is


 ***************************************************************************************

 ***************************************************************************************
 *    Jansen, R. (2022) A recyclerview with multiple item types in Kotlin, Medium. Available at: https://medium.com/@ruut_j/a-recyclerview-with-multiple-item-types-in-kotlin-80e1e36d93e6 (Accessed: 03 June 2024).

back end - where adapters were used

 ***************************************************************************************

 ***************************************************************************************
 *    Kolahan, A. (2020) How to use vertical recyclerview inside Scrollview in Android, Medium. Available at: https://armanco.medium.com/how-to-use-vertical-recyclerview-inside-scrollview-in-android-b72337285517 (Accessed: 03 June 2024).

front end - xml files using recycler view within scroll view

 ***************************************************************************************

 ***************************************************************************************
 *    Koçer, E. (2023) Get events to your Android app using Google Calendar Api, Get Events to Your Android App Using Google Calendar API. Available at: https://medium.com/@eneskocerr/get-events-to-your-android-app-using-google-calendar-api-4411119cd586 (Accessed: 07 June 2024).

back end - code handling the google calendar integration

 ***************************************************************************************

 ***************************************************************************************
 *    Lopez, A. and Chetan, J. (2017) Android select image from internal / external storage, Stack Overflow. Available at: https://stackoverflow.com/questions/42263816/android-select-image-from-internal-external-storage (Accessed: 08 June 2024).

back end - code for selecting an image

 ***************************************************************************************

 ***************************************************************************************
 *    Mshidlyali, A. (2021) Dashboard UI design in Android, GeeksforGeeks. Available at: https://www.geeksforgeeks.org/dashboard-ui-design-in-android/ (Accessed: 14 May 2024).

front end and back end - where dashboard is applicable and cardview

 ***************************************************************************************

 ***************************************************************************************
 *    Ramesh, R. (2023) Building real-time apps with Firebase Realtime Database and Kotlin, Medium. Available at: https://blog.stackademic.com/building-real-time-apps-with-firebase-realtime-database-and-kotlin-fd368a396e96 (Accessed: 01 June 2024).

back end - code handling the firebase stuff

 ***************************************************************************************

 ***************************************************************************************
 *    Ruhil, P. (2021) Timepicker in Kotlin, GeeksforGeeks. Available at: https://www.geeksforgeeks.org/timepicker-in-kotlin/ (Accessed: 02 June 2024).

back end - code handling the time picker

 ***************************************************************************************

 ***************************************************************************************
 *    Yamunje, C. (2022) Android - create BarChart with Kotlin, GeeksforGeeks. Available at: https://www.geeksforgeeks.org/android-create-barchart-with-kotlin/ (Accessed: 08 June 2024).

back end - code handling the graph

 ***************************************************************************************

 */