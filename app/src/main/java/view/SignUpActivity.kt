package view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivitySignUpBinding
import com.orhanobut.logger.Logger
import util.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.getVerificationCode.setOnClickListener {
            if (binding.passWordInput.text.toString()
                    .equals(binding.passWordCheck.text.toString())
            ) {
                FirebaseAuth.auth.createUserWithEmailAndPassword(
                    binding.emailInput.toString(),
                    binding.passWordCheck.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Logger.v(FirebaseAuth.currentUser?.isEmailVerified.toString())

                    } else {
                        Logger.v("task failed " + task.exception)
                    }
                }

            } else {

            }
        }

    }
}