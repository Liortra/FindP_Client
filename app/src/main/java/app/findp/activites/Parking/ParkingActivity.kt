package app.findp.activites.Parking

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.ParkingActivity
import app.findp.activites.Start.LoginActivity
import app.findp.data.ParkingData
import app.findp.entities.ActionEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.*
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.service.ActionService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ParkingActivity : AppCompatActivity() {
    private var parkButton: Button? = null
    private var parkingData: ParkingData? = null
    private var actionService: ActionService? = null
    private var userEntity: UserEntity? = null
    private val PARK = "park"
    private val ACTION = "action"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)
        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit2 = createRetrofit(okHttpClient, ACTION)
        actionService = retrofit2.create(ActionService::class.java)
        parkingData =
            intent.getSerializableExtra(ResultsActivity.Companion.PARKING_DATA) as ParkingData
        userEntity = intent.getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        Log.d("userEntity", "onCreate: $userEntity")
        setIds()
        setClickListeners()
    }

    private fun setClickListeners() {
        parkButton!!.setOnClickListener { popUpAlertDialog() }
    }

    private fun setIds() {
        parkButton = findViewById(R.id.park_btn)
    }

    private fun popUpAlertDialog() {
        val alertMessage = TextView(this)
        alertMessage.gravity = Gravity.CENTER
        alertMessage.setPadding(5, 15, 0, 15)
        alertMessage.text = "Click OK if you park"
        alertMessage.setTextColor(Color.BLACK)
        alertMessage.setTypeface(null, Typeface.BOLD)
        AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setView(alertMessage)
            .setPositiveButton(
                "OK"
            ) { dialog, id ->
                parkingAction()
                val parkingActivityIntent =
                    Intent(this@ParkingActivity, UnParkingActivity::class.java)
                parkingActivityIntent.putExtra(LoginActivity.Companion.USER, userEntity)
                startActivity(parkingActivityIntent)
                finish()
            }
            .setNegativeButton("CANCEL") { dialog, id -> }.show()
    }

    fun parkingAction() {
        val actionCall = actionService!!.invokeAction(
            ActionEntity(
                null, PARK, Element(parkingData!!.elementId),
                null, InvokedBy(userEntity!!.userId), HashMap()
            )
        )
        actionCall!!.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Log.i(
                        "TAG",
                        "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message().javaClass + "  " + response.body()!!.javaClass
                    )
                    Toast.makeText(
                        this@ParkingActivity,
                        "Something ok: " + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    val parkingActivityIntent =
                        Intent(this@ParkingActivity, UnParkingActivity::class.java)
                    parkingActivityIntent.putExtra(LoginActivity.Companion.USER, userEntity)
                    startActivity(parkingActivityIntent)
                } else {
                    Log.i(
                        "TAG",
                        "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message() + "  " + response.body()
                    )
                    Toast.makeText(
                        this@ParkingActivity,
                        "Something not ok: " + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@ParkingActivity, "Failure getAll", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "onFailure: $t")
                return
            }
        })
    }
}