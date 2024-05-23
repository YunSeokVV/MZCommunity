package domain.dailyboard

import data.model.Response
import model.DailyBoard
import model.File

interface DailyBoardRepository {
    suspend fun postBoard(
        contents: String,
        uploadFileUri: List<File>,
        viewType: Int
    ): Response<Boolean>

    suspend fun getRandomDailyBoards(): List<DailyBoard>

    suspend fun getDailyBoard(documentId: String): DailyBoard

    suspend fun increaseFavourability(dailyBoard: DailyBoard, isLike: Boolean): Response<Boolean>
}