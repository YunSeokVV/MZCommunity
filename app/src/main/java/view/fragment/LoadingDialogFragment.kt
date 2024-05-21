package view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mzcommunity.databinding.FragmentLoadingDialogBinding

class LoadingDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoadingDialogBinding.inflate(layoutInflater)



        return binding.root
    }
}