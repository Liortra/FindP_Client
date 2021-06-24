package app.findp.activites.Start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.MainActivity
import app.findp.activites.Settings.ManagerActivity
import app.findp.activites.Start.RegistrationActivity
import app.findp.entities.ActionEntity
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.*
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.entities.utils.Validator.isValidAvatarUrl
import app.findp.entities.utils.Validator.isValidEmail
import app.findp.entities.utils.Validator.isValidUserName
import app.findp.service.ActionService
import app.findp.service.ElementService
import app.findp.service.UserService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RegistrationActivity : AppCompatActivity(), OnItemSelectedListener {
    private var editText_email: TextInputLayout? = null
    private var editText_userName: TextInputLayout? = null
    private var avatar_imageView: ImageView? = null
    private var role_spinner: Spinner? = null
    private var create_btn: Button? = null
    private var url: Uri? = null
    private var userType: String? = null
    private var userService: UserService? = null
    private var elementService: ElementService? = null
    private var actionService: ActionService? = null

    // listView
    private var listView: ListView? = null
    private var avatar: String? = null
    private val image = intArrayOf(
        R.drawable.avatar01,
        R.drawable.avatar02,
        R.drawable.avatar03,
        R.drawable.avatar04,
        R.drawable.avatar05,
        R.drawable.avatar06
    )
    private val headline =
        arrayOf("Student", "Worker", "Anonymous", "City employee", "Professor", "Teacher")
    private val drawableName =
        arrayOf("avatar01", "avatar02", "avatar03", "avatar04", "avatar05", "avatar06")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        init()
        listeners()
    }

    private fun init() {
        editText_email = findViewById<View>(R.id.editText_email) as TextInputLayout
        editText_userName = findViewById<View>(R.id.editText_userName) as TextInputLayout
        avatar_imageView = findViewById<View>(R.id.avatar_imageView) as ImageView
        create_btn = findViewById<View>(R.id.create_btn) as Button
        role_spinner = findViewById<View>(R.id.roleSpinner) as Spinner

        //spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.role_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        role_spinner!!.adapter = adapter
        role_spinner!!.onItemSelectedListener = this

        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient, "user")
        userService = retrofit.create(UserService::class.java)
        val retrofit2 = createRetrofit(okHttpClient, "element")
        elementService = retrofit2.create(ElementService::class.java)
        val retrofit3 = createRetrofit(okHttpClient, "action")
        actionService = retrofit3.create(ActionService::class.java)

        // ListView
        listView = findViewById(R.id.list_view)
        val list: MutableList<HashMap<String, String?>> = ArrayList()
        for (i in 0..5) {
            val hashMap = HashMap<String, String?>()
            hashMap["image"] = Integer.toString(image[i])
            hashMap["headline"] = headline[i]
            list.add(hashMap)
        }
        val from = arrayOf("image", "headline")
        val to = intArrayOf(R.id.imageViewLogo, R.id.textViewHeadline1)
        val simpleAdapter = SimpleAdapter(baseContext, list, R.layout.list_view_activity, from, to)
        listView.setAdapter(simpleAdapter)
        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            view.isSelected = true
            avatar = drawableName[position]
        })
    }

    private fun listeners() {
        create_btn!!.setOnClickListener { view: View? ->
            if (!isValidEmail(
                    editText_email!!.editText!!.text.toString().trim { it <= ' ' })
            ) {
                editText_email!!.error = "Invalid Email"
                return@setOnClickListener
            }
            if (!isValidUserName(
                    editText_userName!!.editText!!.text.toString().trim { it <= ' ' })
            ) {
                editText_userName!!.error = "Invalid User name"
                return@setOnClickListener
            }
            if (avatar == null || !isValidAvatarUrl(avatar)) {
                Toast.makeText(this@RegistrationActivity, "Choose avatar", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val newUser =
                NewUserDetails(editText_email!!.editText!!.text.toString().trim { it <= ' ' },
                    UserRole.valueOf(userType!!),
                    editText_userName!!.editText!!.text.toString().trim { it <= ' ' },
                    avatar!!
                )
            createUser(newUser)
        }
    }

    private fun createUser(newUser: NewUserDetails) {
        if (newUser.role != UserRole.MANAGER) {
            val callUser = userService!!.createNewUser(newUser)
            callUser!!.enqueue(object : Callback<UserEntity?> {
                override fun onResponse(call: Call<UserEntity?>, response: Response<UserEntity?>) {
                    if (!response.isSuccessful) {
                        Log.i("TAG", "onResponse: " + response.code())
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Something wrong: $response",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    } else {
                        Log.i("TAG", "onResponse: " + response.code())
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Create Player User",
                            Toast.LENGTH_SHORT
                        ).show()
                        val userEntity = response.body()
                        val mainActivityIntent =
                            Intent(this@RegistrationActivity, MainActivity::class.java)
                        mainActivityIntent.putExtra("user", userEntity)
                        startActivity(mainActivityIntent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<UserEntity?>, t: Throwable) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Failure create User",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onFailure: $t")
                    return
                }
            })
        } else {
            val actionEntity = createActionForManager(newUser)
            val callAction = actionService!!.invokeAction(actionEntity)
            callAction!!.enqueue(object : Callback<Any?> {
                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    if (!response.isSuccessful) {
                        if (response.code() == 500) {
                            Log.i("TAG", "onResponse: " + response.code())
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Manger already exist",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        } else {
                            Log.i("TAG", "onResponse: " + response.code())
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Something wrong: $response",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                    } else {
                        val manager = convertObjectToEntity(response.body())
                        val callElement = elementService!!.getAllElementsByName(
                            manager.userId!!.domain, manager.userId!!.email,
                            manager.username, 1, 0
                        )
                        callElement!!.enqueue(object : Callback<Array<ElementEntity?>> {
                            override fun onResponse(
                                call: Call<Array<ElementEntity?>>,
                                response: Response<Array<ElementEntity?>>
                            ) {
                                if (!response.isSuccessful) {
                                    Log.i("TAG", "onResponse: " + response.code())
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Something wrong: $response",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return
                                } else {
                                    val managerElement = response.body()!!
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Success Element user",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("TAG", "onResponse: " + response.code())
                                    val managerActivityIntent = Intent(
                                        this@RegistrationActivity,
                                        ManagerActivity::class.java
                                    )
                                    managerActivityIntent.putExtra("user", manager)
                                    managerActivityIntent.putExtra("element", managerElement[0])
                                    startActivity(managerActivityIntent)
                                    finish()
                                }
                            }

                            override fun onFailure(
                                call: Call<Array<ElementEntity?>>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Failure Manager User",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.i("TAG", "onFailure: $t")
                                return
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Failure invoke action",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onFailure: $t")
                    return
                }
            })
        }
    }

    private fun convertObjectToEntity(response: Any?): UserEntity {
        val mapper = ObjectMapper()
        return mapper.convertValue(response, object : TypeReference<UserEntity?>() {})
    }

    private fun createActionForManager(newUser: NewUserDetails): ActionEntity {
        val hm: MutableMap<String, Any> = HashMap()
        hm["email"] = newUser.email
        hm["role"] = newUser.role
        hm["username"] = newUser.username
        hm["avatar"] = newUser.avatar
        return ActionEntity(
            null, "createUserManagerByUsername", Element(ElementId("", "")), Date(),
            InvokedBy(UserId(Util.domain, newUser.email)), hm
        )
    }

    private fun createElementForManger(userEntity: UserEntity): ElementEntity {
        val hm: Map<String, Any> = HashMap()
        return ElementEntity(
            null, "demo", userEntity.username, true, Date(),
            CreatedBy(userEntity.userId), Location(999999.0, 999999.0), hm
        )
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        userType = parent.getItemAtPosition(position).toString()
        //        Log.d("pttt", "onItemSelected: " + userType);
//        Log.d("pttt", "onItemSelected2: " + UserRole.valueOf(userType));
        Toast.makeText(parent.context, userType, Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Util.GALLERY_REQUEST -> if (resultCode == RESULT_OK && data != null) {
                avatar_imageView!!.setImageURI(data.data)
                url = data.data
            }
        }
    }
}