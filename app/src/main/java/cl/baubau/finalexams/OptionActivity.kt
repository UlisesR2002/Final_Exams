package cl.baubau.finalexams

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OptionActivity : AppCompatActivity() {
    private lateinit var easyDifficultyButton: Button
    private lateinit var mediumDifficultyButton: Button
    private lateinit var hardDifficultyButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        easyDifficultyButton = findViewById(R.id.optionEasyButton)
        mediumDifficultyButton = findViewById(R.id.optionMediumButton)
        hardDifficultyButton = findViewById(R.id.optionHardButton)

        easyDifficultyButton.setOnClickListener{
            selectDifficulty(easyDifficultyButton)
        }
        mediumDifficultyButton.setOnClickListener{
            selectDifficulty(mediumDifficultyButton)
        }
        hardDifficultyButton.setOnClickListener{
            selectDifficulty(hardDifficultyButton)
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
}