package app.findp.activites.Start

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.MainActivity
import app.findp.activites.Parking.UnParkingActivity
import app.findp.activites.Settings.ManagerActivity
import app.findp.entities.ActionEntity
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.*
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.entities.utils.Validator.isValidEmail
import app.findp.service.ActionService
import app.findp.service.ElementService
import app.findp.service.UserService
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

open class LoginActivity : AppCompatActivity() {
    private val IS_USER_PARKED = "isUserParked"
    private var editText_email: TextInputLayout? = null
    private var login_btn: Button? = null
    private var signUp_textView: TextView? = null
    private var userService: UserService? = null
    private var elementService: ElementService? = null
    private var actionService: ActionService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        listeners()
    }

    private fun init() {
        editText_email = findViewById<View>(R.id.editText_email) as TextInputLayout
        login_btn = findViewById<View>(R.id.login_btn) as Button
        signUp_textView = findViewById<View>(R.id.signUp_textView) as TextView

        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient, USER)
        userService = retrofit.create(UserService::class.java)
        val retrofit2 = createRetrofit(okHttpClient, ELEMENT)
        elementService = retrofit2.create(ElementService::class.java)
        val retrofit3 = createRetrofit(okHttpClient, ACTION)
        actionService = retrofit3.create(ActionService::class.java)
    }

    private fun listeners() {
        login_btn!!.setOnClickListener { view: View? ->
            if (!isValidEmail(
                    editText_email!!.editText!!.text.toString().trim { it <= ' ' })
            ) {
                editText_email!!.error = "Invalid Email"
                return@setOnClickListener
            }
            val callUser = userService!!.login(
                Util.domain, editText_email!!.editText!!
                    .text.toString().trim { it <= ' ' })
            callUser!!.enqueue(object : Callback<UserEntity?> {
                override fun onResponse(call: Call<UserEntity?>, response: Response<UserEntity?>) {
                    if (!response.isSuccessful) {
                        Log.i("TAG", "onResponse: " + response.code())
                        Toast.makeText(
                            this@LoginActivity,
                            "Something wrong: $response",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.i("TAG", "onResponse: " + response.code())
                        Toast.makeText(this@LoginActivity, "Login User", Toast.LENGTH_SHORT).show()
                        val userEntity = response.body()
                        if (userEntity!!.role != UserRole.MANAGER) {
                            isUserParked(userEntity)
                        } else {
                            val checkManager = elementService!!.getAllElementsByName(
                                userEntity.userId!!.domain,
                                userEntity.userId!!.email, userEntity.username, 1, 0
                            )
                            checkManager!!.enqueue(object : Callback<Array<ElementEntity?>?> {
                                override fun onResponse(
                                    call: Call<Array<ElementEntity?>?>,
                                    response: Response<Array<ElementEntity?>?>
                                ) {
                                    Log.i("TAG", "onResponse: " + response.code())
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Find element",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val managerElementEntity = response.body()!!
                                    val managerActivityIntent =
                                        Intent(this@LoginActivity, ManagerActivity::class.java)
                                    managerActivityIntent.putExtra(USER, userEntity)
                                    managerActivityIntent.putExtra(ELEMENT, managerElementEntity[0])
                                    startActivity(managerActivityIntent)
                                    finish()
                                }

                                override fun onFailure(
                                    call: Call<Array<ElementEntity?>?>,
                                    t: Throwable
                                ) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Failure create User",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("TAG", "onFailure: $t")
                                }
                            })
                        }
                    }
                }

                override fun onFailure(call: Call<UserEntity?>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Failure create User", Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "onFailure: $t")
                }
            })
        }
        signUp_textView!!.setOnClickListener { view: View? ->
            val registrationActivityIntent =
                Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(registrationActivityIntent)
        }
    }

    private fun isUserParked(userEntity: UserEntity?) {
        val actionCall = actionService!!.invokeAction(
            ActionEntity(
                null, IS_USER_PARKED, Element(ElementId("", "")),
                null, InvokedBy(userEntity!!.userId), HashMap()
            )
        )

        actionCall!!.enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.isSuccessful) {
                    val isUserParked = response.body() as Boolean
                    Log.i(
                        "TAG",
                        "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message().javaClass + "  " + (response.body() as Boolean).javaClass
                    )
                    Toast.makeText(
                        this@LoginActivity,
                        "Something ok: " + response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TEST", "onResponse: $isUserParked")
                    if (isUserParked) {
                        val unparkingActivityIntent =
                            Intent(this@LoginActivity, UnParkingActivity::class.java)
                        unparkingActivityIntent.putExtra(USER, userEntity)
                        startActivity(unparkingActivityIntent)
                        finish()
                    } else {
                        val parkingActivityIntent =
                            Intent(this@LoginActivity, MainActivity::class.java)
                        parkingActivityIntent.putExtra(USER, userEntity)
                        startActivity(parkingActivityIntent)
                        finish()
                    }
                } else {
                    Log.i(
                        "TAG",
                        "onResponse: " + response.code() + "  " + "  " + response.errorBody() + "  " + response.message() + "  " + response.body()
                    )
                    val parkingActivityIntent = Intent(this@LoginActivity, MainActivity::class.java)
                    parkingActivityIntent.putExtra(USER, userEntity)
                    startActivity(parkingActivityIntent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Failure getAll", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "onFailure: $t")
                return
            }
        })
    }

    companion object{
        const val USER = "user"
        const val ELEMENT = "element"
        const val ACTION = "action"
    }
}