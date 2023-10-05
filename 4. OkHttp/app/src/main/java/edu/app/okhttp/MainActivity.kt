package edu.app.okhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import edu.app.okhttp.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    //private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.button.setOnClickListener {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://publicobject.com/helloworld.txt")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("OUR_APP", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        var body = response.body!!.string()
                        Log.d("OKHTTP", body)
                        runOnUiThread {
                            mainBinding.textView.text = body
                        }
                    }
                }
            })
        }
    }
}