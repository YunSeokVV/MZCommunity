package viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mzcommunity.R
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.DailyBoard
import model.LoginedUser
import usecase.LoadingUsecase
import javax.inject.Inject

@HiltViewModel
class LoadingActivityViewModel @Inject constructor(
    private val application: Application,
    private val loadingUsecase: LoadingUsecase
) : AndroidViewModel(application) {
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


    fun getWriterNicknames() : ArrayList<String> {
        val result =  _dailyBoards.value?.map{ dailyBoards ->
            dailyBoards.writerNickname
        }
        return ArrayList(result)
    }

    // 인텐트로 uri 값을 넘길 수 없기 때문에 list안의 모든 uri 값을 String으로 만든뒤 넘겨주는 메소드
    fun writerProfileUriStrs() : ArrayList<String> {
        val result = _dailyBoards.value?.map{ dailyBoards ->
            dailyBoards.writerProfileUri.toString()
        }


        return ArrayList(result)!!
    }

    fun getStrFileLists() : List<List<String>>{
        val result = _dailyBoards.value?.map {dailyBoards ->
            dailyBoards.files.map {uri ->
                uri.toString()
            }
        }

        return result!!
    }
    fun getUserProfile() = viewModelScope.launch {
        loadingUsecase.getUserProfile(application.applicationContext).collect { user ->
            _logined_userInfo.value = user
            checkLoadedDataComplete()
        }
    }

    fun getDailyBoards() = viewModelScope.launch {
        loadingUsecase.getDailyBoards().collect {
            _dailyBoards.value = it
            checkLoadedDataComplete()
        }
    }

    fun checkLoadedDataComplete(){
        if(_dailyBoards.value != null && _logined_userInfo.value != null){
            _loadComplete.value = true
        }
    }

}