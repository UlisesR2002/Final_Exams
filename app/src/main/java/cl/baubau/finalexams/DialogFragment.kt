package cl.baubau.finalexams

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class MyDialogFragment(score: Int) : DialogFragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private var score: Int = score

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflar el diseño del diálogo
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.popup_loss, null)

        // Configurar los elementos de la interfaz
        val scoreTextView = view.findViewById<TextView>(R.id.popupScoreTextView)
        val highScore = view.findViewById<TextView>(R.id.popupHighScoreTextView)
        val button1 = view.findViewById<Button>(R.id.popupRetryButton)
        val button2 = view.findViewById<Button>(R.id.popupHomeButton)

        sharedPreferences = requireContext().getSharedPreferences("common_prefs", Context.MODE_PRIVATE)

        val savedHighScore =
            sharedPreferences.getInt(OptionActivity.KEY_HIGH_SCORE, 0)

        scoreTextView.text = getString(R.string.popup_high_score_textview) + score.toString()
        highScore.text = getString(R.string.popup_high_score_textview) + savedHighScore.toString()

        button1.setOnClickListener {
            // Acción para el botón 1
            dismiss() // Cerrar el diálogo si es necesario
            val intent = Intent(requireContext(), PlayActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        button2.setOnClickListener {
            // Acción para el botón 2
            dismiss() // Cerrar el diálogo si es necesario
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Crear el diálogo con el diseño personalizado
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        return builder.create()
    }
}