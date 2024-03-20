package view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import util.FirebaseAuth
import util.Util
import viewModel.LoginActivityViewModel
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginActivityViewModel: LoginActivityViewModel by viewModels()


    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                lifecycleScope.launch {
                    loginActivityViewModel.signInWithGoogle(task)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Logger.addLogAdapter(AndroidLogAdapter())
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val textView: View? = binding.signUpGoogle.getChildAt(0)
        if (textView is TextView)
            textView.setText(getString(R.string.start_with_google));


        binding.logInBtn.setOnClickListener {
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

        // 구글 아이디로 로그인
        binding.signUpGoogle.setOnClickListener {
            googleSignIn()

        }

        loginActivityViewModel.isGoogleLogin.observe(this, Observer { data ->
            if (data) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        })


    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = let { GoogleSignIn.getClient(it, gso) }
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

}