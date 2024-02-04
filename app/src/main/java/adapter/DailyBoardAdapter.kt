package adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mzcommunity.databinding.DailyBoardItemListBinding
import model.DailyPosting

class DailyBoardAdapter() : ListAdapter<DailyPosting, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<DailyPosting>(){
            override fun areItemsTheSame(oldItem: DailyPosting, newItem: DailyPosting): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: DailyPosting, newItem: DailyPosting): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class DailyBoardViewHolder(private val binding : DailyBoardItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data : DailyPosting){
            with(binding){

            }
        }
    }

}