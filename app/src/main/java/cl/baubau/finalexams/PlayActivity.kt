package cl.baubau.finalexams

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PlayActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 15000 // 15 segundos inicialmente
    private lateinit var TimerText: TextView
    private lateinit var cardButtonOption1: Button
    private lateinit var cardButtonOption2: Button
    private lateinit var cardButtonOption3: Button
    private lateinit var cardButtonOption4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        sharedPreferences = getSharedPreferences("common_prefs", Context.MODE_PRIVATE)

        val savedDifficulty =
            sharedPreferences.getString(OptionActivity.KEY_DIFFICULTY, OptionActivity.DEFAULT_DIFFICULTY)

        val (selectedCategories, categoryNames) = OptionActivity.loadSelectedCategories(sharedPreferences)

        println(savedDifficulty)
        println("Category Names: ${categoryNames.joinToString(", ")}")
        println("Category Bool: ${selectedCategories.joinToString(", ")}")

        // Buscar las vistas por sus IDs
        TimerText = findViewById(R.id.TimerText)
        cardButtonOption1 = findViewById(R.id.cardButtonOption1)
        cardButtonOption2 = findViewById(R.id.cardButtonOption2)
        cardButtonOption3 = findViewById(R.id.cardButtonOption3)
        cardButtonOption4 = findViewById(R.id.cardButtonOption4)

        // Iniciar el cronómetro
        startCountdown()

        // Listener para los botones de respuesta
        setAnswerButtonClickListeners()
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                // Aquí puedes manejar lo que sucede cuando el tiempo se agota
                Toast.makeText(this@PlayActivity, "Tiempo agotado", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun updateCountdownText() {
        val seconds = timeLeftInMillis / 1000
        val millis = (timeLeftInMillis % 1000) / 10
        val timeFormatted = String.format("%02d:%02d", seconds, millis)
        TimerText.text = timeFormatted
    }

    private fun setAnswerButtonClickListeners() {
        val correctAnswer = "Option A" // Reemplaza con la respuesta correcta

        cardButtonOption1.setOnClickListener {
            checkAnswerAndStopTimer(correctAnswer, cardButtonOption1)
        }

        cardButtonOption2.setOnClickListener {
            checkAnswerAndStopTimer(correctAnswer, cardButtonOption2)
        }

        cardButtonOption3.setOnClickListener {
            checkAnswerAndStopTimer(correctAnswer, cardButtonOption3)
        }

        cardButtonOption4.setOnClickListener {
            checkAnswerAndStopTimer(correctAnswer, cardButtonOption4)
        }
    }

    private fun checkAnswerAndStopTimer(correctAnswer: String, clickedButton: Button) {
        if (correctAnswer == clickedButton.text.toString()) {
            // Respuesta correcta
            Toast.makeText(this, "Correcto", Toast.LENGTH_SHORT).show()
            checkAndColorButtons(correctAnswer,clickedButton)
            // Puedes hacer otras acciones aquí, como cargar la siguiente pregunta, etc.
        } else {
            // Respuesta incorrecta
            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
            checkAndColorButtons(correctAnswer,clickedButton)
            // Puedes hacer otras acciones aquí, como mostrar la respuesta correcta, etc.
        }
    }

    private fun checkAndColorButtons(correctAnswer: String,clickedButton: Button) {
        // Detener el cronómetro
        countDownTimer?.cancel()

        if(correctAnswer == cardButtonOption1.text.toString()){
            cardButtonOption1.setTextColor(ContextCompat.getColor(this, R.color.colorCorrect))
        }else{
            cardButtonOption1.setTextColor(ContextCompat.getColor(this, R.color.colorIncorrect))
        }

        if(correctAnswer == cardButtonOption2.text.toString()){
            cardButtonOption2.setTextColor(ContextCompat.getColor(this, R.color.colorCorrect))
        }else{
            cardButtonOption2.setTextColor(ContextCompat.getColor(this, R.color.colorIncorrect))
        }

        if(correctAnswer == cardButtonOption3.text.toString()){
            cardButtonOption3.setTextColor(ContextCompat.getColor(this, R.color.colorCorrect))
        }else{
            cardButtonOption3.setTextColor(ContextCompat.getColor(this, R.color.colorIncorrect))
        }

        if(correctAnswer == cardButtonOption4.text.toString()){
            cardButtonOption4.setTextColor(ContextCompat.getColor(this, R.color.colorCorrect))
        }else{
            cardButtonOption4.setTextColor(ContextCompat.getColor(this, R.color.colorIncorrect))
        }

        // Desactivar todos los botones
        cardButtonOption1.isEnabled = false
        cardButtonOption2.isEnabled = false
        cardButtonOption3.isEnabled = false
        cardButtonOption4.isEnabled = false

        // Activar solo el botón clickeado (opcional)
        clickedButton.isEnabled = true
    }
}
