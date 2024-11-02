package ma.ensa.projet


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        val tvScore = findViewById<TextView>(R.id.tvScore)
        tvScore.text = "Your Score: $score/10"
    }
}
