package ui.main

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.DailyBoard
import data.model.LoginedUser
import ui.board.dailyboard.DailyBoardFragment
import ui.mypage.MyPageFragment
import ui.mypage.MyPageFragment.loginUserListener
import ui.posting.dailyboard.PostingFragment
import ui.board.versusboard.VersusFragment


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), loginUserListener {
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginedUserProfile: LoginedUser

    private val dailyBoards: ArrayList<DailyBoard> by lazy {
        intent.getSerializableExtra("dailyBoards") as ArrayList<DailyBoard>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val userProfile: LoginedUser = intent.getSerializableExtra("userProfile") as LoginedUser
        loginedUserProfile = userProfile

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                setFragment(0)
            ).commit()


        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_board -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            setFragment(0)
                        )
                        .commit()
                    true
                }

                R.id.fragment_versus -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, setFragment(1))
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
                        .replace(R.id.fragment_container, setFragment(2))
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
            .replace(R.id.fragment_container, setFragment(0))
            .commit()
    }

    override fun loginUserListner(loginedUser: LoginedUser) {
        loginedUserProfile = loginedUser
    }

    // todo : emum 적용할 것. 각 screen 에 따라서 보여줘야 할 화면이 달라짐
    private fun setFragment(screen : Int) : Fragment {
        val bundle = Bundle()
        bundle.putSerializable("loginedUserProfile", loginedUserProfile)

        // 일상 게시글
        if(screen == 0){
            bundle.putParcelableArrayList("dailyBoards", dailyBoards as ArrayList<out Parcelable?>?)
            val dailyBoardFragment = DailyBoardFragment()
            dailyBoardFragment.arguments = bundle
            return dailyBoardFragment
        }

        // 밸런스 게시판
        if(screen == 1){
            val versusFragment = VersusFragment()
            versusFragment.arguments = bundle
            return versusFragment
        }

        // 마이페이지
        if(screen == 2){
            val mypageFragemnt = MyPageFragment()
            mypageFragemnt.arguments = bundle
            return mypageFragemnt
        }
        return Fragment()
    }
}