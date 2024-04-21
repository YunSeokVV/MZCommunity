package view

import androidx.recyclerview.widget.RecyclerView
import com.example.mzcommunity.databinding.ItemLoadingBinding

class LoadingViewHolder(private val binding : ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
    val progressBar = binding.progressBar

    fun showLoading(viewHolder : LoadingViewHolder, position : Int) {}
}