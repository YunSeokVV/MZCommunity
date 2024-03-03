package view

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import util.FirebaseAuth
import util.Util
import viewModel.LoginActivityViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginActivityViewModel : LoginActivityViewModel by viewModels()

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                loginActivityViewModel.signInWithGoogle(task)
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

        binding.signUpKakao.setOnClickListener {


        }


    }

    private fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .build()

        val mGoogleSignInClient = let { GoogleSignIn.getClient(it, gso) }
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }


//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            val photoUrl = account.photoUrl.toString()
//            Logger.v(photoUrl)
//
//            firebaseAuthWithGoogle(account)
//        } catch (e: ApiException) {
//            Logger.v(e.message.toString())
//            Logger.v(e.statusCode.toString())
//
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        val mAuth = FirebaseAuth.auth
//        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//
//                } else {
//
//                    Logger.v(task.exception.toString())
//                }
//
//            }
//
//    }

}