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

        sharedPreferences = getSharedPreferences("common_prefs", Context.MODE_PRIVATE)

        val (selectedCategories, categoryNames) = loadSelectedCategories(sharedPreferences)
        println("Category Names: ${categoryNames.joinToString(", ")}")
        println("Category Bool: ${selectedCategories.joinToString(", ")}")


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

    private fun saveSelectedCategories(selectedCategories: BooleanArray, categoryNames: Array<String>) {
        val selectedCategoryNames = categoryNames.filterIndexed { index, _ ->
            selectedCategories[index]
        }.toTypedArray()

        with(sharedPreferences.edit()) {
            for (i in 0 until selectedCategories.size) {
                putBoolean("${KEY_CATEGORY}$i", selectedCategories[i])
            }
            putString(KEY_CATEGORY_NAMES, selectedCategoryNames.joinToString(","))
            apply()
        }
    }

    private fun showCategorySelectionDialog() {
        val categories = arrayOf(
            "Any category", "General knowledge", "Books", "Film", "Music", "Musical & Theatres", "Television", "Video games",
            "Board games", "Science & Nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History",
            "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Japanese Anime & Manga", "Cartoon & Animations"
        )

        val (selectedCategories, _) = loadSelectedCategories(sharedPreferences)

        val builder = AlertDialog.Builder(this)
        val dialog: AlertDialog = builder.setTitle(R.string.option_select_category_button)
            .setMultiChoiceItems(categories, selectedCategories) { _, which, isChecked ->
                if (which == 0) {
                    // Si seleccionas "Any Category", desmarcar todas las demás categorías
                    for (i in 1 until selectedCategories.size) {
                        selectedCategories[i] = false
                    }
                } else {
                    // Si seleccionas cualquier otra categoría, desmarcar "Any Category"
                    selectedCategories[0] = false
                }

                selectedCategories[which] = isChecked
            }
            .setPositiveButton("Accept") { _, _ ->
                saveSelectedCategories(selectedCategories, categories)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Cancelar, no se realiza ninguna acción
            }
            .create()

        dialog.show()
    }
    companion object {
        const val KEY_DIFFICULTY = "difficulty_preference"
        const val DEFAULT_DIFFICULTY = "easy"
        private const val DIFFICULTY_EASY = "easy"
        private const val DIFFICULTY_MEDIUM = "medium"
        private const val DIFFICULTY_HARD = "hard"

        private const val KEY_CATEGORY = "category_preference"
        const val KEY_CATEGORY_NAMES = "category_names_preference"

        fun loadSelectedCategories(sharedPreferences: SharedPreferences): Pair<BooleanArray, Array<String>> {
            val categories = arrayOf(
                "Any category", "General knowledge", "Books", "Film", "Music", "Musical & Theatres", "Television", "Video games",
                "Board games", "Science & Nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History",
                "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Japanese Anime & Manga", "Cartoon & Animations"
            )

            // Cargar los valores de las categorías desde SharedPreferences
            val selectedCategories = BooleanArray(categories.size) {
                sharedPreferences.getBoolean("${KEY_CATEGORY}$it", it == 0) // Establecer "Any category" como true por defecto
            }

            val namesString = sharedPreferences.getString(KEY_CATEGORY_NAMES, null)
            val categoryNames = if (!namesString.isNullOrBlank()) {
                namesString.split(",").toTypedArray()
            } else {
                arrayOf("Any category")
            }

            return Pair(selectedCategories, categoryNames)
        }
    }
}