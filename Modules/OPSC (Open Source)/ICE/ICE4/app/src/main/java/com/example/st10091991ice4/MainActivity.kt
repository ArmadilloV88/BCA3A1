package com.example.st10091991ice4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val excuses = listOf(
        "Aliens abducted me and I had to negotiate my way back.",
        "I accidentally joined a circus and couldn't find my way out.",
        "I was trapped in a time loop and lost all sense of reality.",
        "My cat hypnotized me and I couldn't break free.",
        "I fell into a black hole of procrastination and couldn't escape.",
        "I got lost in a parallel universe where time moves faster.",
        "My house was invaded by squirrels, and I had to defend my territory.",
        "A rogue AI stole my keyboard and demanded ransom.",
        "I discovered a secret underground civilization and got caught up in their festivities.",
        "I was recruited by a secret society of magicians and had to attend their emergency meeting."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generateExcuseButton: Button = findViewById(R.id.generateExcuseButton)
        val excuseTextView: TextView = findViewById(R.id.excuseTextView)

        generateExcuseButton.setOnClickListener {
            val randomIndex = Random.nextInt(excuses.size)
            val randomExcuse = excuses[randomIndex]
            excuseTextView.text = randomExcuse
        }
    }
}