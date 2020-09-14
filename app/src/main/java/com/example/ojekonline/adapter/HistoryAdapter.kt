package com.example.ojekonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ojekonline.R
import com.example.ojekonline.utils.Booking
import kotlinx.android.synthetic.main.history_item.view.*

//TODO 39
class HistoryAdapter(
    private val mValues: List<Booking>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){


    //mengambil layout u dilempar ke view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)

        return ViewHolder(view)
    }

    //menghitung jmlh item di rv
    override fun getItemCount(): Int = mValues.size

    //ngirim data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.mAwal.text = item.lokasiAwal
        holder.mTanggal.text = item.tanggal
        holder.mTujuan.text = item.lokasiTujuan
    }

    //untuk menginisialisasi
    inner class ViewHolder(mView: View): RecyclerView.ViewHolder(mView) {
        var mAwal: TextView = mView.itemAwal
        var mTujuan: TextView = mView.itemTujuan
        var mTanggal: TextView = mView.item_tanggal

    }
}