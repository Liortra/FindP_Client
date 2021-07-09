package app.findp.activites.Parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Start.LoginActivity
import app.findp.data.ParkingData
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.Location
import app.findp.entities.utils.Util
import app.findp.service.ElementService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ScanningActivity : AppCompatActivity() {
    val IS_TAKEN = "isTaken"
    val DISTANCE = "1"
    private var iconImg: ImageView? = null
    private var scanningMsg: TextView? = null
    private var playerUserEntity: UserEntity? = null
    private var elementService: ElementService? = null
    private  lateinit var parkingData: Array<ParkingData?>
    private val MyLatMockup = 32.1150148
    private val MyLngMockup = 34.8162585
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        init()
        //        listeners();
        rotate()
        //        getAllElementsByLocation();
    }

    private fun init() {
        scanningMsg = findViewById<View>(R.id.scan_msg) as TextView?
        iconImg = findViewById<View>(R.id.icon_image) as ImageView?
        // static method
        val okHttpClient: OkHttpClient = Util.createOkHttpClient()
        val retrofit2: Retrofit = Util.createRetrofit(okHttpClient, LoginActivity.Companion.ELEMENT)
        elementService = retrofit2.create<ElementService>(ElementService::class.java)
        playerUserEntity =
            getIntent().getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        userLocationMockup = Location(MyLatMockup, MyLngMockup)
    }

    var rotateAnimation: RotateAnimation? = null
    private fun rotate() {
        rotateAnimation = RotateAnimation(
            0F, 360F,
            RotateAnimation.RELATIVE_TO_SELF, .5f,
            RotateAnimation.RELATIVE_TO_SELF, .5f
        )
        rotateAnimation!!.setDuration(2000)
        rotateAnimation!!.setRepeatCount(Animation.ABSOLUTE)
        rotateAnimation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                scanningMsg!!.setVisibility(View.VISIBLE)
            }

            override fun onAnimationEnd(animation: Animation) {
                allElementsByLocation
            }

            override fun onAnimationRepeat(animation: Animation) {
                if (scanningMsg!!.getVisibility() == View.INVISIBLE) scanningMsg!!.setVisibility(View.VISIBLE) else scanningMsg!!.setVisibility(
                    View.INVISIBLE
                )
            }
        })
        iconImg!!.startAnimation(rotateAnimation)
    }

    //                        iconImg.startAnimation(rotateAnimation);
    val allElementsByLocation: Unit
        get() {
            val elementsCall: Call<Array<ElementEntity?>?> = elementService!!.getAllElementsByLocation(
                playerUserEntity!!.userId!!.domain,
                playerUserEntity!!.userId!!.email, "32.1149197", "34.8159152", DISTANCE, 10, 0
            )!!
            elementsCall.enqueue(object : Callback<Array<ElementEntity?>?> {
                override fun onResponse(
                    call: Call<Array<ElementEntity?>?>,
                    response: Response<Array<ElementEntity?>?>
                ) {
                    if (response.isSuccessful) {
                        Log.i(
                            "TAG",
                            "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message().javaClass + "  " + response.body()!!.javaClass
                        )
                        Toast.makeText(
                            this@ScanningActivity,
                            "Something ok: " + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                        val elementsEntities: Array<ElementEntity> = filterTakenPark(
                            response.body()!!
                        )
                        if (elementsEntities.size > 0) {
                            parkingData = convertElementsToParking(elementsEntities)
                            sortByDistance(parkingData)
                            val scanningActivityIntent =
                                Intent(this@ScanningActivity, ResultsActivity::class.java)
                            scanningActivityIntent.putExtra(ELEMENT_ENTITIES, parkingData)
                            scanningActivityIntent.putExtra(
                                LoginActivity.Companion.USER,
                                playerUserEntity
                            )
                            startActivity(scanningActivityIntent)
                            //                        iconImg.startAnimation(rotateAnimation);
                            finish()
                        } else {
                            val scanningActivityIntent =
                                Intent(this@ScanningActivity, SorryActivity::class.java)
                            scanningActivityIntent.putExtra(
                                LoginActivity.Companion.USER,
                                playerUserEntity
                            )
                            startActivity(scanningActivityIntent)
                            finish()
                        }
                    } else {
                        Log.i(
                            "TAG",
                            "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message() + "  " + response.body()
                        )
                        Toast.makeText(
                            this@ScanningActivity,
                            "Something not ok: " + response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Array<ElementEntity?>?>, t: Throwable) {
                    Toast.makeText(this@ScanningActivity, "Failure getAll", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "onFailure: $t")
                    return
                }
            })
        }

    private fun filterTakenPark(elementEntityData: Array<ElementEntity?>): Array<ElementEntity> {
        val list: MutableList<ElementEntity> = ArrayList<ElementEntity>()
        for (elementEntity in elementEntityData) {
            if (!(elementEntity!!.elementAttributes!!.get(IS_TAKEN) as Boolean)) {
                list.add(elementEntity)
            }
        }
        return list.toTypedArray()
    }

    private fun convertElementsToParking(elementEntityData: Array<ElementEntity>): Array<ParkingData?> {
        val parkingData: Array<ParkingData?> = arrayOfNulls<ParkingData>(elementEntityData.size)
        for (i in elementEntityData.indices) {
            parkingData[i] = ParkingData(
                elementEntityData[i].name!!,
                elementEntityData[i].location!!,
                elementEntityData[i].elementId!!,
                java.lang.Boolean.parseBoolean(
                    elementEntityData[i].elementAttributes!!.get(IS_TAKEN).toString()
                )
            )
            parkingData[i]!!.calculateDistance(userLocationMockup!!)
        }
        return parkingData
    }

    fun sortByDistance(arr: Array<ParkingData?>) {
        val n = arr.size
        for (i in 0 until n - 1) for (j in 0 until n - i - 1) if (arr[j]!!.distance > arr[j + 1]!!.distance) {
            // swap arr[j+1] and arr[i]
            val temp: Double = arr[j]!!.distance
            arr[j]!!.distance = arr[j + 1]!!.distance
            arr[j + 1]!!.distance = temp
        }
    }

    companion object {
        const val ELEMENT_ENTITIES = "elementEntities"
        var userLocationMockup: Location? = null
    }
}