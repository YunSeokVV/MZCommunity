package ui.board.dailyboard

import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.DailyBoardImageItemListBinding
import com.example.mzcommunity.databinding.DailyBoardTextItemListBinding
import com.example.mzcommunity.databinding.DailyBoardVideoItemListBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.orhanobut.logger.Logger
import data.model.DailyBoard
import data.model.UserFavourability

open class DailyBoardBaseAdapter(
    private val binding: ViewBinding,
    private val currentList: List<DailyBoard>
) : RecyclerView.ViewHolder(binding.root) {

    open fun bindView(
        binding: ViewBinding,
        item: DailyBoard,
        writeName: TextView,
        postingContents: TextView,
        likeImg: Button,
        disLikeImg: Button,
        likeCount: TextView,
        disLikeCount: TextView,
        userProfileImg: ImageView
    ) {
        writeName.text = item.writerNickname
        postingContents.text = item.boardContents

        when (item.favourability) {
            UserFavourability.LIKE -> {
                setThumbNailColor(true, likeImg, binding)
                setThumbNailColor(false, disLikeImg, binding)
            }

            UserFavourability.DISLIKE -> {
                setThumbNailColor(false, likeImg, binding)
                setThumbNailColor(true, disLikeImg, binding)
            }

            UserFavourability.USUAL -> {
                setThumbNailColor(false, likeImg, binding)
                setThumbNailColor(false, disLikeImg, binding)
            }
        }
        likeCount.text = item.like.toString()
        disLikeCount.text = item.disLike.toString()
        Glide.with(binding.root.context).load(Uri.parse(item.writerProfileUri))
            .into(userProfileImg)

    }

    open fun bindView(
        binding: DailyBoardVideoItemListBinding,
        item: DailyBoard,
        holder: DailyBoardAdapter.DailyBoardVideoItemViewHolder
    ): DailyBoardAdapter.DailyBoardVideoItemViewHolder {

        binding.writeName.text = item.writerNickname
        binding.postingContents.text = item.boardContents

        if (item.favourability == UserFavourability.LIKE) {
            setThumbNailColor(true, binding.likeImg, binding)
            setThumbNailColor(false, binding.disLikeImg, binding)
        } else if (item.favourability == UserFavourability.DISLIKE) {
            setThumbNailColor(false, binding.likeImg, binding)
            setThumbNailColor(true, binding.disLikeImg, binding)
        } else if (item.favourability == UserFavourability.USUAL) {
            setThumbNailColor(false, binding.likeImg, binding)
            setThumbNailColor(false, binding.disLikeImg, binding)
        }

        binding.likeCount.text = item.like.toString()
        binding.disLikeCount.text = item.disLike.toString()

        Glide.with(binding.root.context).load(Uri.parse(item.writerProfileUri))
            .into(binding.userProfileImg)

        return holder
    }

    open fun bindClickListener(
        binding: ViewBinding,
        showComment: DailyBoardAdapter.ShowComment,
        increaseLike: DailyBoardAdapter.IncreaseLike,
        increaseDisLike: DailyBoardAdapter.IncreaseDisLike
    ) {
        if (binding is DailyBoardTextItemListBinding) {
            bindClick(
                binding.comment,
                binding.likeImg,
                binding.disLikeImg,
                binding.likeCount,
                binding.disLikeCount,
                binding,
                showComment,
                increaseLike,
                increaseDisLike
            )
        } else if (binding is DailyBoardImageItemListBinding) {
            bindClick(
                binding.comment,
                binding.likeImg,
                binding.disLikeImg,
                binding.likeCount,
                binding.disLikeCount,
                binding,
                showComment,
                increaseLike,
                increaseDisLike
            )
        } else if (binding is DailyBoardVideoItemListBinding) {
            bindClick(
                binding.comment,
                binding.likeImg,
                binding.disLikeImg,
                binding.likeCount,
                binding.disLikeCount,
                binding,
                showComment,
                increaseLike,
                increaseDisLike
            )
        }
    }

    private fun setThumbNailColor(isActive: Boolean, thumbNail: View, binding: ViewBinding) {
        var color = ContextCompat.getColor(binding.root.context, R.color.theme)
        if (isActive) {
            color = ContextCompat.getColor(binding.root.context, R.color.theme)
        } else {
            color = ContextCompat.getColor(binding.root.context, R.color.black)
        }
        val colorStateList = ColorStateList.valueOf(color)
        thumbNail.setBackgroundTintList(colorStateList)
    }

    private fun bindClick(
        comment: View,
        likeImg: Button,
        disLikeImg: Button,
        likeCount: TextView,
        disLikeCount: TextView,
        binding: ViewBinding,
        showComment: DailyBoardAdapter.ShowComment,
        increaseLike: DailyBoardAdapter.IncreaseLike,
        increaseDisLike: DailyBoardAdapter.IncreaseDisLike
    ) {
        comment.setOnClickListener {
            showComment.showComment(adapterPosition)
        }

        likeImg.setOnClickListener {
            val dailyBoard = currentList.get(adapterPosition)
            increaseLike.increaseLike(adapterPosition)

            // 좋아요 버튼의 색깔을 파란색으로 변경
            if (dailyBoard.favourability == UserFavourability.USUAL) {
                setThumbNailColor(true, likeImg, binding)
                // 좋아요 +1
                likeCount.text =
                    setFavourCount((likeCount.text.toString()).toInt(), true).toString()

                return@setOnClickListener
            }
            // 사용자가 이전에 싫어요를 눌렀다가 좋아요를 누른 경우
            if (dailyBoard.favourability == UserFavourability.DISLIKE) {
                setThumbNailColor(true, likeImg, binding)
                setThumbNailColor(false, disLikeImg, binding)

                // 좋아요 +1, 싫어요 -1
                likeCount.text =
                    setFavourCount((likeCount.text.toString()).toInt(), true).toString()
                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        false
                    ).toString()

                return@setOnClickListener
            }

            // 사용자가 이전에 좋아요를 눌렀다가 다시 한번 좋아요를 누르는 경우
            if (dailyBoard.favourability == UserFavourability.LIKE) {
                setThumbNailColor(false, likeImg, binding)

                // 좋아요 -1
                likeCount.text =
                    setFavourCount(
                        (likeCount.text.toString()).toInt(),
                        false
                    ).toString()

                return@setOnClickListener
            }

        }

        disLikeImg.setOnClickListener {
            val dailyBoard = currentList.get(adapterPosition)
            increaseDisLike.increaseDisLike(adapterPosition)

            // 싫어요 버튼의 색깔을 파란색으로 변경
            if (dailyBoard.favourability == UserFavourability.USUAL) {
                setThumbNailColor(true, disLikeImg, binding)

                // 싫어요 +1
                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        true
                    ).toString()

                return@setOnClickListener
            }

            // 사용자가 이전에 싫어요를 눌렀다가 다시 싫어요를 누른 경우
            else if (dailyBoard.favourability == UserFavourability.DISLIKE) {
                setThumbNailColor(false, disLikeImg, binding)

                // 싫어요 -1
                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        false
                    ).toString()

                return@setOnClickListener
            }

            // 사용자가 이전에 좋아요를 눌렀다가 싫어요를 누르는 경우
            else if (dailyBoard.favourability == UserFavourability.LIKE) {
                setThumbNailColor(false, likeImg, binding)
                setThumbNailColor(true, disLikeImg, binding)

                // 좋아요 -1, 싫어요 +1
                likeCount.text =
                    setFavourCount(
                        (likeCount.text.toString()).toInt(),
                        false
                    ).toString()
                disLikeCount.text =
                    setFavourCount(
                        (disLikeCount.text.toString()).toInt(),
                        true
                    ).toString()

                return@setOnClickListener
            }

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

}