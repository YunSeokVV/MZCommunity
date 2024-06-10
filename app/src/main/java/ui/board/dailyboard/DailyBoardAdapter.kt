package ui.board.dailyboard

import android.content.res.ColorStateList
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.DailyBoardImageItemListBinding
import com.example.mzcommunity.databinding.DailyBoardTextItemListBinding
import com.example.mzcommunity.databinding.DailyBoardVideoItemListBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.tabs.TabLayoutMediator
import com.orhanobut.logger.Logger
import data.model.DailyBoard
import data.model.DailyBoardViewType
import data.model.UserFavourability


class DailyBoardAdapter(
    private val increaseLike: IncreaseLike,
    private val increaseDisLike: IncreaseDisLike,
    private val showComment: ShowComment
) : ListAdapter<DailyBoard, RecyclerView.ViewHolder>(diffUtil) {
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
        fun increaseLike(adapterPosition: Int)
    }

    interface IncreaseDisLike {
        fun increaseDisLike(adapterPosition: Int)
    }

    interface ShowComment {
        fun showComment(adapterPosition: Int)
    }

    // 백그라운드에 갔을때 재생중인 동영상을 멈추기 위해 존재하는 변수
    private var recentVideoItemViewHolder: DailyBoardVideoItemViewHolder? = null

    // todo : 앱이 처음 실행됐을때 recentVideoItemViewHolder 객체의 null 검사를 방지하기 위해 임의로 만든 플래그값이다. 가급적이면 다른 해결책을 찾아서 이 변수를 사용하지 말자.
    private var _isRecentVideoInitalized: Boolean = false

    fun isRecentVideoInitalized(): Boolean = _isRecentVideoInitalized

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (_isRecentVideoInitalized)
                    recentVideoItemViewHolder?.releaseVideo()

                var currentViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(layoutManager.findLastVisibleItemPosition())


                // 스크롤해서 ui에 보여진 마지막 아이템이 동영상 타입이라면 재생시킨다.
                if (currentViewHolder != null && currentViewHolder.itemViewType == DailyBoardViewType.fromValue(
                        DailyBoardViewType.VIDEO
                    )
                ) {
                    currentViewHolder = currentViewHolder as DailyBoardVideoItemViewHolder
                    currentViewHolder.processVideo(currentViewHolder)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(onScrollListener)
    }

    inner class DailyBoardImageItemViewHolder(
        private val binding: DailyBoardImageItemListBinding
    ) : DailyBoardBaseAdapter(binding, currentList) {

        fun getBind(): DailyBoardImageItemListBinding = binding

        init {
            bindClickListener(binding, showComment, increaseLike, increaseDisLike)

        }

    }

    inner class DailyBoardTextItemViewHolder(
        private val binding: DailyBoardTextItemListBinding
    ) : DailyBoardBaseAdapter(binding, currentList) {

        fun getBind(): DailyBoardTextItemListBinding = binding

        init {
            bindClickListener(binding, showComment, increaseLike, increaseDisLike)
        }

    }


    inner class DailyBoardVideoItemViewHolder(
        private val binding: DailyBoardVideoItemListBinding
    ) : DailyBoardBaseAdapter(binding, currentList) {

        fun getBind(): DailyBoardVideoItemListBinding = binding

        fun pauseVideo() {
            binding.playerView.player?.pause()
        }

        fun releaseVideo() {
            binding.playerView.player?.release()
        }

        fun playVideo() {
            binding.playerView.player?.play()
        }

        fun processVideo(dailyBoardVideoItemViewHolder: DailyBoardVideoItemViewHolder) {
            val context = binding.root.context

            // 현재 동영상이 재생중인지 확인
            val resume = binding.playerView.player?.isPlaying ?: false
            if (!resume) {
                ExoPlayer.Builder(context)
                    .build()
                    .also { exoPlayer ->
                        binding.playerView.player = exoPlayer
                        val mediaItem = MediaItem.fromUri(currentList[adapterPosition].files[0])
                        exoPlayer.setMediaItems(listOf(mediaItem), 0, 0)
                        exoPlayer.playWhenReady = true
                        exoPlayer.prepare()
                    }
                recentVideoItemViewHolder = dailyBoardVideoItemViewHolder
                _isRecentVideoInitalized = true
            }
        }

        init {
            bindClickListener(binding, showComment, increaseLike, increaseDisLike)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (DailyBoardViewType.fromValue(viewType)) {
            DailyBoardViewType.IMAGE -> {
                val binding =
                    DailyBoardImageItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return DailyBoardImageItemViewHolder(binding)
            }

            DailyBoardViewType.TEXT -> {
                val binding =
                    DailyBoardTextItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return DailyBoardTextItemViewHolder(binding)
            }

            DailyBoardViewType.VIDEO -> {
                val binding =
                    DailyBoardVideoItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return DailyBoardVideoItemViewHolder(binding)
            }

            else -> {
                val binding =
                    DailyBoardImageItemListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return DailyBoardImageItemViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (currentList[position].viewType) {
            DailyBoardViewType.IMAGE -> {
                if (holder is DailyBoardImageItemViewHolder) {
                    val binding = holder.getBind()
                    holder.bindView(
                        binding,
                        currentList[position],
                        binding.writeName,
                        binding.postingContents,
                        binding.likeImg,
                        binding.disLikeImg,
                        binding.likeCount,
                        binding.disLikeCount,
                        binding.userProfileImg
                    )

                    binding.dailyImgViewPager.adapter =
                        DailyViewPagerAdapter(
                            currentList[position].files,
                            currentList[position].files.size,
                        )

                    TabLayoutMediator(
                        binding.intoTabLayout,
                        binding.dailyImgViewPager
                    ) { tab, position -> }.attach()

                    if (currentList[position].files.size == 1) {
                        binding.intoTabLayout.visibility = View.GONE
                    } else {
                        binding.intoTabLayout.visibility = View.VISIBLE
                    }

                }

            }

            DailyBoardViewType.TEXT -> {
                if (holder is DailyBoardTextItemViewHolder) {
                    val binding = holder.getBind()
                    holder.bindView(
                        binding,
                        currentList[position],
                        binding.writeName,
                        binding.postingContents,
                        binding.likeImg,
                        binding.disLikeImg,
                        binding.likeCount,
                        binding.disLikeCount,
                        binding.userProfileImg
                    )
                }
            }

            DailyBoardViewType.VIDEO -> {
                if (holder is DailyBoardVideoItemViewHolder) {
                    val viedeoHolder =
                        holder.bindView(holder.getBind(), currentList[position], holder)
                    viedeoHolder.processVideo(viedeoHolder)
                }
            }
        }

    }

    fun pauseVideoOnstop() {
        recentVideoItemViewHolder?.pauseVideo()
    }

    fun releaseVideo() {
        currentList.forEach { dailyBoard ->
            if (dailyBoard.viewType == DailyBoardViewType.VIDEO) {
                recentVideoItemViewHolder?.releaseVideo()
            }
        }
    }

    fun resumeVideoOnResume() {
        if (_isRecentVideoInitalized) {
            recentVideoItemViewHolder?.let { viewHolder ->
                viewHolder.playVideo()
            }
        }
    }

}