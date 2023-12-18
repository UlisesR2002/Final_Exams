package cl.baubau.finalexams

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class QuestionGetter(private val callback: GetterCallback)
{
    //https://opentdb.com/api.php?amount=2&type=multiple
    //https://opentdb.com/api.php?amount=10&difficulty=easy
    //https://opentdb.com/api.php?amount=1&category=20&difficulty=easy&type=multiple

    private val baseAddress: String = "https://opentdb.com/api.php?amount=1"
    private var questionCount: Int = 0;

    init
    {

    }

    fun getQuestion()
    {
        var urlString = baseAddress

        //Addcategory
        urlString+= getCategory()
        urlString+= getDifficult()
        urlString+= "&type=multiple"

        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                performApiRequest(urlString)
            }
            var question = TextToQuestion(result)
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

    fun TextToQuestion(string: String) : Question
    {
        return getTestQuestion()
    }

    fun getCategory() : String
    {
        return "";
    }

    fun getDifficult() : String
    {
        return "";
    }

    private suspend fun performApiRequest(urlString: String): String {
        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"

            return try {
                val responseCode = urlConnection.responseCode
                Log.i("ApiTask", "Response Code: $responseCode")

                val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                response.toString()
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

}