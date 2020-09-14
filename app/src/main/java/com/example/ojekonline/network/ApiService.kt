package com.example.ojekonline.network

import com.example.ojekonline.model.ResultRoute
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

//TODO 28
interface ApiService {
    @GET("json")
    fun actionRoute(@Query("origin") origin : String,
                    @Query("destination") destination: String,
                    @Query("key") key: String):Flowable<ResultRoute>

    //TODO 36
    @Headers(
        "Authorization: key=AAAAnXG4RD0:APA91bGSORubYD2OziIieN8rSgrftjg_Gya7Y-6m6CFSsqwtq2l0Et6sUaT0Viuf_9z8P3zjcod4Ap6AoMrnb4nf2d4vDKQ1xxL1z1XKnQLJJuSFNqbI52lzrG3B6TR0oFnzgiXgaPDz",
        "Content-Type:application/json"
    )

    @POST("fcm/send")
    fun sendChatNotification(@Body requestNotification: RequestNotification): Call<ResponseBody>
}