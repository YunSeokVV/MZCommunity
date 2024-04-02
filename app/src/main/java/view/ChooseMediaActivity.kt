package view

import adapter.ImageAdapter
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMultiImagePostingBinding
import model.Images
import util.Util
import viewmodel.ChooseMediaActivityViewModel


class ChooseMediaActivity : AppCompatActivity() {
    companion object{
        var chooseMediaActivity = ChooseMediaActivity()
    }

    private val chooseMediaActivityViewModel: ChooseMediaActivityViewModel by viewModels()

    private val imageAdapter = ImageAdapter(true)
    lateinit var cam_uri: Uri

    private val startCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                imageAdapter.setImages(chooseMediaActivityViewModel.setPicturedImageUri(Images(cam_uri.toString())))
                chooseMediaActivityViewModel.setChoosen(true)
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_image_posting)
        chooseMediaActivity = this
        val binding = ActivityMultiImagePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chooseMediaActivityViewModel.isChoosen.observe(this, Observer<Boolean> { isChoosen ->
            if (isChoosen) {
                binding.choosingImages.visibility = View.INVISIBLE
                binding.textInform.visibility = View.INVISIBLE
                binding.choosenPictureList.visibility = View.VISIBLE
            } else {
                binding.choosenPictureList.visibility = View.INVISIBLE
                binding.choosingImages.visibility = View.VISIBLE
                binding.textInform.visibility = View.VISIBLE
            }
        })

        val imagesRecyclerView = binding.choosenPictureList

        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    val choosenUrls = mutableListOf<Images>()
                    for (i in uris.iterator()) {
                        choosenUrls.add(Images(i.toString()))
                    }
                    imageAdapter.setImages(choosenUrls)
                    chooseMediaActivityViewModel.setChoosen(true)
                }
            }

        binding.sotrageImages.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.takePhoto.setOnClickListener {

            val permissionCheck: Int =
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(android.Manifest.permission.CAMERA),
                    0
                )
            } else {
                takePhoto()
            }
        }

        binding.arrowBack.setOnClickListener {
            finish()
        }

        binding.nextStep.setOnClickListener {
            val intent = Intent(this, PostingMediaActivity::class.java)

            intent.putExtra("choosenImages", imageAdapter.getImages())
            startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Util.makeToastMessage("권한을 허용해야 사진 촬영이 가능합니다 :)", this)
            }
        }

    }

    fun takePhoto() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )?.let {
            cam_uri = it
        }
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri)
        startCamera.launch(cameraIntent)
    }

}