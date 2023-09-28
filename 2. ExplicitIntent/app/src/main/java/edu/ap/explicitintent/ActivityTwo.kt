package edu.ap.explicitintent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import edu.ap.explicitintent.databinding.ActivityActivityTwoBinding

class ActivityTwo : AppCompatActivity() {

    private lateinit var activityTwoBinding: ActivityActivityTwoBinding

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        activityTwoBinding = ActivityActivityTwoBinding.inflate(layoutInflater)
        val view = activityTwoBinding.root

        val input1Value = getIntent().getStringExtra("input1Value")
        val input2Value = getIntent().getStringExtra("input2Value")

        activityTwoBinding.input1.setText(input1Value);
        activityTwoBinding.input2.setText(input2Value);

        //setContentView(R.layout.activity_activity_two)
        setContentView(view)

        activityTwoBinding.button.setOnClickListener {
            val input1 = activityTwoBinding.input1.text.toString()
            val input2 = activityTwoBinding.input2.text.toString()
            val intent = Intent()
            // Pass relevant data back as a result
            intent.putExtra("input1Value", input1)
            intent.putExtra("input2Value", input2)
            // Activity finished ok, return the data
            setResult(RESULT_OK, intent) // set result code and bundle data for response
            this.finish();
        }
    }
}