package com.example.ice3_st10092275

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
fun main(){
    println("Please enter a number:")
    val number = readln().toIntOrNull()

    if(number == null){
        println("Please enter a number")
    }else{
        when{
            number > 0 -> println("Your number is a positive")
            number < 0 -> println("Your number is a negative")
            else -> println("Your number is a zero")
        }
    }
}