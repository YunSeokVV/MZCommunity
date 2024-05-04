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
import androidx.recyclerview.widget.RecyclerView
import com.example.mzcommunity.databinding.FragmentBoardBinding
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