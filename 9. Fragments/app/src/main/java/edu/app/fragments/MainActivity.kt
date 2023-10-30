package edu.app.fragments

import android.R.attr.fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import edu.app.fragments.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), SecondFragment.OnItemClickedListener {

    private lateinit var mainBinding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.FragmentOneButton.setOnClickListener {
            val TEXT = "Fragment One"

            val firstFragment = FirstFragment();
            val bundle = Bundle()
            bundle.putString("FragmentText", TEXT)
            firstFragment.setArguments(bundle)
            setFragment(firstFragment)
        }

        mainBinding.FragmentTwoButton.setOnClickListener {
            setFragment(SecondFragment())
        }
    }

    override fun onItemClicked(msg: String?) {
        Snackbar.make(mainBinding.root, msg!!, Snackbar.LENGTH_LONG).show()
    }

    private fun setFragment(fragment: Fragment) {
        // Begin the transaction
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        // Replace the contents of the container with the new fragment
        ft.replace(mainBinding.frameLayout.id, fragment)
        // Complete the changes added above
        ft.commit()
    }
}