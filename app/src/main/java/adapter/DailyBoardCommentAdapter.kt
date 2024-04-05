package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import com.orhanobut.logger.Logger
import model.Comment

class DailyBoardCommentAdapter(
    private val comments: List<Comment>,
    private val popstReplyListener: PostReplyOnClickListener
) :
    RecyclerView.Adapter<DailyBoardCommentAdapter.DailyBoardCommentViewHolder>() {

    interface PostReplyOnClickListener {
        fun postReplyClick(comment: Comment)
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
        fun bind(item: Comment) {
            Glide.with(binding.root.context).load(item.witerUri).into(binding.userProfileImg)
            binding.writeName.setText(item.writerName)
            binding.postingContents.setText(item.contents)
            binding.selectReply.setOnClickListener {
                popstReplyListener.postReplyClick(item)
            }
        }


    }
}