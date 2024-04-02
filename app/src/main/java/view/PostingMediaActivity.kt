package view

import adapter.ImageAdapter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityPostingMediaBinding
import dagger.hilt.android.AndroidEntryPoint
import model.Images
import viewmodel.PostingMediaActivityViewModel

@AndroidEntryPoint
class PostingMediaActivity : AppCompatActivity() {
    private val imageAdapter = ImageAdapter(false)
    private val viewModel: PostingMediaActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_media)
        val binding = ActivityPostingMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val progressDialog = ProgressDialog(this)
        val intent = intent
        val uploadImagesUri = intent.getSerializableExtra("choosenImages") as ArrayList<Images>

        if (uploadImagesUri.isEmpty()) {
            binding.boardContentesFull.visibility = View.VISIBLE
            binding.choosenPictureList.visibility = View.GONE
            binding.boardContentes.visibility = View.GONE
        } else {
            val imagesRecyclerView = binding.choosenPictureList
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            imageAdapter.setImages(uploadImagesUri)
            imagesRecyclerView.visibility = View.VISIBLE
        }

        viewModel.isPostingComplete.observe(this, Observer { data ->
            if(data){
                progressDialog.dismiss()
                ChooseMediaActivity.chooseMediaActivity.finish()
                finish()

            }
        })

        binding.arrowBack.setOnClickListener {
            finish()
        }

        binding.postBoard.setOnClickListener {
            progressDialog.show()
            if (uploadImagesUri.isEmpty()) {
                viewModel.createPost(binding.boardContentesFull.text.toString(), uploadImagesUri)
            } else {
                viewModel.createPost(binding.boardContentes.text.toString(), uploadImagesUri)
            }

        }

    }
}