package id.airmas.airmas

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.firebase.ui.auth.ui.phone.PhoneVerification
import com.google.firebase.auth.FirebaseAuth

class PhoneLogin : AppCompatActivity() {
    private lateinit var edtNoTelepon : EditText
    private lateinit var btnLogin : Button
    lateinit var mAuth : FirebaseAuth
    lateinit var codeSent : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        btnLogin = findViewById(R.id.btn_login) as Button
        edtNoTelepon = findViewById(R.id.edt_nomor_ponsel) as EditText
        mAuth = FirebaseAuth.getInstance()
        btnLogin.setOnClickListener{

            val no : String = edtNoTelepon.text.toString()
            if(no.isEmpty()){
                edtNoTelepon.setError("Phone number is required")
                edtNoTelepon.requestFocus()

            }
            if(no.length < 10){
                edtNoTelepon.setError("Please enter a valid phone number")
                edtNoTelepon.requestFocus()

            }
            val i = Intent (this, PhoneVerify::class.java)
            i.putExtra("NoTelepon", no)
            startActivity(i)
        }
    }
}
