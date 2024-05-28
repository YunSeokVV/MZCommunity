package ui.main

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.mzcommunity.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.mzcommunity.R
import data.model.DailyBoard
import data.model.LoginedUser
import ui.base.BaseActivity
import ui.board.dailyboard.DailyBoardFragment
import ui.mypage.MyPageFragment
import ui.mypage.MyPageFragment.loginUserListener
import ui.posting.dailyboard.PostingFragment
import ui.board.versusboard.VersusFragment
import ui.main.model.Screen


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>({ActivityMainBinding.inflate(it)}), loginUserListener {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var loginedUserProfile: LoginedUser

    private val dailyBoards: ArrayList<DailyBoard> by lazy {
        intent.getSerializableExtra("dailyBoards") as ArrayList<DailyBoard>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val userProfile: LoginedUser = intent.getSerializableExtra("userProfile") as LoginedUser
        loginedUserProfile = userProfile

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                setFragment(Screen.DAILY_BOARD)
            ).commit()


        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_board -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            setFragment(Screen.DAILY_BOARD)
                        )
                        .commit()
                    true
                }

                R.id.fragment_versus -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, setFragment(Screen.VERSUS_BOARD))
                        .commit()
                    true
                }

                R.id.fragment_write_board -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PostingFragment()).commit()
                    true
                }

                R.id.fragment_my_page -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, setFragment(Screen.MY_PAGE))
                        .commit()
                    true
                }

                else -> false
            }
        }


    }

    fun showBoardFragmnet() {
        binding.bottomNavigationView.setSelectedItemId(R.id.fragment_board)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, setFragment(Screen.DAILY_BOARD))
            .commit()
    }

    override fun loginUserListner(loginedUser: LoginedUser) {
        loginedUserProfile = loginedUser
    }

    private fun setFragment(screen: Screen): Fragment {
        val bundle = Bundle()
        bundle.putSerializable("loginedUserProfile", loginedUserProfile)

        when (screen) {
            Screen.DAILY_BOARD -> {
                bundle.putParcelableArrayList(
                    "dailyBoards",
                    dailyBoards as ArrayList<out Parcelable?>?
                )
                val dailyBoardFragment = DailyBoardFragment()
                dailyBoardFragment.arguments = bundle
                return dailyBoardFragment
            }

            Screen.VERSUS_BOARD -> {
                val versusFragment = VersusFragment()
                versusFragment.arguments = bundle
                return versusFragment
            }

            Screen.MY_PAGE -> {
                val mypageFragemnt = MyPageFragment()
                mypageFragemnt.arguments = bundle
                return mypageFragemnt
            }

        }

    }
}