package ma.ensa.projet


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Délai de 2 secondes avant de passer à l'activité suivante
        GlobalScope.launch {
            delay(2500)
            startActivity(Intent(this@SplashActivity, QuizActivity::class.java))
            finish()
        }
    }
}
