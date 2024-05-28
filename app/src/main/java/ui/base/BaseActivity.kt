package ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding>(val bindingFactory: (LayoutInflater) -> T) :
    AppCompatActivity() {
    private var _binding : T? = null
    val binding get() =_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingFactory(layoutInflater)
        setContentView(binding?.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}