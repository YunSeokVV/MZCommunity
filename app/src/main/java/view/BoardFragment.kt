package view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mzcommunity.R
import com.orhanobut.logger.Logger

class BoardFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Logger.v("BoardFramgentCalled")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

}