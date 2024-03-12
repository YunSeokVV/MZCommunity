package view

import adapter.ImageAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityPostingMediaBinding
import com.orhanobut.logger.Logger
import model.Images
import java.io.Serializable


class PostingMediaActivity : AppCompatActivity() {
    private val imageAdapter = ImageAdapter(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_media)
        val binding = ActivityPostingMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = intent
        val uploadImagesUri = intent.getSerializableExtra("choosenImages") as ArrayList<Images>

        Logger.v(uploadImagesUri.size.toString())
        Logger.v(uploadImagesUri.toString())

        val imagesRecyclerView = binding.choosenPictureList
        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        imageAdapter.setImages(uploadImagesUri)

        binding.arrowBack.setOnClickListener {
            finish()
        }


    }
}