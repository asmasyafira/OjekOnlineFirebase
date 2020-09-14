package com.example.ojekonline.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ojekonline.utama.MainActivity
import com.example.ojekonline.R
import com.example.ojekonline.signup.SignUpActivity
import com.example.ojekonline.signup.Users
import com.example.ojekonline.utils.Constant
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var googleSignInClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //TODO 12
        auth = FirebaseAuth.getInstance()

        signUpButtonGmail.onClick {
            signIn()
        }

        signUpLink.onClick {
            startActivity<SignUpActivity>()
        }

        loginButton.onClick {
            if (loginName.text.isNotEmpty() &&
                loginPassword.text.isNotEmpty()
            ) {
                authUserSignIn(
                    loginName.text.toString(),
                    loginPassword.text.toString()
                )
            }
        }

    }

    //TODO 6
    //auth sign in yg biasa
    private fun authUserSignIn(email: String, pass: String) {
        var status: Boolean? = null

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener {
             task ->
                if (task.isSuccessful){
                    startActivity<MainActivity>()
                    finish()
                }else{
                    toast("Login Failed")
                }
            }
    }

    //TODO 7
    //request sign in gmail
    private fun signIn() {
        val gson = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gson)

        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 4)
    }

    //TODO 8
    //hasil request sign in google
    /* setelah user memilih account yg sudah ter signin akan
    mengambil informasi dari user
    yg signin google menggunakan onActivityResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 4) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }
        }
    }

    //TODO 9
    //untuk sign in firebase auth firebase
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        var uid = String()
        val credential1 = GoogleAuthProvider.getCredential(acct?.idToken, null)

        auth?.signInWithCredential(credential1)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth?.currentUser
                checkDatabase(task.result?.user?.uid, acct)
                uid = user?.uid.toString()
            } else {

            }
        }
    }

    //TODO 10
    //cek database
    // apakah user yg signin udh ada di realtime database / blm
    private fun checkDatabase(uid: String?, acct: GoogleSignInAccount?) {
        val database = FirebaseDatabase.getInstance()

        val myRef = database.getReference(Constant.tb_Uaser)
        val query = myRef.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            //klo usernya udh ada, bakal msk ke mainact
            //klo blm ada, data akan masuk ke database firebase
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    startActivity<MainActivity>()
                } else {
                    acct?.displayName?.let {
                        acct.email?.let { it1 ->
                            insertUser(it, it1, "08", uid)
                        }
                    }
                }
            }

        })
    }

    //TODO 11
    /*proses ini hampir sama dengan signup, klo proses ini berhasil
    akan pindah ke authentikasi activity untuk memasukan dari nomer telepon user.
    'Any' nya ganti Boolean*/
    private fun insertUser(name: String, email: String, hp: String, idUser: String?): Boolean {
        val user = Users()
        user.email = email
        user.name = name
        user.uid = auth?.uid

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myRef = database.getReference(Constant.tb_Uaser)

        myRef.child(key?: "").setValue(user)
        //setValue = menyimpan ke databse

        startActivity<AuthentikasiHpActivity>(Constant.Key to key)

        return true
    }
}
