package util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.logger.Logger
import view.activity.MainActivity

class FirebaseAuth {
    companion object{
        val auth = Firebase.auth
        fun signUpWithEmail(email : String, password : String, context : Context){
            FirebaseAuth.auth.createUserWithEmailAndPassword(
                email,
                password

            // tyeeCasting 하면 에러 발생!
            ).addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Util.makeToastMessage("회원가입을 축하합니다! 저희앱을 사용해주셔서 감사합니다 :)", context)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Util.makeToastMessage("6자 이상의 문자 형태로 비밀번호를 입력해주세요 :)", context)
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Util.makeToastMessage("서버 오류입니다. 관리자에게 문의주세요 :)", context)
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Util.makeToastMessage("중복되는 사용자가 있습니다. 다른 계정을 사용해주세요 :)", context)
                    } catch (e: Exception) {
                        Logger.v("알수없는 오류입니다. 관리자에게 문의주세요 :)" + e.message.toString())
                    }
                }
            }
        }

    }
}