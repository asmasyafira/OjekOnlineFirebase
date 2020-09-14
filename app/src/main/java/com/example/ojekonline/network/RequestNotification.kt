package com.example.ojekonline.network

import com.example.ojekonline.utils.Booking
import com.google.gson.annotations.SerializedName

//TODO 35
class RequestNotification {

    @SerializedName("to")
    var token: String? = null

    @SerializedName("data")
    var sendNotificationModel: Booking? = null
}