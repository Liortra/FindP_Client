package app.findp.activites.Parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.UnParkingActivity
import app.findp.activites.Start.LoginActivity
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

class UnParkingActivity : AppCompatActivity() {
    private val ACTION = "action"
    private val UNPARK = "unpark"
    private var unparkBtn: Button? = null
    private var avatar_imageView: ImageView? = null
    private var logout_btn: ImageView? = null
    private var user_role: TextView? = null
    private var userEntity: UserEntity? = null
    private var actionService: ActionService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_un_parking)
        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit2 = createRetrofit(okHttpClient, ACTION)
        actionService = retrofit2.create(ActionService::class.java)
        userEntity = intent.getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        setIds()
        setInfo()
        setOnClickListeners()
    }

    private fun setInfo() {
        val context = avatar_imageView!!.context
        val id = context.resources.getIdentifier(
            userEntity!!.avatar,
            "drawable",
            this@UnParkingActivity.packageName
        )
        avatar_imageView!!.setImageResource(id)
        user_role!!.text = userEntity!!.username
    }

    private fun setOnClickListeners() {
        unparkBtn!!.setOnClickListener { unParkAction() }
        logout_btn!!.setOnClickListener { v: View? ->
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
    }

    private fun unParkAction() {
        val actionCall = actionService!!.invokeAction(
            ActionEntity(
                null, UNPARK, Element(ElementId("", "")),
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
                        this@UnParkingActivity,
                        "Something ok: " + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    val unParkingActivityIntent =
                        Intent(this@UnParkingActivity, MainActivity::class.java)
                    unParkingActivityIntent.putExtra(LoginActivity.Companion.USER, userEntity)
                    startActivity(unParkingActivityIntent)
                    finish()
                } else {
                    Log.i(
                        "TAG",
                        "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message() + "  " + response.body()
                    )
                    Toast.makeText(
                        this@UnParkingActivity,
                        "Something not ok: " + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(this@UnParkingActivity, "Failure getAll", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "onFailure: $t")
                return
            }
        })
    }

    private fun setIds() {
        unparkBtn = findViewById(R.id.unpark_btn)
        avatar_imageView = findViewById<View>(R.id.avatar_imageView) as ImageView
        logout_btn = findViewById<View>(R.id.logout_btn) as ImageView
        user_role = findViewById<View>(R.id.user_name) as TextView
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}