package ui.posting.versusboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityPostingVersusBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.base.BaseActivity
import util.FirebaseAuth
import ui.loading.LoadingDialogFragment

@AndroidEntryPoint
class PostingVersusActivity : BaseActivity<ActivityPostingVersusBinding>({ActivityPostingVersusBinding.inflate(it)}) {
    private val viewModel: PostingVersusActivityViewModel by viewModels()
    private val loadingDialogFragment = LoadingDialogFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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