package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardCommentItemBinding
import com.orhanobut.logger.Logger
import model.Comment

class DailyBoardNestedCommentAdapter(private val neestedComments: List<Comment>) :
    RecyclerView.Adapter<DailyBoardNestedCommentAdapter.NestedCommentViewHolder>() {
    inner class NestedCommentViewHolder(private val binding: DailyBoardCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) {
            Glide.with(binding.root.context).load(item.witerUri).into(binding.userProfileImg)
            binding.writeName.setText(item.writerName)
            binding.postingContents.setText(item.contents)
            binding.selectReply.setOnClickListener {
                //popstReplyListener.postReplyClick(item)
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