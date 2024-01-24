package view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import util.FirebaseAuth
import util.NaverAuth
import util.Util

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Logger.addLogAdapter(AndroidLogAdapter())
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NaverAuth.initalizeNaverIDLogin(this)
        binding.signUpEmailBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.logInBtn.setOnClickListener {
            // 이메일 주소, 비밀번호 editText중 입력하지 않은게 있는 경우
            if (TextUtils.isEmpty(binding.emailInput.text.toString()) || TextUtils.isEmpty(binding.passWordInput.text.toString())) {
                Util.makeToastMessage("입력하지 않은 항목이 있습니다.", this)

            // 정상 로그인
            } else {
                FirebaseAuth.auth.signInWithEmailAndPassword(
                    binding.emailInput.text.toString(),
                    binding.passWordInput.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Util.makeToastMessage("사용자 정보를 확인해주세요 :)", this)
                        try {
                            throw task.exception!!
                        } catch (e: Exception) {
                            Logger.v(e.message.toString())
                        }
                    }
                }
            }
        }

        // 네이버 아이디로 로그인
        binding.signUpNaver.setOnClickListener {
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(response: NidProfileResponse) {
                            Logger.v(response.profile?.email.toString())

//                            // 메인화면으로 전환
//                            val intent = Intent(baseContext, MainActivity::class.java)
//                            startActivity(intent)
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                            val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                            Logger.v("errorCode: $errorCode, errorDesc: $errorDescription")
                        }

                        override fun onError(errorCode: Int, message: String) {
                            onFailure(errorCode, message)
                        }
                    })

                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Logger.v("errorCode:$errorCode, errorDesc:$errorDescription")
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }


            NaverAuth.signUpNaver(this, oauthLoginCallback)
        }

    }
}