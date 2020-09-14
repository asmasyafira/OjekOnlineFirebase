package com.example.ojekonline.utama

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ojekonline.R
import com.example.ojekonline.model.Driver
import com.example.ojekonline.utils.Booking
import com.example.ojekonline.utils.Constant
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_waiting_driver.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class WaitingDriverActivity : AppCompatActivity(), OnMapReadyCallback {

    var database: FirebaseDatabase? = null
    var key: String? = null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_driver)

        key = intent.getStringExtra(Constant.Key)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
        as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        homeBottomHome.onClick {
            startActivity<MainActivity>()
        }

        database = FirebaseDatabase.getInstance()

        val myRef = database?.getReference(Constant.tb_booking)

        myRef?.child(key ?: "")?.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                showData(booking)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //mengambil koordinat driver
    @SuppressLint("SetTextI18n")
    private fun showData(booking : Booking?){

        homeAwal.text = booking?.lokasiAwal
        homeTujuan.text = booking?.lokasiTujuan
        homePrice.text = booking?.harga + "(" + booking?.jarak + ")"

        val myRef = database?.getReference("Driver")
        val query = myRef?.orderByChild("uid")?.equalTo(booking?.driver)
        query?.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children){
                    val driver = Driver()
                    val d = issue.getValue(Driver::class.java)

                    driver.latitude = d?.latitude
                    driver.longitude = d?.longitude
                    homeNameDriver.text = d?.name
                    showMarker(driver, d?.name.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showMarker(driver: Driver, name: String) {
        val res = this.resources
        val marker1 = BitmapFactory.decodeResource(res, R.drawable.ic_location_black)
        val smallMarker = Bitmap.createScaledBitmap(marker1, 200, 200, false)
        val sydney = driver.latitude?.toDouble()?.let {
            driver.longitude?.toDouble()?.let {
                it1 -> LatLng(it, it1)
            }
        }
    }
}
