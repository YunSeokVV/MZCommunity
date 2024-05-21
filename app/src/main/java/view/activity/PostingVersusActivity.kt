package view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.databinding.ActivityPostingVersusBinding
import dagger.hilt.android.AndroidEntryPoint
import util.FirebaseAuth
import view.fragment.LoadingDialogFragment
import viewmodel.VersusActivityViewModel
@AndroidEntryPoint
class PostingVersusActivity : AppCompatActivity() {
    private val viewModel: VersusActivityViewModel by viewModels()
    private val loadingDialogFragment = LoadingDialogFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPostingVersusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.posting.setOnClickListener {
            val inputTitle = binding.inputTitle.text.toString()
            val inputOpinion1 = binding.inputOpinion1.text.toString()
            val inputOpinion2 = binding.inputOpinion2.text.toString()
            loadingDialogFragment.show(supportFragmentManager, loadingDialogFragment.tag)
            viewModel.postVersusBoard(
                inputTitle,
                inputOpinion1,
                inputOpinion2,
                FirebaseAuth.auth.uid.toString()
            )
        }

        viewModel.postingResponse.observe(this, Observer {
            if(it){
                loadingDialogFragment.dismiss()
                finish()
            }
        })
    }
}