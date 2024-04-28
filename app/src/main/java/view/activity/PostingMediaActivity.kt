package view.activity

import adapter.ImageAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityPostingMediaBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.Images
import util.Util
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
        handleIntent(binding, getIntent())

        viewModel.isPostingComplete.observe(this, Observer { data ->
            if (data) {
                Util.showProgressDialog(this, false)
                ChooseMediaActivity.chooseMediaActivity.finish()
                finish()

            }
        })

        binding.arrowBack.setOnClickListener {
            finish()
        }

        binding.postBoard.setOnClickListener {
            Util.showProgressDialog(this, true)
        }

    }

    private fun handleIntent(binding: ActivityPostingMediaBinding, intent: Intent) {
        if (intent.getStringExtra("onlyText") != null) {
            binding.boardContentesFull.visibility = View.VISIBLE
            binding.choosenPictureList.visibility = View.GONE
            binding.boardContentes.visibility = View.GONE
            return
        }

        if (intent.getSerializableExtra("choosenImages") != null) {
            val uploadImagesUri = intent.getSerializableExtra("choosenImages") as ArrayList<Images>
            val imagesRecyclerView = binding.choosenPictureList
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            imageAdapter.setImages(uploadImagesUri)

            return
        }

        if (intent.getSerializableExtra("choosenVideo") != null) {
            val uri = intent.getStringExtra("choosenVideo")
            binding.choosenVideo.visibility = View.VISIBLE
            binding.choosenPictureList.visibility = View.GONE

            binding.choosenVideo.setVideoURI(Uri.parse(uri))
            binding.choosenVideo.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }
            return
        }
    }

}