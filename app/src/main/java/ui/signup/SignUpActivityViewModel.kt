package ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.Response
import domain.signup.SignUpUsecase
import javax.inject.Inject

@HiltViewModel
class SignUpActivityViewModel @Inject constructor(private val application : Application, private val signUpUsecase: SignUpUsecase) : AndroidViewModel(application){

    private val _saveUserInfo = MutableLiveData<Boolean>(false)
    val saveUserInfo : LiveData<Boolean>get() = _saveUserInfo

    suspend fun setUserNickName(nickName : String){
        signUpUsecase.setUserNickname(nickName)
    }

    suspend fun saveUserLoginInfo(email : String, passwd : String) = viewModelScope.launch {
        signUpUsecase.saveUserLoginInfo(email, passwd, application.applicationContext).collect{
            when(it){
                is Response.Success -> {
                    _saveUserInfo.value = true
                }

                is Response.Failure -> {

                }
            }
        }
    }

}