package adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import model.File

class ImageAdapter(private val isRadius : Boolean) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    //private val imagesData = mutableListOf<Images>()
    private val fileData = ArrayList<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context)
        if(isRadius){
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_selected_item_radius, parent,false)
            return ViewHolder(itemView)
        } else{
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_selected_item, parent,false)
            return ViewHolder(itemView)
        }

    }

    override fun getItemCount(): Int {
        return fileData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fileData[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val choosenImage : ImageView = itemView.findViewById(R.id.userChoosenImage)

        fun bind(item : File){
            choosenImage.setImageURI(Uri.parse(item.uri))
            Glide.with(itemView.context).load(item.uri).into(choosenImage)
        }

    }

    fun setImages(data : List<File>){
        fileData.clear()
        fileData.addAll(data)
        notifyDataSetChanged()
    }

    fun getImages() = fileData
}