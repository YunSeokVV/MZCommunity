package view

import adapter.DailyBoardAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.DailyBoard
import viewModel.BoardFramgnetViewModel

@AndroidEntryPoint
class BoardFragment : Fragment() {
    private val viewModel by viewModels<BoardFramgnetViewModel>()
    private lateinit var binding: FragmentBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater)

        binding.dailyBoards.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        viewModel.document.observe(requireActivity(), Observer {
            val dailyBoardAdapter = DailyBoardAdapter(
                it,
                viewModel.getUserUploadFilesUri(),
                object : DailyBoardAdapter.IncreaseLike {
                    override fun increaseLike(dailyBoard: DailyBoard) {
                        viewModel.increaseDailyBoardLike(dailyBoard)
                    }

                },
                object : DailyBoardAdapter.IncreaseDisLike {
                    override fun increaseDisLike(dailyBoard: DailyBoard) {
                        viewModel.increaseDailyBoardDisLike(dailyBoard)
                    }

                })
            binding.dailyBoards.adapter = dailyBoardAdapter

            dailyBoardAdapter.submitList(it)
            dailyBoardAdapter.notifyDataSetChanged()
        })

        return binding.root
    }

}