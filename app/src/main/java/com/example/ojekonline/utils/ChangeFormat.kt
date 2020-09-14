package com.example.ojekonline.utils

import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

//TODO 31
//untuk halaman mengatur fomat harga
object ChangeFormat {

    fun toRupiahFormat(nominal: String): String{

        val df = DecimalFormat.getCurrencyInstance() as DecimalFormat

        val dfs = DecimalFormatSymbols()

        dfs.currencySymbol = ""
        dfs.monetaryDecimalSeparator = ','
        dfs.groupingSeparator = ','
        df.decimalFormatSymbols = dfs
        df.maximumFractionDigits = 0

        val rupiah = df.format(d(nominal))
        return rupiah
    }

    private fun d(transPokok: String): Double? {

        var x : Double = 0.0
        try {
            x = java.lang.Double.parseDouble(transPokok)
        } catch (e : Exception){

        }
        
        return x
    }

}