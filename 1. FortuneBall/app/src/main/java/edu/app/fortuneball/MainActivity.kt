package edu.app.fortuneball

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import edu.app.fortuneball.databinding.ActivityMainBinding
import java.util.Random

class MainActivity : AppCompatActivity() {

    private var fortuneList = arrayOf(
        "Don’t count on it",
        "Ask again later",
        "You can rely on it",
        "Without a doubt",
        "Outlook is not so good",
        "It's decidedly so",
        "Signs point to yes",
        "Yes, definitely",
        "Yes",
        "My sources say NO"
    )

    private lateinit var fortuneText: TextView
    private lateinit var generateFortuneButton: Button
    private lateinit var fortuneBallImage: ImageView


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        fortuneText = findViewById<View>(R.id.fortuneText) as TextView
        fortuneBallImage = findViewById<View>(R.id.fortunateImage) as ImageView
        generateFortuneButton = findViewById<View>(R.id.fortuneButton) as Button

        generateFortuneButton.setOnClickListener {
            val index = Random().nextInt(fortuneList.size)
            fortuneText.setText(fortuneList[index])

            YoYo.with(Techniques.Swing)
                .duration(500)
                .playOn(fortuneBallImage)
        }

        Log.v("FORTUNE APP TAG","onCreateCalled")
    }
}
