package view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.example.mzcommunity.databinding.FragmentVersusBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VersusFragment : Fragment() {
    private lateinit var binding : FragmentVersusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVersusBinding.inflate(inflater)
        binding.posting.setOnClickListener {
            val intent = Intent(
                activity,
                PostingVersusActivity::class.java
            )
            startActivity(intent)
        }


        return binding.root
    }
}