package view.fragment

import adapter.DailyBoardAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.DailyBoard
import model.LoginedUser
import viewmodel.BoardFramgnetViewModel

@AndroidEntryPoint
class BoardFragment(private val loginedUserProfile : LoginedUser, private val dailyBoard: List<DailyBoard>) : Fragment() {
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
                    viewModel.increaseFavourability(dailyBoard, dailyBoard.boardUID, adapterPosition, true)
                }

            },
            object : DailyBoardAdapter.IncreaseDisLike {
                override fun increaseDisLike(dailyBoard: DailyBoard, adapterPosition : Int) {
                    viewModel.increaseFavourability(dailyBoard, dailyBoard.boardUID, adapterPosition, false)
                }

            },
            object : DailyBoardAdapter.ShowComment{
                override fun showComment(dailyBoard : DailyBoard) {
                    val bottomSheetFragment = BottomSheetFragment(dailyBoard.boardUID,"dailyBoardComment", "dailyBoardNestedComment", "dailyBoardComment", loginedUserProfile)
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                }

            })


        binding.dailyBoards.adapter = dailyBoardAdapter
        viewModel.initDailyBoards(dailyBoard)
        dailyBoardAdapter.submitList(dailyBoard)

        viewModel.dailyBoards.observe(requireActivity(), Observer {
            dailyBoardAdapter.submitList(it)
        })

        return binding.root
    }
}