package cl.baubau.finalexams

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OptionActivity : AppCompatActivity() {
    private lateinit var easyDifficultyButton: Button
    private lateinit var mediumDifficultyButton: Button
    private lateinit var hardDifficultyButton: Button
    private lateinit var selectCategoryButton: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        easyDifficultyButton = findViewById(R.id.optionEasyButton)
        mediumDifficultyButton = findViewById(R.id.optionMediumButton)
        hardDifficultyButton = findViewById(R.id.optionHardButton)
        selectCategoryButton = findViewById(R.id.optionSelectCategoryButton)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Obtener la preferencia de dificultad almacenada
        val savedDifficulty = sharedPreferences.getString(KEY_DIFFICULTY, DEFAULT_DIFFICULTY)

        // Restablecer el color de los botones según la preferencia almacenada
        when (savedDifficulty) {
            DIFFICULTY_EASY -> selectDifficulty(easyDifficultyButton)
            DIFFICULTY_MEDIUM -> selectDifficulty(mediumDifficultyButton)
            DIFFICULTY_HARD -> selectDifficulty(hardDifficultyButton)
            else -> selectDifficulty(easyDifficultyButton) // Fallback a easy si la preferencia no es válida
        }

        easyDifficultyButton.setOnClickListener {
            saveAndSelectDifficulty(DIFFICULTY_EASY)
        }

        mediumDifficultyButton.setOnClickListener {
            saveAndSelectDifficulty(DIFFICULTY_MEDIUM)
        }

        hardDifficultyButton.setOnClickListener {
            saveAndSelectDifficulty(DIFFICULTY_HARD)
        }

        selectCategoryButton.setOnClickListener {
            showCategorySelectionDialog()
        }
    }

    private fun saveAndSelectDifficulty(difficulty: String) {
        // Guardar la preferencia de dificultad
        with(sharedPreferences.edit()) {
            putString(KEY_DIFFICULTY, difficulty)
            apply()
        }

        // Restablecer el color de los botones según la preferencia seleccionada
        when (difficulty) {
            DIFFICULTY_EASY -> selectDifficulty(easyDifficultyButton)
            DIFFICULTY_MEDIUM -> selectDifficulty(mediumDifficultyButton)
            DIFFICULTY_HARD -> selectDifficulty(hardDifficultyButton)
        }
    }

    private fun selectDifficulty(selectedButton: Button) {
        // Restablecer el color predeterminado para todos los botones
        easyDifficultyButton.backgroundTintList = getColorStateList(R.color.colorTabButtonBackground)
        mediumDifficultyButton.backgroundTintList = getColorStateList(R.color.colorTabButtonBackground)
        hardDifficultyButton.backgroundTintList = getColorStateList(R.color.colorTabButtonBackground)

        // Establecer el color del botón seleccionado
        selectedButton.backgroundTintList = getColorStateList(R.color.colorTabButtonBackgroundSelected)
    }
    private fun saveSelectedCategories(selectedCategories: BooleanArray) {
        with(sharedPreferences.edit()) {
            for (i in selectedCategories.indices) {
                putBoolean("$KEY_CATEGORY$i", selectedCategories[i])
            }
            apply()
        }
    }

    private fun loadSelectedCategories(): BooleanArray {
        val categories = arrayOf(
            "Any category", "General knowledge", "Books", "Film", "Music", "Musical & Theatres", "Television", "Video games",
            "Board games", "Science & Nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History",
            "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Japanese Anime & Manga", "Cartoon & Animations"
        )

        val selectedCategories = BooleanArray(categories.size) { false }

        for (i in 0 until categories.size) {
            selectedCategories[i] = sharedPreferences.getBoolean("$KEY_CATEGORY$i", false)
        }

        return selectedCategories
    }
    private fun showCategorySelectionDialog() {
        val categories = arrayOf(
            "Any category", "General knowledge", "Books", "Film", "Music", "Musical & Theatres", "Television", "Video games",
            "Board games", "Science & Nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History",
            "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Japanese Anime & Manga", "Cartoon & Animations"
        )

        val selectedCategories = loadSelectedCategories()

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.option_select_category_button)
            .setMultiChoiceItems(categories, selectedCategories) { _, which, isChecked ->
                selectedCategories[which] = isChecked
            }
            .setPositiveButton("Accept") { _, _ ->
                // Aquí puedes hacer algo con los temas seleccionados
                // selectedThemes contiene un arreglo de booleanos que indica cuáles temas están seleccionados
                // Puedes usar este arreglo para realizar acciones específicas en base a los temas seleccionados
                saveSelectedCategories(selectedCategories)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Cancelar, no se realiza ninguna acción
            }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        private const val KEY_DIFFICULTY = "difficulty_preference"
        private const val DEFAULT_DIFFICULTY = "easy"
        private const val DIFFICULTY_EASY = "easy"
        private const val DIFFICULTY_MEDIUM = "medium"
        private const val DIFFICULTY_HARD = "hard"

        private const val KEY_CATEGORY = "category_preference"
    }
}