package edu.app.sqlite

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import edu.app.sqlite.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var binding: ActivityMainBinding
    private val URL = "https://jsonplaceholder.typicode.com/todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        binding.button.setOnClickListener {
            binding.textView.text = ""
            var todos = databaseHelper.allTodos(binding.input.text.toString())
            var results: String? = "Size : " + todos.size + "\n\n"
            for (todo in todos) {
                results += todo.title + "\n\n"
            }
             binding.textView.setText(results)
        }

        Log.d("SQLITE", "Size : " + databaseHelper.checkDBEmpty())

        if(databaseHelper.checkDBEmpty() == 0) {
            getData()
        }
    }

    override fun onDestroy() {
        databaseHelper.close()
        super.onDestroy()
    }

    fun getData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(URL)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("SQLITE", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val parser: Parser = Parser.default()
                    var json = StringBuilder(response.body!!.string())
                    var array = parser.parse(json) as JsonArray<JsonObject>

                    array.map {
                        val todo = Todo(it["id"] as Int,
                            it["userId"] as Int,
                            it["title"] as String,
                            (if (it["completed"] as Boolean) 1 else 0))
                        databaseHelper.addTodo(todo)

                    }
                }
            }
        })
    }
}