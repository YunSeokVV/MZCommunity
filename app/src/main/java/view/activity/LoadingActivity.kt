package view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityLoadingBinding
import com.example.mzcommunity.databinding.ActivityLoginBinding
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.LoginedUser
import util.Util
import viewmodel.LoadingActivityViewModel
import viewmodel.MainActivityViewModel

@AndroidEntryPoint
class LoadingActivity : AppCompatActivity() {
    private val viewModel : LoadingActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        val binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadComplete.observe(this, Observer {loadComplete ->
            if(loadComplete){
                val intent = Intent(this, MainActivity::class.java)
                val userProfile = viewModel.logined_userInfo.value
                val dailyBoards = ArrayList(viewModel.dailyBoards.value)

                intent.putExtra("userProfile", userProfile)
                intent.putExtra("dailyBoards", dailyBoards)
                startActivity(intent)
                finish()
            }
        })

    }
}