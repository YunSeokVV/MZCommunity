package view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val fragmnetTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmnetTransaction.replace(R.id.fragment_container, MyPageFragment())
//        fragmnetTransaction.commit()
    }
}