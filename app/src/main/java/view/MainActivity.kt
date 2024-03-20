package view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardFragment()).commit()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_board -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BoardFragment()).commit()
                    true
                }

                R.id.fragment_versus -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, VersusFragment()).commit()
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

    }

    fun showBoardFragmnet() {
        binding.bottomNavigationView.setSelectedItemId(R.id.fragment_board)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, BoardFragment())
            .commit()
    }
}