package view.fragment

import adapter.DailyBoardAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.databinding.FragmentBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import model.DailyBoard
import model.LoginedUser
import viewmodel.BoardFramgnetViewModel


@AndroidEntryPoint
class BoardFragment(
    private val loginedUserProfile: LoginedUser,
    private val dailyBoard: List<DailyBoard>
) : Fragment() {
    private val viewModel by viewModels<BoardFramgnetViewModel>()
    private lateinit var binding: FragmentBoardBinding
    private lateinit var dailyBoardAdapter: DailyBoardAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater)

        binding.dailyBoards.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        dailyBoardAdapter = DailyBoardAdapter(
            object : DailyBoardAdapter.IncreaseLike {
                override fun increaseLike(dailyBoard: DailyBoard, adapterPosition: Int) {
                    dailyBoardAdapter.releaseVideo()
                    viewModel.increaseFavourability(
                        dailyBoard,
                        dailyBoard.boardUID,
                        adapterPosition,
                        true
                    )
                }

            },
            object : DailyBoardAdapter.IncreaseDisLike {
                override fun increaseDisLike(dailyBoard: DailyBoard, adapterPosition: Int) {
                    dailyBoardAdapter.releaseVideo()
                    viewModel.increaseFavourability(
                        dailyBoard,
                        dailyBoard.boardUID,
                        adapterPosition,
                        false
                    )
                }

            },
            object : DailyBoardAdapter.ShowComment {
                override fun showComment(dailyBoard: DailyBoard) {
                    val bottomSheetFragment = BottomSheetFragment(
                        dailyBoard.boardUID,
                        "dailyBoardComment",
                        "dailyBoardNestedComment",
                        "dailyBoardComment",
                        loginedUserProfile
                    )
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                }

            })


        binding.dailyBoards.adapter = dailyBoardAdapter
        viewModel.initDailyBoards(dailyBoard)


        binding.swipeRefreshLayout.setOnRefreshListener {
            dailyBoardAdapter.releaseVideo()
            viewModel.getRandomDailyBoards()
            viewModel.isRfreshing = true
        }

        viewModel.dailyBoards.observe(requireActivity(), Observer {
            dailyBoardAdapter.submitList(it.toMutableList()) {
                if (viewModel.isRfreshing){
                    binding.dailyBoards.scrollToPosition(0)
                    viewModel.isRfreshing = false
                }
            }

            binding.dailyBoards.post {
                binding.swipeRefreshLayout.isRefreshing = false
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        })

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        if (dailyBoardAdapter.isRecentVideoInitalized())
        dailyBoardAdapter.pauseVideoOnstop()
    }

    override fun onResume() {
        super.onResume()
        if (dailyBoardAdapter.isRecentVideoInitalized())
        dailyBoardAdapter.resumeVideoOnResume()
    }
}