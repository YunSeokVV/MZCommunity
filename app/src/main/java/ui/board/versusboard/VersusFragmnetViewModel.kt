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
import domain.versus.VersusUsecase
import javax.inject.Inject


@HiltViewModel
class VersusFragmnetViewModel @Inject constructor(private val versusUseCase: VersusUsecase) :
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
            versusUseCase.getRandomVersusBoard().collect {
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
        versusUseCase.voteOpinion(opinion1Vote, _boardUID).collect {
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

    fun calculatePercent(firstOpinion: Int, secondOpinion: Int): Int = versusUseCase.calculatePercent(firstOpinion, secondOpinion)

}