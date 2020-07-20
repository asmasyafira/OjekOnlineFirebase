package com.example.ojekonline.fragment


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.ojekonline.R
import com.example.ojekonline.maps.GPSTrack
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var latAwal: Double? = null
    var lonAwal: Double? = null
    var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return TextView(activity).apply {
            setText(R.string.hello_blank_fragment)
        }
    }

    //menginisialisasi dari mapsview

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }
    }

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

    //GEOCODER
    //menerjemahkan koordinat jdi nma lokasi
    private fun showName(lat: Double, lon: Double): String {
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
    //marker origin
    private fun showMainMarket(lat: Double, lon: Double, msg: String) {
        val res = context?.resources
        val marker1 = BitmapFactory
            .decodeResource(res, R.drawable.ic_location_red)
        val smallmarker = Bitmap
            .createScaledBitmap(marker1, 80, 120, false)

        val coordinate = LatLng(lat, lon)

        //membuat pin baru di andro
        map?.addMarker(
            MarkerOptions().position(coordinate).title(msg)
                .icon(BitmapDescriptorFactory.fromBitmap(smallmarker))
        )

        //mengatur zoom canera
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 16f))

        //nyeting biar posisi marker biar selalu ditengah
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))


    }
    //marker destination
    fun showMarker(lat: Double, lon: Double, msg:String){
        val coordinat = LatLng(lat, lon)

        map?.addMarker(MarkerOptions().position(coordinat).title(msg))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinat, 16f))
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }


}


