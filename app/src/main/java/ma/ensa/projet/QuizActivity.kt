package ma.ensa.projet

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ma.ensa.projet.models.Question
import ma.ensa.projet.network.RetrofitInstance

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var option1Button: Button
    private lateinit var option2Button: Button
    private lateinit var option3Button: Button
    private lateinit var option4Button: Button
    private var score = 0
    private var currentQuestionIndex = 0
    private var questions: List<Question>? = null // Liste nullable pour éviter une NullPointerException

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Liaison avec les vues
        questionTextView = findViewById(R.id.tvQuestion)
        option1Button = findViewById(R.id.btnAnswer1)
        option2Button = findViewById(R.id.btnAnswer2)
        option3Button = findViewById(R.id.btnAnswer3)
        option4Button = findViewById(R.id.btnAnswer4)

        // Charger les questions sans bloquer l'interface utilisateur
        lifecycleScope.launch {
            loadQuestions()  // Charger les questions en arrière-plan avec coroutines
        }

        // Écouteurs pour les boutons
        option1Button.setOnClickListener { checkAnswer(option1Button.text.toString()) }
        option2Button.setOnClickListener { checkAnswer(option2Button.text.toString()) }
        option3Button.setOnClickListener { checkAnswer(option3Button.text.toString()) }
        option4Button.setOnClickListener { checkAnswer(option4Button.text.toString()) }
    }

    private suspend fun loadQuestions() {
        try {
            val response = RetrofitInstance.api.getQuestions()
            questions = response.results  // Récupère la liste des questions

            if (!questions.isNullOrEmpty()) {
                // Si la liste des questions n'est pas vide, afficher la première question
                withContext(Dispatchers.Main) {
                    displayQuestion()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@QuizActivity, "Aucune question disponible.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@QuizActivity, "Erreur de récupération des questions : ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayQuestion() {
        if (questions != null && currentQuestionIndex < questions!!.size) {
            val question = questions!![currentQuestionIndex]
            // Décoder le texte HTML
            questionTextView.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY)
            option1Button.text = Html.fromHtml(question.incorrect_answers[0], Html.FROM_HTML_MODE_LEGACY)
            option2Button.text = Html.fromHtml(question.incorrect_answers[1], Html.FROM_HTML_MODE_LEGACY)
            option3Button.text = Html.fromHtml(question.incorrect_answers[2], Html.FROM_HTML_MODE_LEGACY)
            option4Button.text = Html.fromHtml(question.correct_answer, Html.FROM_HTML_MODE_LEGACY)

            // Reset button colors
            resetButtonColors()
        } else {
            // Gérer le cas où il n'y a plus de questions ou si elles ne sont pas chargées
            if (questions == null) {
                Toast.makeText(this, "Les questions ne sont pas encore chargées.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Quiz terminé! Score: $score", Toast.LENGTH_LONG).show()
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("score", score)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun resetButtonColors() {
        val defaultColor = resources.getColor(android.R.color.darker_gray) // Default button color
        option1Button.setBackgroundColor(defaultColor)
        option2Button.setBackgroundColor(defaultColor)
        option3Button.setBackgroundColor(defaultColor)
        option4Button.setBackgroundColor(defaultColor)
    }

    private fun checkAnswer(selectedAnswer: String) {
        if (questions != null) {
            val correctAnswer = questions!![currentQuestionIndex].correct_answer

            // Get the right button based on the correct answer
            val correctButton: Button = when (correctAnswer) {
                option1Button.text.toString() -> option1Button
                option2Button.text.toString() -> option2Button
                option3Button.text.toString() -> option3Button
                option4Button.text.toString() -> option4Button
                else -> option1Button // Just a fallback to avoid null pointer
            }

            if (selectedAnswer == correctAnswer) {
                score++
                // Change background to green for correct answer
                correctButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                Toast.makeText(this, "Bonne réponse!", Toast.LENGTH_SHORT).show()
            } else {
                // Change background to red for the selected answer
                when (selectedAnswer) {
                    option1Button.text.toString() -> option1Button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    option2Button.text.toString() -> option2Button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    option3Button.text.toString() -> option3Button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                    option4Button.text.toString() -> option4Button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light))
                }
                // Show the correct button as well
                correctButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                Toast.makeText(this, "Mauvaise réponse!", Toast.LENGTH_SHORT).show()
            }

            // After a brief delay, move to the next question
            currentQuestionIndex++
            lifecycleScope.launch {
                delay(1000) // 1 second delay
                displayQuestion()
            }
        } else {
            Toast.makeText(this, "Impossible de vérifier la réponse, les questions ne sont pas chargées.", Toast.LENGTH_SHORT).show()
        }
    }
}