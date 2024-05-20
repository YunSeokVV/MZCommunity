package adapter

import android.net.Uri
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import com.example.mzcommunity.databinding.ItemLoadingBinding
import model.Comment
import model.LoginedUser
import view.LoadingViewHolder


private const val ITEM = 0
private const val LOADING = 1

class DailyBoardCommentAdapter(
    private val popstReplyListener: PostReplyOnClickListener,
    private val showCommentListener: ShowCommentListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var nestedCommentList = mutableListOf<Comment>()

    var comments = mutableListOf<Comment?>()

    interface PostReplyOnClickListener {
        fun postReplyClick(comment: Comment, recyclerView: RecyclerView)
    }

    interface ShowCommentListener {
        fun showNestedComment(comment: Comment, recyclerView: RecyclerView)
    }

    fun addComment(loginedUser: LoginedUser, contents: String, parentUID: String) {
        val comment =
            Comment(Uri.parse(loginedUser.profileUri), loginedUser.nickName, contents, parentUID, false)
        comments.add(0, comment)
        notifyItemInserted(0)
    }

    fun showProgress() {
        comments.add(null)
        notifyItemInserted(comments.size)
    }

    fun hideProgress() {
        comments.removeAt(comments.size - 1)
        notifyItemRemoved(comments.size)
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
                popstReplyListener.postReplyClick(item, nestedRecyclerView)
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
        nestedCommentList = comments.toMutableList()
    }

    fun addNestedCommentItem(loginedUser: LoginedUser, contents: String, parentUID: String){
        val comment =
            Comment(Uri.parse(loginedUser.profileUri), loginedUser.nickName, contents, parentUID, false)

        nestedCommentList.add(comment)
    }

}