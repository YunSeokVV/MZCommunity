package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import com.orhanobut.logger.Logger
import model.Comment

class DailyBoardCommentAdapter(
    private val comments: List<Comment>,
    private val popstReplyListener: PostReplyOnClickListener,
    private val showCommentListener: ShowCommentListener
) :
    RecyclerView.Adapter<DailyBoardCommentAdapter.DailyBoardCommentViewHolder>(){
    var nestedCommentList = listOf<Comment>()

    interface PostReplyOnClickListener {
        fun postReplyClick(comment: Comment)
    }

    interface ShowCommentListener {
        fun showNestedComment(comment: Comment, recyclerView: RecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DailyBoardCommentViewHolder(
        DailyBoardCommentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )


    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: DailyBoardCommentViewHolder, position: Int) {
        holder.bind(comments.get(position))
    }


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