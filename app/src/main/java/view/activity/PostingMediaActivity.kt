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
import dagger.hilt.android.AndroidEntryPoint
import model.File
import util.Util
import viewmodel.PostingMediaActivityViewModel

@AndroidEntryPoint
class PostingMediaActivity : AppCompatActivity() {
    private val imageAdapter = ImageAdapter(false)
    private val viewModel: PostingMediaActivityViewModel by viewModels()
    private var choosenFiles = mutableListOf<File>()
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

            val contents = if (binding.boardContentes.text.isEmpty()) {
                binding.boardContentesFull.text.toString()
            } else {
                binding.boardContentes.text.toString()
            }
            viewModel.createPost(contents, choosenFiles)
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
            val uploadFileUri = intent.getSerializableExtra("choosenImages") as ArrayList<File>
            val imagesRecyclerView = binding.choosenPictureList
            imagesRecyclerView.adapter = imageAdapter
            imagesRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            imageAdapter.setImages(uploadFileUri)
            choosenFiles.addAll(uploadFileUri)
            return
        }

        if (intent.getSerializableExtra("choosenVideo") != null) {
            val uri = intent.getStringExtra("choosenVideo") ?: "nothing"
            binding.choosenVideo.visibility = View.VISIBLE
            binding.choosenPictureList.visibility = View.GONE
            binding.choosenVideo.setVideoURI(Uri.parse(uri))
            binding.choosenVideo.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }


            val video = listOf<File>(File(uri))
            choosenFiles.addAll(video)
            return
        }
    }

}