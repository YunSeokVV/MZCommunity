package view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmnetTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmnetTransaction.replace(R.id.fragment_container, MyPageFragment())
        fragmnetTransaction.commit()
    }
}