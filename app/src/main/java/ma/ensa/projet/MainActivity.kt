package ma.ensa.projet


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var option4Button: Button
    private var score = 0
    private var currentQuestionIndex = 0
    private lateinit var questions: List<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionTextView = findViewById(R.id.questionTextView)
        option1Button = findViewById(R.id.option1Button)
        option2Button = findViewById(R.id.option2Button)
        option3Button = findViewById(R.id.option3Button)
        option4Button = findViewById(R.id.option4Button)

        // Utiliser coroutines pour récupérer des données
        lifecycleScope.launch {
            loadQuestions()
            displayQuestion()
        }

        option1Button.setOnClickListener { checkAnswer(0) }
        option2Button.setOnClickListener { checkAnswer(1) }
        option3Button.setOnClickListener { checkAnswer(2) }
        option4Button.setOnClickListener { checkAnswer(3) }
    }

    private suspend fun loadQuestions() {
        // Utiliser Retrofit pour l'appel réseau
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val quizApi = retrofit.create(QuizApi::class.java)

        // Appel réseau asynchrone avec coroutines
        withContext(Dispatchers.IO) {
            val response = quizApi.getQuestions()
            questions = response.results
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionTextView.text = question.question
            option1Button.text = question.incorrectAnswers[0]
            option2Button.text = question.incorrectAnswers[1]
            option3Button.text = question.incorrectAnswers[2]
            option4Button.text = question.correctAnswer
        } else {
            Toast.makeText(this, "Quiz terminé! Score: $score", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAnswer(selectedOptionIndex: Int) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        val selectedAnswer = when (selectedOptionIndex) {
            0 -> option1Button.text.toString()
            1 -> option2Button.text.toString()
            2 -> option3Button.text.toString()
            else -> option4Button.text.toString()
        }

        if (selectedAnswer == correctAnswer) {
            score++
            Toast.makeText(this, "Bonne réponse!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Mauvaise réponse!", Toast.LENGTH_SHORT).show()
        }

        currentQuestionIndex++
        displayQuestion()
    }
}

// Interface Retrofit pour l'API
interface QuizApi {
    @GET("api.php?amount=10&type=multiple")
    suspend fun getQuestions(): QuizResponse
}

// Modèles de données
data class QuizResponse(val results: List<Question>)

data class Question(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)
