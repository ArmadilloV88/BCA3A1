package com.example.st10091991ice3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.st10091991ice3.ui.theme.ST10091991ICE3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ST10091991ICE3Theme {
                IceCreamApp()
            }
        }
    }
}

@Composable
fun IceCreamApp() {
    var selectedMenu by remember { mutableStateOf(MenuOption.DETERMINE_NUMBER) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Menu(onMenuSelected = { selectedMenu = it })

        when (selectedMenu) {
            MenuOption.DETERMINE_NUMBER -> NumberAnalyzer()
            MenuOption.CALCULATE_FACTORIAL -> FactorialCalculator()
            MenuOption.CHECK_PALINDROME -> PalindromeChecker()
            MenuOption.CALCULATE_GEOMETRY -> GeometryCalculator()
        }
    }
}

@Composable
fun Menu(onMenuSelected: (MenuOption) -> Unit) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Select an option:", style = MaterialTheme.typography.bodyMedium)

        MenuOption.values().forEach { option ->
            Button(onClick = { onMenuSelected(option) }) {
                Text(option.title)
            }
        }
    }
}
enum class MenuOption(val title: String) {
    DETERMINE_NUMBER("Determine Number"),
    CALCULATE_FACTORIAL("Calculate Factorial"),
    CHECK_PALINDROME("Check Palindrome"),
    CALCULATE_GEOMETRY("Calculate Geometry")
}

@Composable
fun NumberAnalyzer() {
    var number by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column {
        TextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Enter a number") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { result = analyzeNumber(number.toInt()) }
            )
        )

        Button(onClick = {
            result = analyzeNumber(number.toInt())
        }) {
            Text("Analyze")
        }

        Text(result)
    }
}

fun analyzeNumber(number: Int): String {
    return when {
        number > 0 -> "The number is positive."
        number < 0 -> "The number is negative."
        else -> "The number is zero."
    }
}

@Composable
fun FactorialCalculator() {
    var number by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column {
        TextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Enter a positive integer") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { result = calculateFactorial(number.toInt()) }
            )
        )

        Button(onClick = {
            result = calculateFactorial(number.toInt())
        }) {
            Text("Calculate Factorial")
        }

        Text(result)
    }
}

fun calculateFactorial(number: Int): String {
    var factorial = 1
    for (i in 1..number) {
        factorial *= i
    }
    return "The factorial of $number is $factorial."
}

@Composable
fun PalindromeChecker() {
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column {
        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Enter a string") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { result = if (isPalindrome(input)) {
                    "The string \"$input\" is a palindrome."
                } else {
                    "The string \"$input\" is not a palindrome."
                } }
            )
        )

        Button(onClick = {
            result = if (isPalindrome(input)) {
                "The string \"$input\" is a palindrome."
            } else {
                "The string \"$input\" is not a palindrome."
            }
        }) {
            Text("Check Palindrome")
        }

        Text(result)
    }
}

fun isPalindrome(input: String): Boolean {
    val cleanInput = input.replace("\\s+".toRegex(), "").toLowerCase()
    return cleanInput == cleanInput.reversed()
}

@Composable
fun GeometryCalculator() {
    var radius by remember { mutableStateOf("") }
    var circleAreaResult by remember { mutableStateOf("") }
    var sphereVolumeResult by remember { mutableStateOf("") }

    Column {
        TextField(
            value = radius,
            onValueChange = { radius = it },
            label = { Text("Enter the radius") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    val r = radius.toDouble()
                    circleAreaResult = "The area of the circle with radius $r is ${calculateCircleArea(r)}."
                    sphereVolumeResult = "The volume of the sphere with radius $r is ${calculateSphereVolume(r)}."
                }
            )
        )

        Button(onClick = {
            val r = radius.toDouble()
            circleAreaResult = "The area of the circle with radius $r is ${calculateCircleArea(r)}."
            sphereVolumeResult = "The volume of the sphere with radius $r is ${calculateSphereVolume(r)}."
        }) {
            Text("Calculate")
        }

        Text(circleAreaResult)
        Text(sphereVolumeResult)
    }
}

fun calculateCircleArea(radius: Double): Double {
    return Math.PI * radius * radius
}

fun calculateSphereVolume(radius: Double): Double {
    return (4.0 / 3.0) * Math.PI * radius * radius * radius
}

@Preview(showBackground = true)
@Composable
fun IceCreamAppPreview() {
    ST10091991ICE3Theme {
        IceCreamApp()
    }
}