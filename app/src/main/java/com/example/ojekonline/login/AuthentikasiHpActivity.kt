package com.example.ojekonline.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ojekonline.utama.MainActivity
import com.example.ojekonline.R
import com.example.ojekonline.utils.Constant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_authentikasi_hp.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

//TODO 13
class AuthentikasiHpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentikasi_hp)

        val key = intent.getStringExtra(Constant.Key)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_Uaser)

        //update realtime database
        authentikasiSubmit.onClick {
            if (authentikasiNomorHp.text.toString().isNotEmpty()) {
                myRef.child(key).child("hp")
                    .setValue(authentikasiNomorHp.text.toString())
                startActivity<MainActivity>()
            }
            else toast("Tidak Boleh Kosong!")
        }
    }
}
