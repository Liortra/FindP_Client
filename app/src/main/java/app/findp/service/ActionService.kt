package app.findp.service

import app.findp.entities.ActionEntity
import app.findp.entities.utils.Util
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ActionService {
    // POST
    @POST(".")
    fun invokeAction(@Body actionEntity: ActionEntity?): Call<Any?>?

    companion object {
        const val BASE_URL = "http://" + Util.ipAndPort + "/acs/actions/"
    }
}