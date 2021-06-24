package app.findp.service

import app.findp.entities.UserEntity
import app.findp.entities.utils.NewUserDetails
import app.findp.entities.utils.Util
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    // POST
    @POST(".")
    fun createNewUser(@Body newUserDetails: NewUserDetails?): Call<UserEntity?>?

    // PUT
    @PUT("{userDomain}/{userEmail}")
    fun updateUserDetails(
        @Path("userDomain") userDomain: String?,
        @Path("userEmail") userEmail: String?,
        @Body userEntity: UserEntity?
    ): Call<Void?>?

    // GET
    @GET("login/{userDomain}/{userEmail}")
    fun login(
        @Path("userDomain") userDomain: String?,
        @Path("userEmail") userEmail: String?
    ): Call<UserEntity?>?

    companion object {
        const val BASE_URL = "http://" + Util.ipAndPort + "/acs/users/"
    }
}