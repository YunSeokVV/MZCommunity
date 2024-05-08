package view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMainBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.LoginedUser
import view.fragment.BoardFragment
import view.fragment.MyPageFragment
import view.fragment.PostingFragment
import view.fragment.VersusFragment
import viewmodel.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel : MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginedUserProfile : LoginedUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Logger.v("gd")

        viewModel.loginedUserInfo.observe(this, Observer { user ->
            loginedUserProfile = user

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BoardFragment(loginedUserProfile)).commit()


            binding.bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.fragment_board -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, BoardFragment(loginedUserProfile)).commit()
                        true
                    }

                    R.id.fragment_versus -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, VersusFragment(loginedUserProfile)).commit()
                        true
                    }

                    R.id.fragment_write_board -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, PostingFragment()).commit()
                        true
                    }

                    R.id.fragment_my_page -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, MyPageFragment()).commit()
                        true
                    }

                    else -> false
                }
            }
        })



    }

    fun showBoardFragmnet() {
        binding.bottomNavigationView.setSelectedItemId(R.id.fragment_board)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BoardFragment(loginedUserProfile))
            .commit()
    }
}