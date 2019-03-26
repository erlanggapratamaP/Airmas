package id.airmas.airmas

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_phone_verify.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit





class PhoneVerify : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mVerificationCode : String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var mVerifikasi : EditText
    //lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mBtnKonfirmasi : Button
    private lateinit var name : String
    private lateinit var alamat : String
    private lateinit var db : FirebaseFirestore
    private lateinit var uid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)

        mVerifikasi = findViewById(R.id.edt_verifikasi) as EditText
        mBtnKonfirmasi = findViewById(R.id.btn_konfirmasi) as Button
        mAuth = FirebaseAuth.getInstance()
        //get UID user
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            uid = user.uid
        }
        //inisiasi firestore
        db = FirebaseFirestore.getInstance()

        //intent for get telepon



        //inisiasi countdown


        mBtnKonfirmasi.setOnClickListener {
            val code : String = mVerifikasi.text.toString()
            if(code.isEmpty()|| code.length < 6){
                mVerifikasi.setError("Enter valid code")
                mVerifikasi.requestFocus()
                return@setOnClickListener
            }
            verifyVerificationCode(code)
        }
        sendVerificationCode()
        //callbacks untuk verifikasi code

    }

    private fun sendVerificationCode(){
        intent = getIntent()
        val mobile : String = intent.getStringExtra("NoTelepon")

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+62"+mobile,      // Phone number to verify
            60,               // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this,             // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
    }
    private fun resendVerificationCode(token: PhoneAuthProvider.ForceResendingToken){
        intent = getIntent()
        val mobile : String = intent.getStringExtra("NoTelepon")
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mobile,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks,
            token)

    }
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code : String = credential.smsCode.toString()
            if(code != null){
                mVerifikasi.setText(code)
            }

            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {

            resendVerificationCode(resendToken)

            baseContext.toast(e.message.toString())


            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
          //  baseContext.toast("onCodeSent: " + verificationId)

            mVerificationCode = verificationId!!
            resendToken = token

            // ...
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String?) {
            super.onCodeAutoRetrievalTimeOut(p0)
        }
    }
    private fun verifyVerificationCode(code : String){
        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationCode!!, code)
       signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   getFromFirestore()


                    // ...
                } else {


                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }
    //get and checking for first user
    private fun getFromFirestore() {

        val docRef= db.collection("user").document(uid)
        docRef.get().addOnCompleteListener(object : OnCompleteListener<DocumentSnapshot>{
            override fun onComplete(task: Task<DocumentSnapshot>) {
                if (task.isSuccessful){
                    val document = task.result
                    if(document!!.exists()){
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        val intent = Intent(applicationContext, FirstLoginProfile::class.java)
                        startActivity(intent)
                    }
                }else {
                    Log.d("PhoneVerify", "get failed with ", task.getException());
                }
            }
        })
    }

}
