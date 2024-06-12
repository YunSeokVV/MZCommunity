package domain.versus

import javax.inject.Inject
import kotlin.math.round

class CalculatePercentUsecase @Inject constructor(){
    fun calculatePercent(firstOpinion: Int, secondOpinion: Int) : Int = round((firstOpinion.toDouble() / (firstOpinion + secondOpinion) * 100)).toInt()
}