package com.example.ojekonline.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.ojekonline.R
import com.example.ojekonline.login.LoginActivity
import com.example.ojekonline.signup.Users
import com.example.ojekonline.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity


class ProfileFragment : Fragment() {

    var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_Uaser)
        val query = myRef.orderByChild("uid").equalTo(auth?.uid)

        //ini eksekusiny cmn bisa sekali
        query.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (issue in snapshot?.children){
                    val data = issue?.getValue(Users::class.java)
                    showProfile(data)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun showProfile(data: Users?) {

        profile_email.text = data?.email
        profile_name.text = data?.name
        profile_hp.text = data?.hp

        profile_sign_out.onClick {
            auth?.signOut()
            startActivity<LoginActivity>()
        }
    }

}
