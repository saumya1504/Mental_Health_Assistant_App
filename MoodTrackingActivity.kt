package eu.tutorials.loginsignupsql

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

class MoodTrackingActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    interface ApiService {
        @POST("/predict")
        fun predictMentalState(@Body requestBody: Map<String, Float>): Call<ResponseBody>
    }

    private fun createRetrofitService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ec4c-34-67-175-195.ngrok-free.app/") // Replace with your ngrok public URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_tracking)

        val sleepHoursInput: EditText = findViewById(R.id.sleepHoursInput)
        val workHoursInput: EditText = findViewById(R.id.workHoursInput)
        val socialInteractionsInput: EditText = findViewById(R.id.socialInteractionsInput)
        val stressLevelInput: EditText = findViewById(R.id.stressLevelInput)
        val screenTimeInput: EditText = findViewById(R.id.screenTimeInput)
        val predictButton: Button = findViewById(R.id.predictButton)
        val homePageButton: Button = findViewById(R.id.homePageButton)
        resultTextView = findViewById(R.id.resultTextView)

        homePageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        predictButton.setOnClickListener {
            val sleepHours = sleepHoursInput.text.toString().toFloatOrNull()
            val workHours = workHoursInput.text.toString().toFloatOrNull()
            val socialInteractions = socialInteractionsInput.text.toString().toFloatOrNull()
            val stressLevel = stressLevelInput.text.toString().toFloatOrNull()
            val screenTime = screenTimeInput.text.toString().toFloatOrNull()

            if (sleepHours == null || workHours == null || socialInteractions == null || stressLevel == null || screenTime == null) {
                Toast.makeText(this, "Please fill all fields with valid numbers", Toast.LENGTH_SHORT).show()
            } else {
                predictMentalState(sleepHours, workHours, socialInteractions, stressLevel, screenTime)
            }
        }
    }

    private fun predictMentalState(
        sleepHours: Float,
        workHours: Float,
        socialInteractions: Float,
        stressLevel: Float,
        screenTime: Float
    ) {
        val apiService = createRetrofitService()
        val requestBody = mapOf(
            "sleep_hours" to sleepHours,
            "work_hours" to workHours,
            "social_interactions" to socialInteractions,
            "stress_level" to stressLevel,
            "screen_time" to screenTime
        )

        apiService.predictMentalState(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { jsonResponse ->
                        val json = JSONObject(jsonResponse)
                        val mentalState = json.optString("mental_state", "Unknown")
                        resultTextView.text = "Predicted Mental State: $mentalState"
                    }
                } else {
                    Toast.makeText(this@MoodTrackingActivity, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MoodTrackingActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
