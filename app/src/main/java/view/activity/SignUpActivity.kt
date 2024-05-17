package view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import util.FirebaseAuth
import util.Util
import viewmodel.SignUpActivityViewModel

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignUpActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signUpEmailBtn.setOnClickListener {
            // 이메일 주소, 비밀번호, 비밀번호 확인 editText중 입력하지 않은게 있는 경우
            if (TextUtils.isEmpty(binding.emailInput.text.toString()) || TextUtils.isEmpty(binding.emailInput.text.toString())) {
                Util.makeToastMessage("입력하지 않은 항목이 있습니다.", this)
            } else if (binding.passWordInput.text.toString()
                    .equals(binding.passWordCheck.text.toString())
            ) {
                FirebaseAuth.auth.createUserWithEmailAndPassword(
                    binding.emailInput.text.toString(),
                    binding.passWordCheck.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch {
                            viewModel.setUserNickName(binding.emailInput.text.toString())
                            viewModel.saveUserLoginInfo(
                                binding.emailInput.text.toString(),
                                binding.passWordInput.text.toString()
                            )
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            Util.makeToastMessage("6자 이상의 문자 형태로 비밀번호를 입력해주세요 :)", this)
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Util.makeToastMessage("서버 오류입니다. 관리자에게 문의주세요 :)", this)
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Util.makeToastMessage("중복되는 사용자가 있습니다. 다른 계정을 사용해주세요 :)", this)
                        } catch (e: Exception) {
                            Logger.v("알수없는 오류입니다. 관리자에게 문의주세요 :)" + e.message.toString())
                        }
                    }
                }
            } else {
                Util.makeToastMessage("비밀번호를 다시한번 확인해주세요 :)", this)
            }
        }

        viewModel.saveUserInfo.observe(this, Observer { saved ->
            if (saved) {
                Util.makeToastMessage("회원가입을 축하합니다! 저희앱을 사용해주셔서 감사합니다 :)", this)
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
            }
        })

    }

}