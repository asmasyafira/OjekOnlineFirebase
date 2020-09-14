package com.example.ojekonline.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.ojekonline.R
import com.example.ojekonline.adapter.HistoryAdapter
import com.example.ojekonline.utils.Booking
import com.example.ojekonline.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_history.*
import java.lang.IllegalStateException

class HistoryFragment : Fragment() {

    private var auth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        auth?.uid?.let { bookingHistoryUser(it) }
    }

    //TODO 38
    //mengambil data dr firebasenya
    private fun bookingHistoryUser(uid: String) {

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)

        val data = ArrayList<Booking>()
        val query = myRef.orderByChild("uid").equalTo(uid)

        query.addValueEventListener(object :ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (issue in snapshot.children){
                    val dataFirebase = issue.getValue(Booking::class.java)
                    val booking = Booking()
                    booking.tanggal = dataFirebase?.tanggal
                    booking.uid = dataFirebase?.uid
                    booking.lokasiAwal = dataFirebase?.lokasiAwal
                    booking.latAwal = dataFirebase?.latAwal
                    booking.lonAwal = dataFirebase?.lonAwal
                    booking.latTujuan = dataFirebase?.latTujuan
                    booking.lonTujuan = dataFirebase?.lonTujuan
                    booking.lokasiTujuan = dataFirebase?.lokasiTujuan
                    booking.jarak = dataFirebase?.jarak
                    booking.harga = dataFirebase?.harga
                    booking.status = dataFirebase?.status

                    data.add(booking)
                    showdata(data)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //TODO 40
    //data yang diambil dr firebase realtime database
    //dipindahkan ke adapter dan adapter di set ke recyclerview
    private fun showdata(data: ArrayList<Booking>){
        if (data != null){
            try {
                rv1.adapter = HistoryAdapter(data)
                rv1.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager
            } catch (e : IllegalStateException){

            }
        }
    }

}
