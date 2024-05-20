package util

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuth {
    companion object{
        val auth = Firebase.auth
    }
}