package view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityLoginBinding
import com.kakao.sdk.user.UserApiClient
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

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        binding.logInBtn.setOnClickListener {
            Logger.v("Check here")
            // 이메일 주소, 비밀번호 editText중 입력하지 않은게 있는 경우
            if (TextUtils.isEmpty(binding.emailInput.text.toString()) || TextUtils.isEmpty(
                    binding.passWordInput.text.toString()
                )
            ) {
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

        binding.signUpEmailBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        NaverAuth.initalizeNaverIDLogin(this)
        // 네이버 아이디로 로그인
        binding.signUpNaver.setOnClickListener {
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    NidOAuthLogin().callProfileApi(object :
                        NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(response: NidProfileResponse) {
                            Logger.v(response.profile?.email.toString())

                            // 메인화면으로 전환
                            val intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
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

        binding.signUpKakao.setOnClickListener {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Logger.v("login error " + error.toString())
                } else if (token != null) {
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Logger.v("user information request Failed!")
                        } else if (user != null) {
                            var scopes = mutableListOf<String>()

                            // 사용자가 동의한 항목에 대한 정보를 사용할 수 있다.
//                        if (user.kakaoAccount?.emailNeedsAgreement == true) {
//
//                        } else if(user.kakaoAccount?.profileNeedsAgreement == true) {
//
//                        }

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                        }
                    }
                }
            }

        }


    }
}