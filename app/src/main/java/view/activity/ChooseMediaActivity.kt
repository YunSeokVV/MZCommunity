package view.activity

import adapter.ImageAdapter
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMultiImagePostingBinding
import model.File
import util.Util
import viewmodel.ChooseMediaActivityViewModel

class ChooseMediaActivity : AppCompatActivity() {
    companion object {
        var chooseMediaActivity = ChooseMediaActivity()
    }

    private val chooseMediaActivityViewModel: ChooseMediaActivityViewModel by viewModels()
    private val imageAdapter = ImageAdapter(true)
    lateinit var cam_uri: Uri
    private lateinit var binding: ActivityMultiImagePostingBinding

    private val photo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                val image = mutableListOf<File>()
                image.add(File(cam_uri.toString()))
                imageAdapter.setImages(
                    image
                )
                showUplodableView(binding, true)
            }
        })

    private val video = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                binding.choosenVideo.setVideoURI(cam_uri)
                binding.choosenVideo.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                }
                showUplodableView(binding, false)
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_image_posting)
        chooseMediaActivity = this
        binding = ActivityMultiImagePostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagesRecyclerView = binding.choosenPictureList

        imagesRecyclerView.adapter = imageAdapter
        imagesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val pickFile =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
                if (uris.isNotEmpty()) {
                    val choosenUrls = mutableListOf<File>()
                    for (i in uris.iterator()) {
                        choosenUrls.add(File(i.toString()))
                    }
                    imageAdapter.setImages(choosenUrls)
                    showUplodableView(binding, true)
                }
            }

        val pickVideo =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                binding.choosenVideo.setVideoURI(uri)
                binding.choosenVideo.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                }
                if (uri != null) {
                    cam_uri = uri
                }
                showUplodableView(binding, false)
            }

        binding.sotrageImages.setOnClickListener {
            var fileType: Array<String> = arrayOf("사진", "동영상")
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("업로드 형식 선택")
                .setItems(fileType, DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        // 사진 선택
                        0 -> {
                            pickFile.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }

                        // 동영상 선택
                        1 -> {
                            pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        }
                    }

                })
            builder.show()
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
            settingIntent(intent)
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

    private fun takePhoto() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        this.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )?.let {
            cam_uri = it
        }

        var fileType: Array<String> = arrayOf("사진", "동영상")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("업로드 형식 선택")
            .setItems(fileType, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    // 사진 선택
                    0 -> {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri)
                        photo.launch(cameraIntent)

                    }

                    // 동영상 선택
                    1 -> {
                        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri)
                        video.launch(videoIntent)
                    }
                }

            })
        builder.show()
    }

    private fun settingIntent(intent: Intent) : Intent{
        if(binding.choosingImages.visibility == View.VISIBLE){
            intent.putExtra("onlyText", "text")
            return intent
        }

        if(binding.choosenPictureList.visibility == View.VISIBLE){
            intent.putExtra("choosenImages", imageAdapter.getImages())
            return intent
        }

        if(binding.choosenVideo.visibility == View.VISIBLE){
            intent.putExtra("choosenVideo", cam_uri.toString())
            return intent
        }
        return intent
    }


    //isImages : 이미지를 표현하는 리사이클러뷰가 보여지는지 videoView가 보여져야 하는지 판별해주는 변수
    private fun showUplodableView(binding: ActivityMultiImagePostingBinding, isImages: Boolean) {
        binding.choosingImages.visibility = View.INVISIBLE
        binding.textInform.visibility = View.INVISIBLE

        if (isImages) {
            binding.choosenPictureList.visibility = View.VISIBLE
            binding.choosenVideo.visibility = View.INVISIBLE
        } else {
            binding.choosenPictureList.visibility = View.INVISIBLE
            binding.choosenVideo.visibility = View.VISIBLE
        }
    }

}