package app.findp.service

import app.findp.entities.ElementEntity
import app.findp.entities.utils.ElementId
import app.findp.entities.utils.Util
import retrofit2.Call
import retrofit2.http.*

interface ElementService {
    // POST
    @POST("{managerDomain}/{managerEmail}")
    fun createNewElement(
        @Path("managerDomain") managerDomain: String?,
        @Path("managerEmail") managerEmail: String?,
        @Body newElementEntity: ElementEntity?
    ): Call<ElementEntity?>?

    // PUT
    @PUT("{managerDomain}/{managerEmail}/{elementDomain}/{elementId}")
    fun updateElement(
        @Path("managerDomain") managerDomain: String?, @Path("managerEmail") managerEmail: String?,
        @Path("elementDomain") elementDomain: String?, @Path("elementId") elementId: String?,
        @Body update: ElementEntity?
    ): Call<Void?>?

    @PUT("{managerDomain}/{managerEmail}/{elementDomain}/{elementId}/children")
    fun bindParentElementToChildElement(
        @Path("managerDomain") managerDomain: String?, @Path("managerEmail") managerEmail: String?,
        @Path("elementDomain") elementDomain: String?, @Path("elementId") elementId: String?,
        @Body input: ElementId?
    ): Call<Void?>?

    // GET
    @GET("{userDomain}/{userEmail}/{elementDomain}/{elementId}")
    fun getElement(
        @Path("userDomain") userDomain: String?, @Path("userEmail") userEmail: String?,
        @Path("elementDomain") elementDomain: String?, @Path("elementId") elementId: String?
    ): Call<ElementEntity?>?

    @GET("{userDomain}/{userEmail}")
    fun getAllElements(
        @Path("userDomain") userDomain: String?,
        @Path("userEmail") userEmail: String?
    ): Call<Array<ElementEntity?>?>?

    @GET("{userDomain}/{userEmail}/{elementDomain}/{elementId}/children")
    fun getAllChildrenElements(
        @Path("userDomain") userDomain: String?, @Path("userEmail") userEmail: String?,
        @Path("elementDomain") elementDomain: String?, @Path("elementId") elementId: String?
    ): Call<Array<ElementEntity?>?>?

    @GET("{userDomain}/{userEmail}/{elementDomain}/{elementId}/parents")
    fun getAllParentsElements(
        @Path("userDomain") userDomain: String?, @Path("userEmail") userEmail: String?,
        @Path("elementDomain") elementDomain: String?, @Path("elementId") elementId: String?
    ): Call<Array<ElementEntity?>?>?

    @GET("{userDomain}/{userEmail}/search/byName/{name}")
    fun getAllElementsByName(
        @Path("userDomain") userDomain: String?,
        @Path("userEmail") userEmail: String?,
        @Path("name") name: String?,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Call<Array<ElementEntity?>?>?

    @GET("{userDomain}/{userEmail}/search/byType/{type}")
    fun getAllElementsByType(
        @Path("userDomain") userDomain: String?,
        @Path("userEmail") userEmail: String?,
        @Path("name") type: String?,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Call<Array<ElementEntity?>?>?

    @GET("{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}")
    fun getAllElementsByLocation(
        @Path("userDomain") userDomain: String?, @Path("userEmail") userEmail: String?,
        @Path("lat") lat: String?, @Path("lng") lng: String?, @Path("distance") distance: String?,
        @Query("size") size: Int, @Query("page") page: Int
    ): Call<Array<ElementEntity?>?>?

    companion object {
        const val BASE_URL = "http://" + Util.ipAndPort + "/acs/elements/"
    }
}