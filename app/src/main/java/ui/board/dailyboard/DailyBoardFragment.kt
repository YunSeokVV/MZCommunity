package ui.board.dailyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.databinding.FragmentBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.DailyBoard
import ui.base.BaseFragment


@AndroidEntryPoint
class DailyBoardFragment(
) : BaseFragment<FragmentBoardBinding>() {
    private val viewModel by viewModels<DailyBoardFramgnetViewModel>()

    private var dailyBoardAdapter: DailyBoardAdapter = DailyBoardAdapter(
        object : DailyBoardAdapter.IncreaseLike {
            override fun increaseLike(adapterPosition: Int) {
                viewModel.increaseFavourability(
                    viewModel.getCurrentDailyBoard(adapterPosition),
                    viewModel.getCurrentDailyBoard(adapterPosition).boardUID,
                    adapterPosition,
                    true
                )
            }

        },
        object : DailyBoardAdapter.IncreaseDisLike {
            override fun increaseDisLike(adapterPosition: Int) {
                viewModel.increaseFavourability(
                    viewModel.getCurrentDailyBoard(adapterPosition),
                    viewModel.getCurrentDailyBoard(adapterPosition).boardUID,
                    adapterPosition,
                    false
                )
            }

        },
        object : DailyBoardAdapter.ShowComment {
            override fun showComment(adapterPosition: Int) {
                showComment(viewModel.getCurrentDailyBoard(adapterPosition).boardUID)
            }

        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dailyBoards.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        binding.dailyBoards.adapter = dailyBoardAdapter
        viewModel.initDailyBoards(getDailyBoard())


        binding.swipeRefreshLayout.setOnRefreshListener {
            dailyBoardAdapter.releaseVideo()
            viewModel.getRandomDailyBoards()
            viewModel.isRfreshing = true
        }

        viewModel.dailyBoards.observe(requireActivity(), Observer {
            dailyBoardAdapter.submitList(it.toMutableList()) {
                if (viewModel.isRfreshing) {
                    binding.dailyBoards.scrollToPosition(0)
                    viewModel.isRfreshing = false
                    dailyBoardAdapter.notifyDataSetChanged()
                }
            }

            binding.dailyBoards.post {
                binding.swipeRefreshLayout.isRefreshing = false
                if (binding.swipeRefreshLayout.isRefreshing) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBoardBinding {
        return FragmentBoardBinding.inflate(inflater)
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

    private fun getDailyBoard(): List<DailyBoard> {

        return (arguments?.getParcelableArrayList<DailyBoard>("dailyBoards"))?.toList() as List<DailyBoard>
    }
}