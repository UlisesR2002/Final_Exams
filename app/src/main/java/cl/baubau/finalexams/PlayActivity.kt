package cl.baubau.finalexams

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PlayActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        sharedPreferences = getSharedPreferences("common_prefs", Context.MODE_PRIVATE)

        val savedDifficulty = sharedPreferences.getString(OptionActivity.KEY_DIFFICULTY, OptionActivity.DEFAULT_DIFFICULTY)

        val (selectedCategories, categoryNames) = OptionActivity.loadSelectedCategories(sharedPreferences)

        println(savedDifficulty)
        println("Category Names: ${categoryNames.joinToString(", ")}")
        println("Category Bool: ${selectedCategories.joinToString(", ")}")
    }
}