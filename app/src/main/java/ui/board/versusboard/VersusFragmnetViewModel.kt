package ui.board.versusboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import data.model.Response
import kotlinx.coroutines.launch
import data.model.VersusBoard
import domain.versus.CalculatePercentUsecase
import domain.versus.GetRandomVersusBoardUsecase
import domain.versus.VoteOpinionUsecase
import javax.inject.Inject


@HiltViewModel
class VersusFragmnetViewModel @Inject constructor(
    private val getRandomVersusBoardUsecase: GetRandomVersusBoardUsecase,
    private val voteOpinionUsecase: VoteOpinionUsecase,
    private val calculatePercentUsecase: CalculatePercentUsecase
) :
    ViewModel() {
    private var _boardUID = String()
    val boardUID: String
        get() {
            return _boardUID
        }

    private val _errorValue = MutableLiveData<Exception?>()
    val errorValue: LiveData<Exception?>
        get() {
            return _errorValue
        }

    private val _versusBoard = MutableLiveData<VersusBoard>()

    val versusBoard: LiveData<VersusBoard>
        get() {
            return _versusBoard
        }

    private var _voteComplte = MutableLiveData<Boolean>()
    val voteComplte: LiveData<Boolean>
        get() {
            return _voteComplte
        }

    init {
        getRandomVersusBoard()
    }


    fun getRandomVersusBoard() =
        viewModelScope.launch {
            getRandomVersusBoardUsecase().collect {
                when (it) {
                    is Response.Success -> {
                        val value = it.data
                        _versusBoard.value = value
                        _boardUID = value.boardUID
                    }

                    is Response.Failure -> {
                        Logger.v(it.e?.message.toString())
                        val value = it.e
                        _errorValue.value = value
                    }
                }
            }
        }

    fun addVoteCount(original: Int): Int {
        return original + 1
    }

    fun voteOpinion(opinion1Vote: Boolean) = viewModelScope.launch {
        voteOpinionUsecase(opinion1Vote, _boardUID).collect {
            Logger.v(it.toString())
            when (it) {
                is Response.Success -> {
                    _voteComplte.value = it.data ?: false
                }

                is Response.Failure -> {
                    Logger.v(it.e?.message.toString())
                }
            }
        }
    }

    fun calculatePercent(firstOpinion: Int, secondOpinion: Int): Int =
        calculatePercentUsecase.calculatePercent(firstOpinion, secondOpinion)

}