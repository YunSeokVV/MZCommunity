package view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.R
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import viewModel.BoardFramgnetViewModel

@AndroidEntryPoint
class BoardFragment : Fragment() {

    //private val viewModel : BoardFramgnetViewModel by viewModels()
    //private val viewModel : BoardFramgnetViewModel by requireActivity().viewModels()
    //private val viewModel : BoardFramgnetViewModel by viewModels()
    private val viewModel by viewModels<BoardFramgnetViewModel>()

    //private val viewModel : BoardFramgnetViewModel by viewModels()
    //private val viewModel2 by viewModels<BoardFramgnetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 일단 서버에서 데이터를 갖고 오세요
        viewModel.document.observe(requireActivity(), Observer {
            Logger.v(it)
        })

        return inflater.inflate(R.layout.fragment_board, container, false)
    }

}