package com.example.ojekonline.model

import com.google.gson.annotations.SerializedName

data class StartLocation(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)