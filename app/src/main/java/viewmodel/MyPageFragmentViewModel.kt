package viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import model.Response
import model.User
import usecase.MypageUsecasae
import javax.inject.Inject

@HiltViewModel
class MyPageFragmentViewModel @Inject constructor(private val mypageUsecasae: MypageUsecasae) :
    ViewModel() {
    init {
        getUserProfile()
    }

    private val _isUpdateComplte = MutableLiveData<Boolean>()
    val isUpdateComplte : LiveData<Boolean>
        get() {
            return _isUpdateComplte
        }

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() {
            return _user
        }

    private val _isEditMode = MutableLiveData<Boolean>(false)

    val isEditMode: LiveData<Boolean>
        get() {
            return _isEditMode
        }

    fun setEditMode() {
        val currentValue = _isEditMode.value ?: false
        _isEditMode.value = !currentValue
    }

    fun getUserProfile() = viewModelScope.launch {
        mypageUsecasae.getUserProfile().collect {
            Logger.v(it.toString())
            _user.value = it
        }
    }

    fun updateProfile(nickName: String, profile: Uri) = viewModelScope.launch {
        mypageUsecasae.updateProfile(nickName, profile).collect {
            when (it) {
                is Response.Success -> {
                    _isUpdateComplte.value = it.data ?: false
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

}