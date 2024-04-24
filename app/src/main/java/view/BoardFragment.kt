package view

import adapter.DailyBoardAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.DailyBoard
import model.User
import viewmodel.BoardFramgnetViewModel

@AndroidEntryPoint
class BoardFragment(private val userProfile : User) : Fragment() {
    private val viewModel by viewModels<BoardFramgnetViewModel>()
    private lateinit var binding: FragmentBoardBinding
    private lateinit var dailyBoardAdapter : DailyBoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater)

        binding.dailyBoards.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)


        dailyBoardAdapter = DailyBoardAdapter(
            object : DailyBoardAdapter.IncreaseLike {
                override fun increaseLike(dailyBoard: DailyBoard, adapterPosition : Int) {
                    viewModel.increaseDailyBoardLike(dailyBoard, dailyBoard.boardUID, adapterPosition)
                }

            },
            object : DailyBoardAdapter.IncreaseDisLike {
                override fun increaseDisLike(dailyBoard: DailyBoard, adapterPosition : Int) {
                    viewModel.increaseDailyBoardDisLike(dailyBoard, dailyBoard.boardUID, adapterPosition)
                }

            },
            object : DailyBoardAdapter.ShowComment{
                override fun showComment(dailyBoard : DailyBoard) {
                    val bottomSheetFragment = BottomSheetFragment(dailyBoard.boardUID,"dailyBoardComment", "dailyBoardNestedComment", "dailyBoardComment", userProfile)
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                }

            })



        binding.dailyBoards.adapter = dailyBoardAdapter

        viewModel.document.observe(requireActivity(), Observer {
            dailyBoardAdapter.submitList(it)
        })

        return binding.root
    }
}