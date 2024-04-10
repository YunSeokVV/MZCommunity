package view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentVersusBinding

class VersusFragment : Fragment() {
    private lateinit var binding : FragmentVersusBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        

        return binding.root
    }
}