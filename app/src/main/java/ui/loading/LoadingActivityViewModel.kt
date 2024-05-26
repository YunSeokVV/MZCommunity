package ui.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import data.model.DailyBoard
import data.model.LoginedUser
import domain.loading.LoadingUsecase
import javax.inject.Inject

@HiltViewModel
class LoadingActivityViewModel @Inject constructor(
    private val loadingUsecase: LoadingUsecase
) : ViewModel() {
    init {
        getDailyBoards()
        getUserProfile()
    }

    // 사용자에 대한 정보와 일상 게시글 데이터를 전부 불러오는것을 완료했을때 반응을 감지
    private val _loadComplete = MutableLiveData<Boolean>(false)
    private val _dailyBoards = MutableLiveData<List<DailyBoard>>()
    val dailyBoards : LiveData<List<DailyBoard>>get() {
        return _dailyBoards
    }
    private val _logined_userInfo = MutableLiveData<LoginedUser>()
    val logined_userInfo : LiveData<LoginedUser>get() {
        return _logined_userInfo
    }

    val loadComplete : LiveData<Boolean> get() = _loadComplete
    private fun getUserProfile() = viewModelScope.launch {
        loadingUsecase.getUserProfile().collect { user ->
            _logined_userInfo.value = user
            checkLoadedDataComplete()
        }
    }

    private fun getDailyBoards() = viewModelScope.launch {
        loadingUsecase.getDailyBoards().collect {
            _dailyBoards.value = it
            checkLoadedDataComplete()
        }
    }

    private fun checkLoadedDataComplete(){
        if(_dailyBoards.value != null && _logined_userInfo.value != null){
            _loadComplete.value = true
        }
    }

}