package edu.app.sharedpreferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.widget.doAfterTextChanged
import edu.app.sharedpreferences.databinding.IndexBinding

class MainActivity : ComponentActivity() {

    private lateinit var indexBinding : IndexBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        indexBinding = IndexBinding.inflate(layoutInflater)

        indexBinding.input.setText(getInputValue());
        indexBinding.checkBox.isChecked = getCheckboxValue();

        indexBinding.input.doAfterTextChanged {
            if(indexBinding.checkBox.isChecked) {
                setInputValue()
            }
        }

        indexBinding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                setInputValue()
            }
            setCheckboxValue(isChecked);
        }

        val view = indexBinding.root
        setContentView(view)
    }

    fun setInputValue(): Unit {
        val inputValue: String = indexBinding.input.text.toString();
        if(inputValue != "") {
            val sharedPref = getPreferences(MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putString(R.string.saved_value_key.toString(), inputValue)
                apply()
            }
        }
    }

    fun getInputValue(): String {
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return ""
        return sharedPref.getString(R.string.saved_value_key.toString(), "") ?: ""
    }

    fun setCheckboxValue(boolean: Boolean) {
        val inputValue: String = indexBinding.input.text.toString();
        if(inputValue != "") {
            val sharedPref = getPreferences(MODE_PRIVATE) ?: return
            with (sharedPref.edit()) {
                putBoolean(R.string.checkbox_checked_boolean.toString(), boolean)
                apply()
            }
        }
    }

    fun getCheckboxValue(): Boolean {
        val sharedPref = getPreferences(MODE_PRIVATE) ?: return false;
        return sharedPref.getBoolean(R.string.checkbox_checked_boolean.toString(), false)
    }
}