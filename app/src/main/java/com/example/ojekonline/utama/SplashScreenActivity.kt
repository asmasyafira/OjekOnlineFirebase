package com.example.ojekonline.utama

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.ojekonline.R
import com.example.ojekonline.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()
        Handler().postDelayed(Runnable {
            if (auth?.currentUser?.displayName != null){
                startActivity<MainActivity>()
            }
            else startActivity<LoginActivity>()
        },3000)
    }
}
