package cl.baubau.finalexams

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var optionButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton = findViewById(R.id.mainPlayButton)
        optionButton = findViewById(R.id.mainOptionButton)

        playButton.setOnClickListener{
            val intentPlayActivity = Intent(this, PlayActivity::class.java)
            startActivity(intentPlayActivity)
        }

        optionButton.setOnClickListener{
            val intentOptionActivity = Intent(this, OptionActivity::class.java)
            startActivity(intentOptionActivity)
        }
    }
}