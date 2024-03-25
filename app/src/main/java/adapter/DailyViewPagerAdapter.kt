package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.DailyBoardRowItemBinding

class DailyViewPagerAdapter(private val imagesUri: List<Uri>, private val itemCounts: Int) :
    RecyclerView.Adapter<DailyViewPagerAdapter.ViewHolder>() {

    class ViewHolder(val binding: DailyBoardRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        DailyBoardRowItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )


    override fun getItemCount(): Int = itemCounts

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            binding.apply {
                imagesUri.forEachIndexed { index, uri ->
                    if (position == index) {
                        Glide.with(itemView)
                            .load(uri)
                            .into(binding.content)
                    }
                }
            }
        }
    }
}

