package adapter

import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import com.example.mzcommunity.databinding.ItemLoadingBinding
import com.orhanobut.logger.Logger
import model.Comment
import view.LoadingViewHolder


private const val ITEM = 0
private const val LOADING = 1

class DailyBoardCommentAdapter(
    private var comments: MutableList<Comment?>,
    private val popstReplyListener: PostReplyOnClickListener,
    private val showCommentListener: ShowCommentListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var nestedCommentList = listOf<Comment>()


    interface PostReplyOnClickListener {
        fun postReplyClick(comment: Comment)
    }

    interface ShowCommentListener {
        fun showNestedComment(comment: Comment, recyclerView: RecyclerView)
    }

    fun showProgress() {
        comments.add(null)
        notifyItemInserted(comments.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (comments.get(position) == null) LOADING else ITEM

    }

    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == ITEM) {
            return DailyBoardCommentViewHolder(
                DailyBoardCommentItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            return LoadingViewHolder(
                ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DailyBoardCommentViewHolder) {
            comments.get(position)?.let { holder.bind(it) }
        } else if (holder is LoadingViewHolder) {
            holder.showLoading(holder, position)
        }
    }

    override fun getItemCount() = comments.size


    inner class DailyBoardCommentViewHolder(private val binding: DailyBoardCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nestedRecyclerView = binding.nestedCommentRecyler

        fun bind(item: Comment) {
            Glide.with(binding.root.context).load(item.witerUri).into(binding.userProfileImg)
            binding.writeName.setText(item.writerName)
            binding.postingContents.setText(item.contents)
            binding.selectReply.setOnClickListener {
                popstReplyListener.postReplyClick(item)
            }

            if (item.hasNestedComment) {
                binding.showComment.visibility = View.VISIBLE
            }

            binding.showComment.setOnClickListener {
                showCommentListener.showNestedComment(item, nestedRecyclerView)

                binding.showComment.visibility = View.GONE
                nestedRecyclerView.visibility = View.VISIBLE
                nestedRecyclerView.layoutManager =
                    LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)


            }

        }
    }

    fun setNestedCommentsList(comments: List<Comment>) {
        Logger.v(comments.toString())
        nestedCommentList = comments
    }


}