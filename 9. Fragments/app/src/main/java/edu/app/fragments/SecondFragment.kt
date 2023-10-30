package edu.app.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.app.fragments.databinding.FragmentSecondBinding


/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!
    private var listener: OnItemClickedListener? = null

    interface OnItemClickedListener {
        fun onItemClicked(msg: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is OnItemClickedListener) {
            context
        } else {
            throw ClassCastException(
                context.toString() + " must implement MyListFragment.OnItemClickedListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        _binding!!.FragmentText.setOnClickListener({ listener!!.onItemClicked("You clicked on Second Fragment") })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param fragmentText Fragment main text
         * @return A new instance of fragment SecondFragment.
         */
        @JvmStatic
        fun newInstance() = SecondFragment()
    }*/
}