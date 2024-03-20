package repository

import android.provider.DocumentsContract
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.orhanobut.logger.Logger
import model.DailyboardCollection
import util.Util
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.typeOf


interface GetDailyBoardRepositry {
    //suspend fun getDailyBoard(): List<DailyPosting>
    suspend fun getDailyBoard(): String
}

@Singleton
class GetDailyBoardRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val fireStoreRef: FirebaseFirestore
) :
    GetDailyBoardRepositry {
    override suspend fun getDailyBoard(): String {
        fireStoreRef.collection("dailyBoard").get().addOnSuccessListener { result ->
            for (document in result) {
                Logger.v(document.toString())
                //val tmp : String = document.get("boardContents") as? String ?: "nothing"

                val dailyBoard = DailyboardCollection(Util.parsingFireStoreDocument(document, "boardContents"), Util.parsingFireStoreDocument(document, "disLike").toInt(), Util.parsingFireStoreDocument(document, "like").toInt(), Util.parsingFireStoreDocument(document, "writerUID"))

                Logger.v("dailyBoard.writerUID "+dailyBoard.writerUID)

                //todo : 사용자 닉네임 firestore에 추가하는 로직 구현하고 다시 여기와서 구현하기

                fireStoreRef.collection("MZUsers").document(dailyBoard.writerUID).get().addOnSuccessListener {result ->
                    //Logger.v(result.get(""))
                }

            }

        }
            .addOnFailureListener {

            }
        return "test"
    }

}