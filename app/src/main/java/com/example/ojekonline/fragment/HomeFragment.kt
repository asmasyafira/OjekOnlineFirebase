package com.example.ojekonline.fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.ojekonline.R
import com.example.ojekonline.maps.GPSTrack
import com.example.ojekonline.model.ResultRoute
import com.example.ojekonline.model.RoutesItem
import com.example.ojekonline.network.NetworkModule
import com.example.ojekonline.network.RequestNotification
import com.example.ojekonline.utama.WaitingDriverActivity
import com.example.ojekonline.utils.Booking
import com.example.ojekonline.utils.ChangeFormat
import com.example.ojekonline.utils.Constant
import com.example.ojekonline.utils.DirectionsMapsV2
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.ResponseBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Response
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    var tanggal: String? = null
    var latAwal: Double? = null
    var lonAwal: Double? = null
    var latAkhir: Double? = null
    var lonAkhir: Double? = null
    var map: GoogleMap? = null
    var jarak: String? = null
    var keyy: String? = null
    private var auth: FirebaseAuth? = null
    var dialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()

        return view

    }

    //TODO 18
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //savedInstanceState : Untuk menyimpan status

        //menginisialisasi dari mapsview
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }

        showPermission()
        visibleView(false)
        keyy?.let { bookingHistoryUser(it) }

        homeAwal?.onClick {
            takeLocation(1)
        }

        homeTujuan?.onClick {
            takeLocation(2)
        }

        homeBottomNext?.onClick {
            if (homeAwal.text.isNotEmpty() && homeTujuan.text.isNotEmpty()) {
                insertServer()
            } else {
                toast("Tidak boleh kosong").show()
                view.let {
                    Snackbar.make(
                        it, "Tidak boleh kosong",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun bookingHistoryUser(key: String) {
        showDialog(true)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)

        myRef.child(key).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val booking = snapshot.getValue(Booking::class.java)
                if (booking?.driver != "") {
                    startActivity<WaitingDriverActivity>(Constant.Key to key)
                    showDialog(false)
                }
            }

        })
    }


    private fun insertServer() {
        val currentTime = Calendar.getInstance().time
        tanggal = currentTime.toString()
        insertRequest(
            currentTime.toString(),
            auth?.uid.toString(),
            homeAwal.text.toString(),
            latAwal,
            lonAwal,
            homeTujuan.text.toString(),
            latAkhir,
            lonAkhir,
            homePrice.text.toString(),
            jarak.toString()
        )
    }


    //TODO 34
    //memasukkan data booking ke realtime database
    fun insertRequest(
        tanggal: String,
        uid: String,
        lokasiAwal: String,
        latAwal: Double?,
        lonAwal: Double?,
        lokasiTujuan: String,
        latTujuan: Double?,
        lonTujuan: Double?,
        harga: String,
        jarak: String
    ): Boolean {

        val booking = Booking()
        booking.tanggal = tanggal
        booking.uid = uid
        booking.lokasiAwal = lokasiAwal
        booking.latAwal = latAwal
        booking.lonAwal = lonAwal
        booking.lokasiTujuan = lokasiTujuan
        booking.latTujuan = latTujuan
        booking.lonTujuan = lonTujuan
        booking.harga = harga
        booking.jarak = jarak
        booking.status = 1
        booking.driver = ""

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constant.tb_booking)

        keyy = database.reference.push().key
        val hei = keyy

        pushNotif(booking)
        hei?.let { bookingHistoryUser(it) }
        myRef.child(keyy ?: "").setValue(booking)

        return true
    }


    //TODO 37
    private fun pushNotif(booking: Booking) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Driver")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (issue in snapshot.children) {

                    //proses mengambil token dr server
                    val token = issue.child("token").getValue(String::class.java)
                    println(token.toString())
                    val request = RequestNotification()
                    request.token = token
                    request.sendNotificationModel = booking

                    //push ke firebase server
                    NetworkModule.getServiceFcm().sendChatNotification(request)
                        .enqueue(object : retrofit2.Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                                Log.d("Network failed :", t.message)
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                response.body()
                                Log.d("Response server:", response.message())
                            }

                        })
                }
            }

        })
    }

    private fun showDialog(status: Boolean) {
        dialog = Dialog(activity!!)
        dialog?.setContentView(R.layout.dialogwaitingdriver)
        if (status) {
            dialog?.show()
        } else dialog?.dismiss()
    }


    //TODO 23
    //permission lagi
    fun showPermission() {

        showGps()

        if (activity?.let {
                ContextCompat.checkSelfPermission(
                    it, android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED) {
            if (activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it, android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }!!) {

                showGps()
            } else {
                requestPermissions(
                    arrayOf(
                        //menyediakan lokasi yg lebih akurat
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        //memberikan akurasi lokasi dalam blok kota
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 1
                )
            }
        }
    }

    //TODO 19
    //menampilkan lokasi user bedasrakn gps device user
    private fun showGps() {
        val gps = context?.let { GPSTrack(it) }
        if (gps?.canGetLocation()!!) {
            latAwal = gps.latitude
            lonAwal = gps.longitude

            showMainMarket(latAwal ?: 0.0, lonAwal ?: 0.0, "My Locations")

            val name = showName(latAwal ?: 0.0, lonAwal ?: 0.0)

            homeAwal.text = name

        } else gps.showSettingGPS()
    }

    fun visibleView(status: Boolean) {

        if (status) {
            homebottom.visibility = View.VISIBLE
            homeBottomNext.visibility = View.VISIBLE
        } else {
            homebottom.visibility = View.GONE
            homeBottomNext.visibility = View.GONE
        }
    }


    //TODO 24
    //proses mengarahkan ke autocomplete google place
    fun takeLocation(status: Int) {
        try {
            context?.applicationContext?.let {
                Places.initialize(
                    it, Constant.MAPS_API
                )
            }

            val fields = arrayListOf(
                Place.Field.ID, Place.Field.NAME,
                Place.Field.LAT_LNG, Place.Field.ADDRESS
            )

            val intent = context?.applicationContext?.let {
                Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    fields
                ).build(it)
            }
            startActivityForResult(intent, status)
        } catch (e: GooglePlayServicesRepairableException) {

        } catch (e: GooglePlayServicesNotAvailableException) {
        }

    }


    //TODO 25
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //DR MANA
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val place = data?.let {
                    Autocomplete.getPlaceFromIntent(it)
                }

                latAwal = place?.latLng?.latitude
                lonAwal = place?.latLng?.longitude

                homeAwal.text = place?.address.toString()
                showMainMarket(
                    latAwal ?: 0.0, lonAwal ?: 0.0,
                    place?.address.toString()
                )

                Log.i("locations", "Place: " + place?.name)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                val status = data?.let { Autocomplete.getStatusFromIntent(it) }

                Log.i("locations", status?.statusMessage)
            } else if (resultCode == RESULT_CANCELED) {
            }
            //MAU KEMANA
        } else {
            if (resultCode == RESULT_OK) {
                val place = data?.let { Autocomplete.getPlaceFromIntent(it) }

                latAkhir = place?.latLng?.latitude
                lonAkhir = place?.latLng?.longitude

                showMarker(
                    latAkhir ?: 0.0, lonAkhir ?: 0.0,
                    place?.address.toString()
                )

                homeTujuan.text = place?.address.toString()

                route()

                Log.i("locations", "Place: " + place?.name)
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = data?.let { Autocomplete.getStatusFromIntent(it) }

                Log.i("locations", status?.statusCode.toString())
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    //TODO 29
    //untuk proses dari request route ke server gugel
    @SuppressLint("CheckResult")
    private fun route() {
        val origin = latAwal.toString() + "," + lonAwal.toString()

        val dest = latAkhir.toString() + "," + lonAkhir.toString()

        NetworkModule.getService()
            .actionRoute(origin, dest, Constant.MAPS_API)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ t: ResultRoute? ->
                showData(t?.routes)
            }, {})
    }

    //TODO 32
    //menampilkan harga, route
    private fun showData(routes: List<RoutesItem?>?) {

        visibleView(true)

        if (routes != null) {
            val point = routes[0]?.overviewPolyline?.points

            jarak = routes[0]?.legs?.get(0)?.distance?.text
            val jarakValue = routes[0]?.legs?.get(0)?.distance?.value
            val waktu = routes[0]?.legs?.get(0)?.duration?.text

            home_waktu_distance.text = waktu + " ( " + jarak + ")"

            val pricex = jarakValue?.toDouble()?.let {
                Math.round(it)
            }

            val price = pricex?.div(1000.0)?.times(2000.0)
            val price2 = ChangeFormat.toRupiahFormat(price.toString())
            homePrice.text = "Rp." + price2
            DirectionsMapsV2.gambarRoute(map!!, point!!)
        } else {
            alert {
                message = "Data Route Null"
            }.show()
        }
    }

    //TODO 21
    //GEOCODER
    //menerjemahkan koordinat jdi nma lokasi
    fun showName(lat: Double, lon: Double): String {
        var name = ""
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val adresses =
                geocoder.getFromLocation(lat, lon, 1)

            if (adresses.size > 0) {
                val fetchedAdress = adresses.get(0)
                val strAddress = StringBuilder()

                for (i in 0..fetchedAdress.maxAddressLineIndex) {
                    name = strAddress.append(
                        fetchedAdress
                            .getAddressLine(i)
                    ).append("").toString()
                }
            }
        } catch (e: Exception) {

        }
        return name

    }

    //TODO 22
    //ini buat nampilin marker lokasi yg merah
    //marker origin
    fun showMainMarket(lat: Double, lon: Double, msg: String) {
        val res = context?.resources
        val marker1 = BitmapFactory
            .decodeResource(res, R.mipmap.pinblack)
        val smallmarker = Bitmap
            .createScaledBitmap(marker1, 80, 120, false)

        val coordinate = LatLng(lat, lon)

//        membuat pin baru di android
        map?.addMarker(
            MarkerOptions().position(coordinate)
                .title(msg).icon(
                    BitmapDescriptorFactory
                        .fromBitmap(smallmarker)
                )
        )

        //mengatur zoom camera
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(coordinate, 16f)
        )

        //menyetting biar posisi marker selalu di tengah
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))

    }

    //TODO 23
    //ini buat nampilin marker lokasi yg hitam
    //marker destination
    fun showMarker(lat: Double, lon: Double, msg: String) {
        val coordinat = LatLng(lat, lon)

        map?.addMarker(MarkerOptions().position(coordinat).title(msg))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinat, 16f))
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }

    //TODO 17
    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap?) {
        map = p0
        map?.uiSettings?.isMyLocationButtonEnabled = false
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(-6.3088652, 106.682188), 12f
            )
        )

        //moveCamera : mengubah fokus lokasi suatu peta
    }


    override fun onResume() {
        keyy?.let { bookingHistoryUser(it) }
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            showGps()
        }
    }
}


