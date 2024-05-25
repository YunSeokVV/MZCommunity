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
        fun increaseLike(dailyBoard: DailyBoard, adapterPosition: Int)
    }

    interface IncreaseDisLike {
        fun increaseDisLike(dailyBoard: DailyBoard, adapterPosition: Int)
    }

    interface ShowComment {
        fun showComment(dailyBoard: DailyBoard)
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
                if (currentViewHolder != null && currentViewHolder.itemViewType.equals(
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
            } else if (item.favourability.equals("usual")) {
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            }

            binding.likeCount.text = item.like.toString()
            binding.disLikeCount.text = item.disLike.toString()
            binding.dailyImgViewPager.adapter =
                DailyViewPagerAdapter(
                    currentList[adapterPosition].files,
                    currentList[adapterPosition].files.size,
                )
            TabLayoutMediator(
                binding.intoTabLayout,
                binding.dailyImgViewPager
            ) { tab, position -> }.attach()

            if (currentList[adapterPosition].files.size == 1) {
                binding.intoTabLayout.visibility = View.GONE
            } else {
                binding.intoTabLayout.visibility = View.VISIBLE
            }

            Glide.with(binding.root.context).load(Uri.parse(item.writerProfileUri))
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

    inner class DailyBoardTextItemViewHolder(
        private val binding: DailyBoardTextItemListBinding
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
            } else if (item.favourability.equals("usual")) {
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            }

            binding.likeCount.text = item.like.toString()
            binding.disLikeCount.text = item.disLike.toString()

            Glide.with(binding.root.context).load(Uri.parse(item.writerProfileUri))
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
                if (dailyBoard.favourability == "usual") {
                    setThumbNailColor(true, binding.likeImg)

                    // 좋아요 +1
                    binding.likeCount.text =
                        setFavourCount((binding.likeCount.text.toString()).toInt(), true).toString()

                    // 사용자가 이전에 싫어요를 눌렀다가 좋아요를 누른 경우
                } else if (dailyBoard.favourability == "disLike") {
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
                } else if (dailyBoard.favourability == "like") {
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
            thumbNail.backgroundTintList = colorStateList
        }

    }


    inner class DailyBoardVideoItemViewHolder(
        private val binding: DailyBoardVideoItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

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

        fun bind(item: DailyBoard, position: Int, holder: DailyBoardVideoItemViewHolder) {
            binding.writeName.text = item.writerNickname
            binding.postingContents.text = item.boardContents

            if (item.favourability.equals("like")) {
                setThumbNailColor(true, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            } else if (item.favourability.equals("disLike")) {
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(true, binding.disLikeImg)
            } else if (item.favourability.equals("usual")) {
                setThumbNailColor(false, binding.likeImg)
                setThumbNailColor(false, binding.disLikeImg)
            }

            binding.likeCount.text = item.like.toString()
            binding.disLikeCount.text = item.disLike.toString()

            Glide.with(binding.root.context).load(Uri.parse(item.writerProfileUri))
                .into(binding.userProfileImg)


            if (position == 0) {
                processVideo(holder)
            }
        }

        init {
            binding.comment.setOnClickListener {
                val dailyBoard = currentList.get(adapterPosition)
                showComment.showComment(dailyBoard)
            }

            binding.likeImg.setOnClickListener {
                val dailyBoard = currentList.get(adapterPosition)
                increaseLike.increaseLike(dailyBoard, adapterPosition)
                releaseVideo()

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
                releaseVideo()

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

            null -> throw IllegalArgumentException("Invalid view type")
        }
//        if (viewType.equals(DailyBoardViewType.TEXT)) {
//            val binding =
//                DailyBoardTextItemListBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            return DailyBoardTextItemViewHolder(binding)
//        } else if (viewType.equals(DailyBoardViewType.IMAGE)) {
//            val binding =
//                DailyBoardImageItemListBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            return DailyBoardImageItemViewHolder(binding)
//
//            // DailyBoardViewType.VIDEO
//        } else {
//            val binding =
//                DailyBoardVideoItemListBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            return DailyBoardVideoItemViewHolder(binding)
//        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (currentList[position].viewType) {
            DailyBoardViewType.IMAGE -> {
                (holder as DailyBoardImageItemViewHolder).bind(currentList[position])
            }

            DailyBoardViewType.TEXT -> {
                (holder as DailyBoardTextItemViewHolder).bind(currentList[position])
            }

            DailyBoardViewType.VIDEO -> {
                (holder as DailyBoardVideoItemViewHolder).bind(
                    currentList[position],
                    position,
                    holder
                )
            }
        }

    }

    fun pauseVideoOnstop() {
        recentVideoItemViewHolder?.pauseVideo()
    }

    fun releaseVideo() {
        currentList.forEach { dailyBoard ->
            if (dailyBoard.viewType.equals(2)) {
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