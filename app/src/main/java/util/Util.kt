package util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mzcommunity.R
import com.google.firebase.firestore.DocumentSnapshot
import view.ProgressDialog
import java.util.Random
import kotlin.math.round

class Util {

    companion object {
        private var progressDialog: ProgressDialog? = null

        // drawable 자원에 접근하기 위한 context
        private var context : Context? = null

        fun makeToastMessage(contents: String, context: Context) {
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show()
        }

        fun setResourceContext(context: Context){
            if(this.context == null)
                this.context = context
        }

        fun getStringResource(resourceId: Int) : String{
            val result = "android.resource://" + this.context?.applicationContext?.packageName + "/" + resourceId
            return result
        }

        fun getResourceImage(resourceId: Int) : String{
            val result = "android.resource://" + this.context?.applicationContext?.packageName + "/" + resourceId
            return result
        }

        fun getUnknownProfileImage() : String{
            val result = "android.resource://" + this.context?.applicationContext?.packageName + "/" + R.drawable.user_profile2
            return result
        }

        fun getResourceColor(context: Context, resourceId: Int): Int {
            return ContextCompat.getColor(context, resourceId)
        }

        fun cacluatePercent(firstOpinionNumber: Int, secondOpinionNumber: Int): Int {
            return round((firstOpinionNumber.toDouble() / (firstOpinionNumber + secondOpinionNumber) * 100)).toInt()
        }

        fun parsingFireStoreDocument(documentSnapshot: DocumentSnapshot, key: String): String {
            var result: String
            if (key == "disLike") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key == "like") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else if (key == "writerUID") {
                result = documentSnapshot.get(key) as? String ?: "noWriterUID"
            } else if (key == "boardContents") {
                result = documentSnapshot.get(key) as? String ?: "noBoardContents"
            } else if (key == "userFavourability") {
                val userFavour = documentSnapshot.get(key) as? Map<String, Any>
                result = (userFavour?.get(FirebaseAuth.auth.uid.toString()) ?: "usual").toString()
            } else if (key == "viewType") {
                result = (documentSnapshot.get(key) as? Long ?: 0).toString()
            } else {
                result = documentSnapshot.get(key) as? String ?: getStringResource(R.string.nothing)
            }
            return result
        }

        fun parsingDailyBoardFiles(documentSnapshot: DocumentSnapshot, key: String): List<String> =
            documentSnapshot.get(key) as? List<String> ?: listOf()

        fun removeStr(original: String, deleteStr: String): String {
            var result = original
            result = result.substring(deleteStr.length)
            return result
        }

        fun showProgressDialog(context: Context, showDialog: Boolean) {

            progressDialog = progressDialog ?: ProgressDialog(context)

            if (showDialog) {
                progressDialog?.show()
            } else {
                progressDialog?.dismiss()
                progressDialog = null
            }
        }

        fun getRanNum(ranNum: Int): Int {
            // 0~ranNum 사이의 랜덤 숫자
            val randomNum = Random().nextInt(ranNum)
            return randomNum
        }

    }
}