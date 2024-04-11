package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import model.VersusBoard
import usecase.VersusUsecase
import javax.inject.Inject



@HiltViewModel
class VersusFragmnetViewModel @Inject constructor(private val versusUseCase : VersusUsecase) : ViewModel() {

    private val _versusBoard = MutableLiveData<VersusBoard>()

    private var _opinion1Percent : Int = 0
    val opinion1Percent : Int
        get() {
            return _opinion1Percent
        }

    private var _opinion2Percent : Int = 0
    val opinion2Percent : Int
        get() {
            return _opinion2Percent
        }

    val versusBoard : LiveData<VersusBoard>
        get() {return _versusBoard}

    init {
        getRandomVersusBoard()
    }


    fun getRandomVersusBoard() = viewModelScope.launch {
        versusUseCase.getRandomVersusBoard().collect{
            Logger.v(it.toString())
            _versusBoard.value = it
            getPercentage(true, it.opinion1Count.toInt(), it.opinion2Count.toInt())
        }
    }

    fun getPercentage(opinion1 : Boolean, opinion1Total : Int, opinion2Total : Int){
        var result : Int
        val totalNum = opinion1Total+opinion2Total

        if(totalNum==0){
            return
        }

        if(opinion1){
            result = opinion1Total/totalNum * 100
            _opinion1Percent = result
        } else{
            result = opinion2Total/totalNum * 100
            _opinion2Percent = result
        }
    }

    fun addVoteCount(original : Int) : Int{
        val result = original + 1
        return result
    }

}