package app.findp.entities.utils

import app.findp.service.ActionService
import app.findp.service.ElementService
import app.findp.service.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object Util {
    //const
    const val domain = "2020b.lior.trachtman"
    const val GALLERY_REQUEST = 1
    const val ipAndPort = "192.168.10.76:8083" //"ipconfig:port";

    // static methods
    @JvmStatic
    fun createOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    @JvmStatic
    fun createRetrofit(okHttpClient: OkHttpClient?, type: String): Retrofit {
        val retrofit: Retrofit
        retrofit = when (type) {
            "user" -> Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build()
            "element" -> Retrofit.Builder()
                .baseUrl(ElementService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build()
            "action" -> Retrofit.Builder()
                .baseUrl(ActionService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build()
            else -> throw IllegalStateException("Unexpected value: $type")
        }
        return retrofit
    }
}