package ui.board.dailyboard

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import data.model.DailyBoard
import data.model.UserFavourability
import ui.base.BaseFragment


@AndroidEntryPoint
class DailyBoardFragment(
) : BaseFragment<FragmentBoardBinding>() {
    private val viewModel by viewModels<DailyBoardFramgnetViewModel>()

    private var dailyBoardAdapter: DailyBoardAdapter = DailyBoardAdapter(
        object : DailyBoardAdapter.IncreaseLike {
            override fun setViews(
                adapterPosition: Int,
                likeCount: TextView,
                disLikeCount: TextView,
                likeImg: Button,
                disLikeImg: Button
            ) {
                setFavourButtons(
                    true,
                    viewModel.getCurrentDailyBoard(adapterPosition),
                    likeCount,
                    disLikeCount,
                    likeImg,
                    disLikeImg
                )
            }

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
            override fun setViews(
                adapterPosition: Int,
                likeCount: TextView,
                disLikeCount: TextView,
                likeImg: Button,
                disLikeImg: Button
            ) {
                setFavourButtons(
                    false,
                    viewModel.getCurrentDailyBoard(adapterPosition),
                    likeCount,
                    disLikeCount,
                    likeImg,
                    disLikeImg
                )
            }

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

    private fun setFavourButtons(
        isLike: Boolean, // true - 좋아요 버튼, false - 싫어요 버튼
        dailyBoard: DailyBoard,
        likeCount: TextView,
        disLikeCount: TextView,
        likeImg: Button,
        disLikeImg: Button
    ) {
        // 사용자의 이전 호감도가 보통인 경우
        if (dailyBoard.favourability == UserFavourability.USUAL) {
            if (isLike) {
                setThumbNailColor(true, likeImg)
                setThumbNailColor(false, disLikeImg)
                likeCount.text =
                    setFavourCount((likeCount.text.toString()).toInt(), true).toString()
            } else {
                setThumbNailColor(false, likeImg)
                setThumbNailColor(true, disLikeImg)
                disLikeCount.text =
                    setFavourCount((disLikeCount.text.toString()).toInt(), true).toString()
            }

            return
        }
        // 사용자가 이전에 싫어요를 눌렀던 경우
        if (dailyBoard.favourability == UserFavourability.DISLIKE) {
            // 좋아요 클릭
            if (isLike) {
                setThumbNailColor(true, likeImg)
                setThumbNailColor(false, disLikeImg)

                // 좋아요 +1, 싫어요 -1
                likeCount.text =
                    setFavourCount((likeCount.text.toString()).toInt(), true).toString()

                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        false
                    ).toString()

                //싫어요 클릭
            } else {
                setThumbNailColor(false, likeImg)
                setThumbNailColor(false, disLikeImg)
                disLikeCount.text =
                    setFavourCount((disLikeCount.text.toString()).toInt(), false).toString()
            }

            return
        }

        // 사용자가 이전에 좋아요를 눌렀던 경우
        if (dailyBoard.favourability == UserFavourability.LIKE) {

            // 좋아요 -1
            likeCount.text =
                setFavourCount(
                    (likeCount.text.toString()).toInt(),
                    false
                ).toString()

            if (isLike) {
                setThumbNailColor(false, likeImg)
                setThumbNailColor(false, disLikeImg)

            } else {
                setThumbNailColor(false, likeImg)
                setThumbNailColor(true, disLikeImg)

                // 싫어요 +1
                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        true
                    ).toString()
            }

            return
        }
    }

    private fun setFavourCount(originalCount: Int, increase: Boolean): Int {
        var result = originalCount
        if (increase) {
            result += 1
        } else if (!increase) {
            result -= 1
        }

        if (result == 0)
            return 0

        return result
    }

    // isActvit 가 true인 경우는 버튼이 파랑색인 경우다.
    private fun setThumbNailColor(isActive: Boolean, button: Button) {
        var color = ContextCompat.getColor(requireContext(), R.color.theme)
        if (isActive) {
            color = ContextCompat.getColor(requireContext(), R.color.theme)
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.black)
        }
        val colorStateList = ColorStateList.valueOf(color)
        button.setBackgroundTintList(colorStateList)
    }
}