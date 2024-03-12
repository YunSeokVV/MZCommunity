package util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.mzcommunity.R
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.orhanobut.logger.Logger
import view.SignUpActivity

class NaverAuth {

    companion object {
//        val oauthLoginCallback = object : OAuthLoginCallback {
//            override fun onSuccess() {
//                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
//                    override fun onSuccess(response: NidProfileResponse) {
//                        Logger.v(response.profile?.email.toString())
//
//                        // FireBase에 회원정보 저장
//                        // 메인화면으로 전환
//
//                    }
//
//                    override fun onFailure(httpStatus: Int, message: String) {
//                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
//                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
//                        Logger.v("errorCode: $errorCode, errorDesc: $errorDescription")
//                    }
//
//                    override fun onError(errorCode: Int, message: String) {
//                        onFailure(errorCode, message)
//                    }
//                })
//
//            }
//
//            override fun onFailure(httpStatus: Int, message: String) {
//                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
//                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
//                Logger.v("errorCode:$errorCode, errorDesc:$errorDescription")
//            }
//
//            override fun onError(errorCode: Int, message: String) {
//                onFailure(errorCode, message)
//            }
//        }

        fun initalizeNaverIDLogin(context: Context) {
            NaverIdLoginSDK.initialize(
                context,
                "Cv4nqmW4lWGPvtoq78dt",
                "X0MpJYFKGK",
                context.getString(R.string.app_name)
            )
        }

        fun signUpNaver(context: Context, oauthLoginCallback: OAuthLoginCallback) {
            NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
        }

    }

}