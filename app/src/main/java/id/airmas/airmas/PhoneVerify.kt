package id.airmas.airmas

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneVerify : AppCompatActivity() {
    lateinit var mAuth : FirebaseAuth
    lateinit var mVerificationCode : String
    lateinit var mVerifikasi : EditText
    lateinit var mBtnKonfirmasi : Button
    lateinit var name : String
    lateinit var alamat : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)
        mVerifikasi = findViewById(R.id.edt_verifikasi) as EditText
        mBtnKonfirmasi = findViewById(R.id.btn_konfirmasi) as Button
        mAuth = FirebaseAuth.getInstance()

        intent = getIntent()
        val mobile : String = intent.getStringExtra("NoTelepon")
        sendVerificationCode(mobile)

        mBtnKonfirmasi.setOnClickListener {
            val code : String = mVerifikasi.text.toString()
            if(code.isEmpty()|| code.length < 6){
                mVerifikasi.setError("Enter valid code")
                mVerifikasi.requestFocus()
                return@setOnClickListener
            }
            verifyVerificationCode(code)
        }
    }

    private fun sendVerificationCode(mobile : String){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+62"+mobile,      // Phone number to verify
            60,               // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code : String = credential.smsCode.toString()
            if(code != null){
                mVerifikasi.setText(code)
                verifyVerificationCode(code)
            }


        }

        override fun onVerificationFailed(e: FirebaseException) {


          //  Toast.makeText(this!, e.message, Toast.LENGTH_LONG).show()

            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
            verificationId: String?,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            mVerificationCode = verificationId!!
            // resendToken = token

            // ...
        }
    }
    private fun verifyVerificationCode(code : String){
        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode, code)
       signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)


                    // ...
                } else {


                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }

    private fun getFromFirestore(){

    }
}
