package util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mzcommunity.R
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Random
import kotlin.math.round

class Util {
    companion object {
        fun makeToastMessage(contents: String, context: Context) {
            Toast.makeText(context, contents, Toast.LENGTH_SHORT).show()
        }

        fun getUnknownProfileImage(context : Context): String {
            val result =
                "android.resource://" + context.packageName + "/" + R.drawable.user_profile2
            return result
        }

        fun getUnknownUserNickname(context: Context) : String {
            val result =
                "android.resource://" + context.packageName + "/" + R.string.unknown_user
            return result
        }

        fun parsingDailyBoardFiles(documentSnapshot: DocumentSnapshot, key: String): List<String> =
            documentSnapshot.get(key) as? List<String> ?: listOf()
    }
}