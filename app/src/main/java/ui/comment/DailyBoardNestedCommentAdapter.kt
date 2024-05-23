package ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import data.model.Comment


class DailyBoardNestedCommentAdapter(private val neestedComments: List<Comment>, private val postReplyOnClickListener : PostReplyOnClickListener) :
    RecyclerView.Adapter<DailyBoardNestedCommentAdapter.NestedCommentViewHolder>() {

    interface PostReplyOnClickListener {
        fun postReplyClick(comment: Comment)
    }

    inner class NestedCommentViewHolder(private val binding: DailyBoardCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) {
            Glide.with(binding.root.context).load(item.witerUri).into(binding.userProfileImg)
            binding.writeName.setText(item.writerName)
            binding.postingContents.setText(item.contents)
            binding.selectReply.setOnClickListener {

                postReplyOnClickListener.postReplyClick(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NestedCommentViewHolder(
        DailyBoardCommentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = neestedComments.size

    override fun onBindViewHolder(holder: NestedCommentViewHolder, position: Int) {
        holder.bind(neestedComments.get(position))
    }

}