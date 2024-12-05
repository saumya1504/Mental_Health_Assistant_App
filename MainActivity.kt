package eu.tutorials.loginsignupsql

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find buttons by ID
        val predictButton: Button = findViewById(R.id.predictButton)
        val trackMoodButton: Button = findViewById(R.id.trackMoodButton)
        val logoutButton: Button = findViewById(R.id.logoutButton)

        // Redirect to ML Model Activity
        predictButton.setOnClickListener {
            val intent = Intent(this, PredictionActivity::class.java)
            startActivity(intent)
        }

        // Redirect to Mood Tracking Activity
        trackMoodButton.setOnClickListener {
            val intent = Intent(this, MoodTrackingActivity::class.java)
            startActivity(intent)
        }

        // Handle Logout Button Click
        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close MainActivity
        }
    }
}
