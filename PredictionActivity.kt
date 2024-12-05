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

class PredictionActivity : AppCompatActivity() {

    private lateinit var outputAnswer: TextView

    // Retrofit API Interface
    interface ApiService {
        @POST("/get_answer")
        fun getAnswer(@Body requestBody: Map<String, String>): Call<ResponseBody>
    }

    // Retrofit Instance Creation
    private fun createRetrofitService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ba77-35-193-215-98.ngrok-free.app/") // Replace with your ngrok URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        val inputQuestion: EditText = findViewById(R.id.inputQuestion)
        val predictButton: Button = findViewById(R.id.predictButton)
        val homePageButton: Button = findViewById(R.id.homePageButton) // Home Page Button
        outputAnswer = findViewById(R.id.outputAnswer)

        // Home Page Button Logic
        homePageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }

        predictButton.setOnClickListener {
            val question = inputQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Please enter a question!", Toast.LENGTH_SHORT).show()
            } else {
                predict(question)
            }
        }
    }

    private fun predict(question: String) {
        val apiService = createRetrofitService()
        val requestBody = mapOf("question" to question)

        apiService.getAnswer(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        response.body()?.string()?.let { jsonResponse ->
                            val json = JSONObject(jsonResponse)
                            val answer = json.optString("answer", "No answer provided")
                            val similarity = json.optDouble("similarity", 0.0)
                            outputAnswer.text = "Answer: $answer\nSimilarity: $similarity"
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@PredictionActivity, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PredictionActivity, "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@PredictionActivity, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
