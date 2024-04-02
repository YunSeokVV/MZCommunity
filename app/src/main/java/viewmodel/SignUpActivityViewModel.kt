package viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import usecase.SetUserNicknameUseCase
import javax.inject.Inject

@HiltViewModel
class SignUpActivityViewModel @Inject constructor(private val setUserNicknameUseCase : SetUserNicknameUseCase) : ViewModel(){
    suspend fun setUserNickName(nickName : String){
        setUserNicknameUseCase(nickName)
    }
}