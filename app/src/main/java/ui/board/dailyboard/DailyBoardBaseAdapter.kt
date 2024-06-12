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
        showComment: DailyBoardAdapter.ShowComment,
        increaseLike: DailyBoardAdapter.IncreaseLike,
        increaseDisLike: DailyBoardAdapter.IncreaseDisLike
    ) {
        comment.setOnClickListener {
            showComment.showComment(adapterPosition)
        }

        likeImg.setOnClickListener {
            increaseLike.setViews(adapterPosition, likeCount, disLikeCount, likeImg, disLikeImg)
            increaseLike.increaseLike(adapterPosition)
            disableFavourabilityBtn(likeImg, disLikeImg)
        }

        disLikeImg.setOnClickListener {
            increaseDisLike.setViews(adapterPosition, likeCount, disLikeCount, likeImg, disLikeImg)
            increaseDisLike.increaseDisLike(adapterPosition)
            disableFavourabilityBtn(likeImg, disLikeImg)
        }

    }

    private fun disableFavourabilityBtn(likeImg : Button, disLikeImg: Button){
        likeImg.isEnabled = false
        disLikeImg.isEnabled = false
        likeImg.postDelayed({likeImg.isEnabled = true}, 500)
        disLikeImg.postDelayed({disLikeImg.isEnabled = true}, 500)
    }
}