package id.airmas.airmas

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_first_login_profile.*
import kotlinx.android.synthetic.main.activity_first_login_profile.view.*


class FirstLoginProfile : AppCompatActivity() {
//    lateinit var etFirstLoginName : EditText
//    lateinit var etFirstLoginNickName : EditText
//    lateinit var etFirstLoginAlamat : EditText
//    lateinit var etFirstLoginName : EditText
    private lateinit var uid : String
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login_profile)
        et_name_firstlogin.text.toString()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            uid = user.uid
        }

        db = FirebaseFirestore.getInstance()


    }

    private fun setDataUser(){
        val name : String = et_name_firstlogin.text.toString()
        val nickname : String = et_nama_panggilan_firstlogin.text.toString()
        val alamat : String = et_alamat_firstlogin.text.toString()
        val patokan : String = et_patokan_firstlogin.text.toString()
        val email : String = et_email_firstlogin.text.toString()
        val lol : HashMap<String, Any> = hashMapOf()
        lol.put("name", name)
        lol.put("nickname", nickname)
        lol.put("alamat", alamat)
        lol.put("patokan", patokan)
        lol.put("email", email)


        if(db.collection("user").document(uid) == null){

            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }else{
            val context : Context
            db.collection("user").document(uid)
                .set(lol)
                .addOnSuccessListener {
                   Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Data gagal ditambahkan", Toast.LENGTH_LONG).show()
                }
        }

    }
}
