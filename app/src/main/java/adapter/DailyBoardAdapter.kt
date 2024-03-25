package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    private val dailyBoards: List<DailyBoard>,
    private val uris: List<List<Uri>>,
    private val increaseLike: IncreaseLike,
    private val decreaseLike: DecreaseLike
) : ListAdapter<DailyBoard, DailyBoardAdapter.DailyBoardItemViewHolder>(diffUtil) {

    interface IncreaseLike{
        fun increaseLikse(likeNumber : Int)
    }

    interface DecreaseLike{
        fun decreaseLike(likeNumber : Int)
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<DailyBoard>() {
            override fun areItemsTheSame(oldItem: DailyBoard, newItem: DailyBoard): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DailyBoard, newItem: DailyBoard): Boolean {
                return oldItem == newItem
            }

        }
    }

    class DailyBoardItemViewHolder(
        private val imagesUri: List<List<Uri>>,
        private val binding: DailyBoardItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DailyBoard) {
            binding.writeName.text = item.writerNickname
            binding.postingContents.text = item.boardContents
            binding.likeCount.text = item.like.toString()
            binding.disLikeCount.text = item.disLike.toString()
            binding.dailyImgViewPager.adapter =
                DailyViewPagerAdapter(imagesUri.get(adapterPosition), imagesUri.get(adapterPosition).size)
            TabLayoutMediator(
                binding.intoTabLayout,
                binding.dailyImgViewPager
            ) { tab, position -> }.attach()

            if(imagesUri.get(adapterPosition).size == 1){
                binding.intoTabLayout.visibility = View.GONE
            }

            Glide.with(binding.root.context).load(item.writerProfileUri)
                .into(binding.userProfileImg)
        }

        init {
            binding.likeImg.setOnClickListener {

            }

            binding.disLikeImg.setOnClickListener {

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyBoardItemViewHolder {
        val binding =
            DailyBoardItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DailyBoardItemViewHolder(uris, binding)
    }

    override fun onBindViewHolder(holder: DailyBoardItemViewHolder, position: Int) {
        holder.bind(dailyBoards.get(position))


    }


}