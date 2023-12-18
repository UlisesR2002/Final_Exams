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

class PlayActivity : AppCompatActivity(), GetterCallback{
    private lateinit var sharedPreferences: SharedPreferences
    private var countDownTimer: CountDownTimer? = null
    private var betweenQuestionTimer: CountDownTimer? = null

    private var timeLeftInMillis: Long = 15000 // 15 segundos inicialmente
    private lateinit var timerText: TextView

    private var score: Int = 0
    private var highScore: Int = 0
    private lateinit var scoreTextView: TextView
    private lateinit var highScoreTextView: TextView

    private lateinit var questionTextView: TextView
    private lateinit var questionNumberTextView: TextView

    private lateinit var cardButtonOption1: Button
    private lateinit var cardButtonOption2: Button
    private lateinit var cardButtonOption3: Button
    private lateinit var cardButtonOption4: Button

    //El que obtiene las preguntas
    private lateinit var questionGetter: QuestionGetter
    //La pregunta actual
    private lateinit var actualQuestion: Question

    private var makeResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        sharedPreferences = getSharedPreferences("common_prefs", Context.MODE_PRIVATE)

        highScore =
            sharedPreferences.getInt(OptionActivity.KEY_HIGH_SCORE, 0)

        // Buscar las vistas por sus IDs
        timerText = findViewById(R.id.TimerText)
        questionTextView = findViewById(R.id.cardTextViewQuestionBody)
        questionNumberTextView = findViewById(R.id.cardTextViewNumberQuestion)
        cardButtonOption1 = findViewById(R.id.cardButtonOption1)
        cardButtonOption2 = findViewById(R.id.cardButtonOption2)
        cardButtonOption3 = findViewById(R.id.cardButtonOption3)
        cardButtonOption4 = findViewById(R.id.cardButtonOption4)
        scoreTextView = findViewById(R.id.playScoreTextView)
        highScoreTextView = findViewById(R.id.playHighScoreTextView)

        scoreTextView.text = getString(R.string.play_score_textview, score.toString())
        highScoreTextView.text = getString(R.string.play_high_score_textview, highScore.toString())

        questionGetter = QuestionGetter(this,this)

        // Inicializar los cronómetros
        initializeCountdown()

        // Listener para los botones de respuesta
        setAnswerButtonClickListeners()

        //La primera vez no debemos activar los botones hasta obtener la pregunta
        cardButtonOption1.isEnabled = false
        cardButtonOption2.isEnabled = false
        cardButtonOption3.isEnabled = false
        cardButtonOption4.isEnabled = false

        questionGetter.getQuestion()
    }


    override fun onResume() {
        super.onResume()

        if(!makeResume)
            return
        // Reactivar los cronómetros u otras funciones si es necesario
        countDownTimer?.start()
        // También puedes agregar otras funciones que deban activarse cuando la actividad esté en primer plano

        //Recargar pregunta
        if(::actualQuestion.isInitialized)
            onQuestionGet(actualQuestion)
    }

    override fun onPause() {
        super.onPause()
        // Desactivar los cronómetros u otras funciones si la actividad pasa a segundo plano
        countDownTimer?.cancel()
    }

    private fun initializeCountdown() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                val dialogFragment = MyDialogFragment(score)
                dialogFragment.show(supportFragmentManager, "my_dialog")
                Toast.makeText(this@PlayActivity, "Tiempo agotado", Toast.LENGTH_SHORT).show()
            }
        }

        //2 Segundos entre preguntas
        betweenQuestionTimer = object : CountDownTimer(2000, 2000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() { questionGetter.getQuestion() }
        }
    }

    private fun updateCountdownText() {
        val seconds = timeLeftInMillis / 1000
        val millis = (timeLeftInMillis % 1000) / 10
        val timeFormatted = String.format("%02d:%02d", seconds, millis)
        timerText.text = timeFormatted
    }

    private fun setAnswerButtonClickListeners() {

        cardButtonOption1.setOnClickListener {
            checkAnswerAndStopTimer(cardButtonOption1)
        }

        cardButtonOption2.setOnClickListener {
            checkAnswerAndStopTimer(cardButtonOption2)
        }

        cardButtonOption3.setOnClickListener {
            checkAnswerAndStopTimer(cardButtonOption3)
        }

        cardButtonOption4.setOnClickListener {
            checkAnswerAndStopTimer(cardButtonOption4)
        }
    }

    private fun checkAnswerAndStopTimer(clickedButton: Button) {
        val correctAnswer = actualQuestion.answer

        if (correctAnswer == clickedButton.text.toString()) {
            // Respuesta correcta
            score += 1
            onScoresUpdate()

            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
            checkAndColorButtons(correctAnswer)
            // Puedes hacer otras acciones aquí, como cargar la siguiente pregunta, etc.

            //Empezamos el contador para la siguente pregunta
            betweenQuestionTimer?.start()
        } else {
            // Respuesta incorrecta
            onScoresUpdate()

            Toast.makeText(this, "Wrong, you lose!!!", Toast.LENGTH_SHORT).show()
            checkAndColorButtons(correctAnswer)
            val dialogFragment = MyDialogFragment(score)
            dialogFragment.show(supportFragmentManager, "my_dialog")
        }
    }

    private fun checkAndColorButtons(correctAnswer: String) {
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
        //clickedButton.isEnabled = true
    }

    //Funcion para modificar las puntuaciones y setear sus textos
    private fun onScoresUpdate()
    {
        val scoreText = getString(R.string.play_score_textview, score.toString())
        scoreTextView.text = scoreText

        if (score > highScore)
        {

            OptionActivity.saveHighScore(score,sharedPreferences)
            highScore =
                sharedPreferences.getInt(OptionActivity.KEY_HIGH_SCORE, 0)

            val highScoreText = getString(R.string.play_high_score_textview , highScore.toString())
            highScoreTextView.text = highScoreText
        }
    }


    //Funcion cuando la corrutina obtiene la pregunta
    override fun onQuestionGet(result: Question)
    {
        if(result.question == "Error")
        {
            Toast.makeText(this,"An error as occurred, please try play later", Toast.LENGTH_LONG).show()
            finish()
        }

        if(result.question == "Timeout")
        {
            Toast.makeText(this,"The server did not respond. Please try again later", Toast.LENGTH_LONG).show()
            finish()
            return
        }


        makeResume = true
        println("Obtuvimos la pregunta $result")
        //actualizar
        actualQuestion = result

        questionTextView.text = actualQuestion.question

        questionNumberTextView.text = getString(R.string.gamecard_question_number_textview) + (score + 1).toString()

        cardButtonOption1.text = actualQuestion.options[0]
        cardButtonOption2.text = actualQuestion.options[1]
        cardButtonOption3.text = actualQuestion.options[2]
        cardButtonOption4.text = actualQuestion.options[3]

        //Default color
        cardButtonOption1.setTextColor(ContextCompat.getColor(this, R.color.colorTextTile))
        cardButtonOption2.setTextColor(ContextCompat.getColor(this, R.color.colorTextTile))
        cardButtonOption3.setTextColor(ContextCompat.getColor(this, R.color.colorTextTile))
        cardButtonOption4.setTextColor(ContextCompat.getColor(this, R.color.colorTextTile))

        //Enabled buttons
        cardButtonOption1.isEnabled = true
        cardButtonOption2.isEnabled = true
        cardButtonOption3.isEnabled = true
        cardButtonOption4.isEnabled = true

        //Empezamos contador del juego
        countDownTimer?.start()
    }
}
