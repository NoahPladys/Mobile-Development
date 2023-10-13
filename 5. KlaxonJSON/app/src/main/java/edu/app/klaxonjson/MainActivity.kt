package edu.app.klaxonjson

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import edu.app.klaxonjson.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var array: JsonArray<JsonObject>
    val parser: Parser = Parser.default()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load student data in array
        val json = application.assets.open("students.json")
        array = parser.parse(json) as JsonArray<JsonObject>

        // Set spinner possible values (dropdown)
        val locations = array.map {
            it.obj("schoolResults")
        }.map {
            it!!.string("location")
        }.distinct()

        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item, locations
        )
        binding.spinner.adapter = adapter

        // Set listener on button
        binding.button.setOnClickListener {
            var textToShow = ""
            val students = array.filter {
                it.obj("schoolResults")
                    ?.string("location") == binding.spinner.selectedItem.toString()
            }.map {
                textToShow += it.string("first") + " " + it.string("last") + "\n"
            }

            binding.textView.text = textToShow
        }

        fun parse(name: String): Any? {
            val cls = Parser::class.java
            return cls.getResourceAsStream(name)?.let { inputStream ->
                return Parser.default().parse(inputStream)
            }
        }
    }
}