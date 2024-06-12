package ui.mypage

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Response
import kotlinx.coroutines.launch
import data.model.LoginedUser
import domain.user.GetUserProfileUsecase
import domain.user.UpdateProfileUsecase
import javax.inject.Inject

@HiltViewModel
class MyPageFragmentViewModel @Inject constructor(
    private val updateProfileUsecase: UpdateProfileUsecase,
    private val getUserProfileUsecase: GetUserProfileUsecase
) :
    ViewModel() {

    private val _isUpdateComplte = MutableLiveData<Boolean>()
    private val _logined_user = MutableLiveData<LoginedUser>()
    val loginedUser: LiveData<LoginedUser>
        get() {
            return _logined_user
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
        getUserProfileUsecase().collect {
            _logined_user.value = it
        }
    }

    fun updateProfile(nickName: String, profile: Uri) = viewModelScope.launch {
        updateProfileUsecase(nickName, profile).collect {
            when (it) {
                is Response.Success -> {
                    _isUpdateComplte.value = it.data ?: false
                    getUserProfile()
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

}