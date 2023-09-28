package edu.ap.explicitintent

import android.os.Bundle
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.ap.explicitintent.databinding.ActivityActivityOneBinding
import java.io.Console

class ActivityOne : AppCompatActivity() {

    private lateinit var activityOneBinding: ActivityActivityOneBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityOneBinding = ActivityActivityOneBinding.inflate(layoutInflater)
        val view = activityOneBinding.root

        //setContentView(R.layout.activity_activity_one)
        setContentView(view)

        registerCallback()
    }

    private fun registerCallback() {
        val callback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if(data != null) {
                    val input1Value = data.getStringExtra("input1Value")
                    val input2Value = data.getStringExtra("input2Value")

                    activityOneBinding.input1.setText(input1Value);
                    activityOneBinding.input2.setText(input2Value);
                }
            }
        }
        registerClickListener(callback)
    }

    private fun registerClickListener(callback: ActivityResultLauncher<Intent>) {
        activityOneBinding.button.setOnClickListener {
            val input1 = activityOneBinding.input1.text.toString()
            val input2 = activityOneBinding.input2.text.toString()

            val intent = Intent(this, ActivityTwo::class.java)
            intent.putExtra("input1Value", input1)
            intent.putExtra("input2Value", input2)
            callback.launch(intent)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }*/
}
