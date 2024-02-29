package viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModel(string: String) : ViewModel() {
    val result = string
}

class MainActivityViewModelFactory(private val string: String): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(string) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}