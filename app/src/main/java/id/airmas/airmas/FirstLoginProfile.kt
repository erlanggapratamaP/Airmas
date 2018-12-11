package id.airmas.airmas

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.androidnetworking.AndroidNetworking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.rx2androidnetworking.Rx2AndroidNetworking
import id.airmas.airmas.model.Provinsi
import id.airmas.airmas.model.Kabupaten
import id.airmas.airmas.model.Kecamatan
import id.airmas.airmas.model.Kelurahan
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_first_login_profile.*
import meridianid.farizdotid.actdaerahindonesia.util.JsonParse
import org.jetbrains.anko.spinner
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class FirstLoginProfile : AppCompatActivity(){


    private var listItemsProvinsi  = arrayListOf<Provinsi>()
    private var listItemsKabupaten = arrayListOf<Kabupaten>()
    private var listItemsKecamatan = arrayListOf<Kecamatan>()
    private var listItemsKelurahan = arrayListOf<Kelurahan>()
    private lateinit var preferences: SharedPreferences
    private lateinit var uid : String
    private lateinit var db : FirebaseFirestore
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login_profile)
        //inisiasi rxjava dan fan dan juga insialisasi getDdata
        getProvinsi()
        deletProvinsi()
        AndroidNetworking.initialize(applicationContext)
        et_name_firstlogin.text.toString()
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            uid = user.uid
        }
        btn_konfirmasi_firstlogin.setOnClickListener{
            setDataUser()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
        db = FirebaseFirestore.getInstance()

        //insialisasi spinner

      //  val provinsiList = listItems.map { it.nama }.toString()


//

    }



     fun setDataUser(){
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

        //GET PREFERENCES DARI DATA YANG TELAH DIKIRIMKAN DARI MASING-MASING METHOD GET DAERAH
        val preferences : SharedPreferences = getSharedPreferences("MainPref", Context.MODE_PRIVATE)

         val prov: String? = preferences.getString("dataprovinsi", "Jawa Barat")
         val kab: String? = preferences.getString("datakabupaten", "")
         val kec: String? = preferences.getString("datakecamatan", "")
         val kel: String? = preferences.getString("datakelurahan", "")
         lol.put("provinsi", prov!!)
         lol.put("kabupaten/kota", kab!!)
         lol.put("kecamatan", kec!!)
         lol.put("kelurahan/desa", kel!!)

        db.collection("user").document(uid)
            .set(lol)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data gagal ditambahkan", Toast.LENGTH_LONG).show()
            }
    }
    //METHOD GET PROVINSI
     fun getProvinsi(){
        Rx2AndroidNetworking.get("http://dev.farizdotid.com/api/daerahindonesia/provinsi")
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {
                   try {
                       val jsonObject  = JSONObject(t)
                       val array : JSONArray = jsonObject.getJSONArray("semuaprovinsi")
                       for(item in 0 until array.length()){
                           val f = array.getJSONObject(item)
                           val id = f.getString("id").toInt()
                           val pr  = Provinsi(
                               id,
                               f.getString("nama")
                           )

                           listItemsProvinsi.add(pr)
                       }
                   }catch (e: JSONException){
                       e.printStackTrace()
                   }
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
//                    baseContext.toast(listItems[4].nama!!)
                    val arrayProvinsi = arrayOfNulls<String?>(listItemsProvinsi.size)
                    for(i in 0 .. listItemsProvinsi.size-1){
                        arrayProvinsi[i] = listItemsProvinsi[i].nama!!

                    }
                    val itemAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, arrayProvinsi)
                    itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sp_provinsi.setAdapter(itemAdapter)
                    sp_provinsi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val item:String = parent!!.getItemAtPosition(position).toString()
                            val provinsi : Provinsi = listItemsProvinsi.get(position)
                            val aidiProvinsi = listItemsProvinsi.get(position).id.toString()
                            getKota(aidiProvinsi)
                            deleteKota()

                            //MEMBAWA DATA KEDALAM PREFERECES
                            preferences = getSharedPreferences("MainPref", Context.MODE_PRIVATE);
                            val editor: SharedPreferences.Editor = preferences.edit()
                            editor.putString("dataprovinsi", item)
                        }
                    }


//                    baseContext.toast(array[2]!!)

                }
            })
        
         
    }

    //METHOD GET KOTAA
     fun getKota(aidiProvinsi: String){
        Rx2AndroidNetworking.get("http://dev.farizdotid.com/api/daerahindonesia/provinsi/{id}/kabupaten")
            .addPathParameter("id", aidiProvinsi)
            .build()
            .stringObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {
                    try {
                        val jsonObject  = JSONObject(t)
                        val array : JSONArray = jsonObject.getJSONArray("daftar_kecamatan")
                        for(item in 0 until array.length()){
                            val f = array.getJSONObject(item)
                            val id = f.getString("id").toInt()
                            val kb  = Kabupaten(
                                id,
                                f.getString("nama")
                            )

                            listItemsKabupaten.add(kb)
                        }
                    }catch (e: JSONException){
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {
                    val arrayKabupaten = arrayOfNulls<String?>(listItemsKabupaten.size)
                    for(i in 0 .. listItemsKabupaten.size-1){
                        arrayKabupaten[i] = listItemsKabupaten[i].nama!!

                    }
                    val itemAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, arrayKabupaten)
                    itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sp_kabupaten.setAdapter(itemAdapter)
                    sp_kabupaten.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val item:String = parent!!.getItemAtPosition(position).toString()
                            val kabupaten : Kabupaten = listItemsKabupaten.get(position)
                            val aidiKabupaten = listItemsKabupaten.get(position).id.toString()
                            getKecamatan(aidiKabupaten)
                            deleteKecamatan()
                            //MEMBAWA DATA KEDALAM PREFERENCES
                            preferences = getSharedPreferences("MainPref", Context.MODE_PRIVATE);
                            val editor: SharedPreferences.Editor = preferences.edit()
                            editor.putString("datakabupaten", item)

                        }
                    }

                }
            })
    }
    //METHOD GET KECAMATAN
     fun getKecamatan(aidiKabupaten: String){
         Rx2AndroidNetworking.get("http://dev.farizdotid.com/api/daerahindonesia/provinsi/kabupaten/{id}/kecamatan")
             .addPathParameter("id", aidiKabupaten)
             .build()
             .stringObservable
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : Observer<String>{
                 override fun onSubscribe(d: Disposable) {

                 }

                 override fun onNext(t: String) {
                     try {
                         val jsonObject  = JSONObject(t)
                         val array : JSONArray = jsonObject.getJSONArray("daftar_kecamatan")
                         for(item in 0 until array.length()){
                             val f = array.getJSONObject(item)
                             val id = f.getString("id").toInt()
                             val kc  = Kecamatan(
                                 id,
                                 f.getString("nama")
                             )

                             listItemsKecamatan.add(kc)
                         }
                     }catch (e: JSONException){
                         e.printStackTrace()
                     }
                 }

                 override fun onError(e: Throwable) {

                 }

                 override fun onComplete() {
                     val arrayKecamatan = arrayOfNulls<String?>(listItemsKecamatan.size)
                     for(i in 0 .. listItemsKecamatan.size-1){
                         arrayKecamatan[i] = listItemsKecamatan[i].nama!!

                     }
                     val itemAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, arrayKecamatan)
                     itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                     sp_kecamatan.setAdapter(itemAdapter)
                     sp_kecamatan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                         override fun onNothingSelected(parent: AdapterView<*>?) {

                         }

                         override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                             val item:String = parent!!.getItemAtPosition(position).toString()
                             val kecamatan : Kecamatan = listItemsKecamatan.get(position)
                             val aidiKecamatan = listItemsKecamatan.get(position).id.toString()
                             getKelurahan(aidiKecamatan)
                             deleteKelurahan()
                             //MEMBAWA DATA KEDALAM PREFERENCES
                             preferences = getSharedPreferences("MainPref", Context.MODE_PRIVATE);
                             val editor: SharedPreferences.Editor = preferences.edit()
                             editor.putString("datakecamatan", item)



                         }
                     }

                 }
             })

    }

    //METHOD GET KELURAHAN
     fun getKelurahan(aidiKecamatan: String){
         Rx2AndroidNetworking.get("http://dev.farizdotid.com/api/daerahindonesia/provinsi/kabupaten/kecamatan/{id}/desa")
             .addPathParameter("id", aidiKecamatan)
             .build()
             .stringObservable
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : Observer<String>{
                 override fun onSubscribe(d: Disposable) {

                 }

                 override fun onNext(t: String) {
                     try {
                         val jsonObject  = JSONObject(t)
                         val array : JSONArray = jsonObject.getJSONArray("daftar_desa")
                         for(item in 0 until array.length()){
                             val f = array.getJSONObject(item)
                             val id = f.getString("id").toLong()
                             val kl  = Kelurahan(
                                 id,
                                 f.getString("nama")
                             )

                             listItemsKelurahan.add(kl)
                         }
                     }catch (e: JSONException){
                         e.printStackTrace()
                     }
                 }

                 override fun onError(e: Throwable) {

                 }

                 override fun onComplete() {
                     val arrayKelurahan = arrayOfNulls<String?>(listItemsKelurahan.size)
                     for(i in 0 .. listItemsKelurahan.size-1){
                         arrayKelurahan[i] = listItemsKelurahan[i].nama!!

                     }
                     val itemAdapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, arrayKelurahan)
                     itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                     sp_keluarahan.setAdapter(itemAdapter)
                     sp_keluarahan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                         override fun onNothingSelected(parent: AdapterView<*>?) {

                         }

                         override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                             val item:String = parent!!.getItemAtPosition(position).toString()
                             val kelurahan : Kelurahan = listItemsKelurahan.get(position)
                             val aidiKecamatan = listItemsKelurahan.get(position).id.toString()
                             //MEMBAWA DATA KEDALAM PREFERENCES
                             preferences = getSharedPreferences("MainPref", Context.MODE_PRIVATE);
                             val editor: SharedPreferences.Editor = preferences.edit()
                             editor.putString("datakelurahan", item)


                         }
                     }

                 }
             })

    }
    private fun deletProvinsi(){
        for(i in listItemsProvinsi.size - 1 downTo 0){
            listItemsProvinsi.remove(listItemsProvinsi.get(i))
        }
    }
    private fun deleteKota(){
        for (i in listItemsKabupaten.size - 1 downTo 0) {
            listItemsKabupaten.remove(listItemsKabupaten.get(i))
        }
    }
    private fun deleteKecamatan(){
        for(i in listItemsKecamatan.size - 1 downTo 0){
            listItemsKecamatan.remove(listItemsKecamatan.get(i))
        }
    }
    private fun deleteKelurahan(){
        for (i in listItemsKelurahan.size - 1 downTo 0){
            listItemsKelurahan.remove(listItemsKelurahan.get(i))
        }
    }

}

