package adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.DailyBoardItemListBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.orhanobut.logger.Logger
import model.DailyBoard

class DailyBoardAdapter(
    private val increaseLike: IncreaseLike,
    private val increaseDisLike: IncreaseDisLike,
    private val showComment : ShowComment
) : ListAdapter<DailyBoard, DailyBoardAdapter.DailyBoardItemViewHolder>(diffUtil) {
    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<DailyBoard>() {
            override fun areItemsTheSame(oldItem: DailyBoard, newItem: DailyBoard): Boolean {
                return oldItem.boardUID == newItem.boardUID
            }

            override fun areContentsTheSame(oldItem: DailyBoard, newItem: DailyBoard): Boolean {
                return oldItem == newItem
            }

        }
    }
    interface IncreaseLike {
        fun increaseLike(dailyBoard: DailyBoard, adapterPosition: Int)
    }

    interface IncreaseDisLike {
        fun increaseDisLike(dailyBoard: DailyBoard, adapterPosition: Int)
    }

    interface ShowComment{
        fun showComment(dailyBoard: DailyBoard)
    }

    inner class DailyBoardItemViewHolder(
        private val imagesUri: List<List<Uri>>,
        private val binding: DailyBoardItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyBoard) {
            binding.writeName.text = item.writerNickname
            binding.postingContents.text = item.boardContents

            if (item.favourability.equals("like")) {
                setThumbNailColor(true, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            } else if (item.favourability.equals("disLike")) {
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(true, binding.disLikeImg)
            } else if(item.favourability.equals("usual")){
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            }

            binding.likeCount.text = item.like.toString()
            binding.disLikeCount.text = item.disLike.toString()
            binding.dailyImgViewPager.adapter =
                DailyViewPagerAdapter(
                    imagesUri.get(adapterPosition),
                    imagesUri.get(adapterPosition).size
                )
            TabLayoutMediator(
                binding.intoTabLayout,
                binding.dailyImgViewPager
            ) { tab, position -> }.attach()

            if (imagesUri.get(adapterPosition).size == 1) {
                binding.intoTabLayout.visibility = View.GONE
            }

            Glide.with(binding.root.context).load(item.writerProfileUri)
                .into(binding.userProfileImg)
        }

        init {
            binding.comment.setOnClickListener {
                val dailyBoard = currentList.get(adapterPosition)
                showComment.showComment(dailyBoard)
            }

            binding.likeImg.setOnClickListener {
                val dailyBoard = currentList.get(adapterPosition)
                increaseLike.increaseLike(dailyBoard, adapterPosition)

                // 좋아요 버튼의 색깔을 파란색으로 변경
                if (dailyBoard.favourability.equals("usual")) {
                    setThumbNailColor(true, binding.likeImg)

                    // 좋아요 +1
                    binding.likeCount.text =
                        setFavourCount((binding.likeCount.text.toString()).toInt(), true).toString()

                    // 사용자가 이전에 싫어요를 눌렀다가 좋아요를 누른 경우
                } else if (dailyBoard.favourability.equals("disLike")) {
                    setThumbNailColor(true, binding.likeImg)
                    setThumbNailColor(false, binding.disLikeImg)

                    // 좋아요 +1, 싫어요 -1
                    binding.likeCount.text =
                        setFavourCount((binding.likeCount.text.toString()).toInt(), true).toString()
                    binding.disLikeCount.text =
                        setFavourCount(
                            (binding.disLikeCount.text.toString()).toInt(),
                            false
                        ).toString()

                    // 사용자가 이전에 좋아요를 눌렀다가 다시 한번 좋아요를 누르는 경우
                } else if (dailyBoard.favourability.equals("like")) {
                    setThumbNailColor(false, binding.likeImg)

                    // 좋아요 -1
                    binding.likeCount.text =
                        setFavourCount(
                            (binding.likeCount.text.toString()).toInt(),
                            false
                        ).toString()
                }

            }

            binding.disLikeImg.setOnClickListener {
                val dailyBoard = currentList.get(adapterPosition)
                increaseDisLike.increaseDisLike(dailyBoard, adapterPosition)

                // 싫어요 버튼의 색깔을 파란색으로 변경
                if (dailyBoard.favourability.equals("usual")) {
                    setThumbNailColor(true, binding.disLikeImg)

                    // 싫어요 +1
                    binding.disLikeCount.text =
                        setFavourCount(
                            (binding.disLikeCount.text.toString()).toInt(),
                            true
                        ).toString()

                    // 사용자가 이전에 싫어요를 눌렀다가 다시 싫어요를 누른 경우
                } else if (dailyBoard.favourability.equals("disLike")) {
                    setThumbNailColor(false, binding.disLikeImg)

                    // 싫어요 -1
                    binding.disLikeCount.text =
                        setFavourCount(
                            (binding.disLikeCount.text.toString()).toInt(),
                            false
                        ).toString()

                    // 사용자가 이전에 좋아요를 눌렀다가 싫어요를 누르는 경우
                } else if (dailyBoard.favourability.equals("like")) {
                    setThumbNailColor(false, binding.likeImg)
                    setThumbNailColor(true, binding.disLikeImg)

                    // 좋아요 -1, 싫어요 +1
                    binding.likeCount.text =
                        setFavourCount(
                            (binding.likeCount.text.toString()).toInt(),
                            false
                        ).toString()
                    binding.disLikeCount.text =
                        setFavourCount(
                            (binding.disLikeCount.text.toString()).toInt(),
                            true
                        ).toString()
                }

            }

        }

        fun setFavourCount(originalCount: Int, increase: Boolean): Int {
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

        fun setThumbNailColor(isActive: Boolean, thumbNail: View) {
            var color = ContextCompat.getColor(binding.root.context, R.color.theme)
            if (isActive) {
                color = ContextCompat.getColor(binding.root.context, R.color.theme)
            } else {
                color = ContextCompat.getColor(binding.root.context, R.color.black)
            }
            val colorStateList = ColorStateList.valueOf(color)
            thumbNail.setBackgroundTintList(colorStateList)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyBoardItemViewHolder {
        val binding =
            DailyBoardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DailyBoardItemViewHolder(getUserUploadFilesUri(), binding)
    }

    override fun onBindViewHolder(holder: DailyBoardItemViewHolder, position: Int) {
        holder.bind(currentList.get(position))
    }


    fun getUserUploadFilesUri(): List<List<Uri>> {
        var uris = mutableListOf<List<Uri>>()
        currentList.forEach {
            uris.add(it.files)
        }
        return uris
    }

}