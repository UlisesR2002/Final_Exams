package cl.baubau.finalexams

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class QuestionGetter(private val callback: GetterCallback, private val context: Context)
{
    //https://opentdb.com/api.php?amount=2&type=multiple
    //https://opentdb.com/api.php?amount=10&difficulty=easy
    //https://opentdb.com/api.php?amount=1&category=20&difficulty=easy&type=multiple

    private val baseAddress: String = "https://opentdb.com/api.php?amount=1"
    private var questionCount: Int = 0;
    private var difficulty = mutableListOf<String>()
    private val questionHistory: MutableSet<String> = mutableSetOf()
    private lateinit var sharedPreferences: SharedPreferences
    init
    {
        println("--Inicializando Getter--")
        val easy = "&difficulty=easy"
        val medium = "&difficulty=medium"
        val hard = "&difficulty=hard"

        sharedPreferences = context.getSharedPreferences("common_prefs", Context.MODE_PRIVATE)
        val savedDifficulty = sharedPreferences.getString(OptionActivity.KEY_DIFFICULTY, OptionActivity.DEFAULT_DIFFICULTY)

        difficulty = when(savedDifficulty) {
            "easy" -> mutableListOf(easy,easy,easy,easy,medium,hard)
            "medium" -> mutableListOf(easy,easy,medium,medium,hard,hard)
            "hard" -> mutableListOf(easy,medium,medium,hard,hard,hard)
            else -> mutableListOf(easy,easy,medium,medium,hard,hard)
        }

        println("Valor dificultad:$savedDifficulty")

    }
    fun getQuestion()
    {
        println("--intentando obtener string de la API--")
        var urlString = baseAddress

        //Addcategory
        urlString+= getCategory()
        urlString+= getDifficult()
        urlString+= "&type=multiple"
        println("URL: $urlString")


        GlobalScope.launch(Dispatchers.Main)
        {
            var question = Question("","", mutableListOf())

            val maxIntentos = 3 // Puedes ajustar esto según sea necesario
            val tiempoEsperaMillis = 5000 // Tiempo de espera en milisegundos antes de volver a intentarlo
            var intentos = 0

            do {
                val result = withContext(Dispatchers.IO)
                {
                    performApiRequest(urlString)
                }
                println("Valor obtenido solicitud api: $result")

                if (result == "429")
                {
                    Toast.makeText(context, "Too many question request, please wait a moment...",Toast.LENGTH_LONG).show()
                    delay(tiempoEsperaMillis.toLong())
                }
                else if(result == "Timeout")
                {
                    question = Question("Timeout","", mutableListOf())
                    break
                }
                else
                {
                    question = textToQuestion(result)
                }

                intentos++
            } while (result == "429" && intentos < maxIntentos)
            callback.onQuestionGet(question)
        }
    }


    fun getTestQuestion() : Question
    {
        val options = mutableListOf<String>()
        options.add("Water")
        options.add("Cloro")
        options.add("More blood")
        options.add("ADN")

        options.shuffle()

        val question = Question(
            "How to clean blood?",
            "More blood",
            options
            )

        question.options = options
        return question;
    }

    fun textToQuestion(string: String): Question {
        println("--Procesando texto a pregunta--")
        // Eliminar espacios en blanco al principio y al final de la cadena
        val trimmedString = string.trim()

        if (trimmedString.isEmpty())
        {
            println("La cadena JSON está vacía.")
            return Question("Error", "", mutableListOf())
        }

        val jsonObject = JSONObject(trimmedString)
        val resultsArray = jsonObject.getJSONArray("results")

        var question = "question"
        var correctAnswer = "correctAnswer"
        var incorrectAnswers = mutableListOf<String>()

        try {
            if (resultsArray.length() > 0) {
                // Obtener la pregunta
                val firstQuestion = resultsArray.getJSONObject(0)

                // Obtener los valores específicos
                question =
                    Html.fromHtml(firstQuestion.getString("question"), Html.FROM_HTML_MODE_LEGACY)
                        .toString()
                correctAnswer = firstQuestion.getString("correct_answer")

                println("Procesando preguntas incorrectas")
                incorrectAnswers.add(correctAnswer)
                incorrectAnswers.add(firstQuestion.getJSONArray("incorrect_answers").getString(0))
                incorrectAnswers.add(firstQuestion.getJSONArray("incorrect_answers").getString(1))
                incorrectAnswers.add(firstQuestion.getJSONArray("incorrect_answers").getString(2))



                incorrectAnswers.shuffle()

                println("Procesadas preguntas incorrectas")

                // Mostrar los resultados
                println("Pregunta: $question")
                println("Respuesta Correcta: $correctAnswer")
                println("Respuestas Incorrectas: $incorrectAnswers")
            } else {
                println("No hay preguntas en el JSON.")
            }
        }
        catch(e: Exception)
        {
            println("ERROR QUESTION CREATION")
            e.printStackTrace()
            getTestQuestion()
        }
        return Question(
            question,
            correctAnswer,
            incorrectAnswers
        )
    }

    fun getCategory() : String
    {
        println("--Seleccionando categoria--")
        val (selectedCategories, categoryNames) = OptionActivity.loadSelectedCategories(sharedPreferences)

        //Si la primera esta seleccionada (Any)
        if(selectedCategories[0])
        {
            println("Any fue seleccionado")
            return ""
        }

        var categories = mutableListOf<Int>()

        //Las categorias empiezan desde el nueve, no tengo idea no me pregunten XD
        var i = 8

        for (selection in selectedCategories)
        {
            if(selection)
            {
                categories.add(i)
            }
            i++
        }
        //Randomizar
        categories.shuffle()

        println("La categoria " + categories[0].toString() +   " fue seleccionada")

        return "&category="+ categories[0].toString()
    }

    fun getDifficult() : String
    {
        val result = difficulty[questionCount]
        println("--Dificultad pregunta que se quiere obtener: $result")

        questionCount++
        if(questionCount >= difficulty.count())
        {
            questionCount = 0;
        }

        return result
    }

    private suspend fun performApiRequest(urlString: String): String {
        var result: String
        try {
            withContext(NonCancellable) {
                //Desincronizamos la coroutine para que no se quede estancada esperando a la api
                val deferredResult = GlobalScope.async {
                    val url = URL(urlString)
                    val urlConnection = url.openConnection() as HttpURLConnection
                    urlConnection.requestMethod = "GET"

                    val responseCode = urlConnection.responseCode
                    Log.i("ApiTask", "Response Code: $responseCode")

                    if (responseCode == 429) {
                        "429"
                    } else {
                        val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        val response = StringBuilder()
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        response.toString()
                    }
                }

                //En 5 segundos maximo intentamos obtener el resultado
                result = try {
                    withTimeout(5000) {
                        deferredResult.await()
                    }
                } catch (e: TimeoutCancellationException) {
                    "Timeout"
                }
            }
        } catch (e: Exception) {
            println("ERROR APIREQUEST")
            e.printStackTrace()
            result = ""
        }

        return result
    }

}