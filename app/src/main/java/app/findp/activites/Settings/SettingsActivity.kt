package app.findp.activites.Settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.MainActivity
import app.findp.activites.Settings.SettingsActivity
import app.findp.activites.Start.LoginActivity
import app.findp.entities.UserEntity
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.service.UserService
import com.google.android.material.textfield.TextInputLayout
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class SettingsActivity constructor() : AppCompatActivity() {
    private var editText_userName: TextInputLayout? = null
    private var back_btn: ImageView? = null
    private var update_btn: Button? = null
    private var userService: UserService? = null
    private var userEntityToUpdate: UserEntity? = null

    // listView
    private var listView: ListView? = null

    //    private String avatar;
    //    private int[] image = {R.drawable.avatar01,R.drawable.avatar02,R.drawable.avatar03,R.drawable.avatar04,R.drawable.avatar05,R.drawable.avatar06};
    //    private String[] headline = {"City employee","Student","Worker","Professor","Teacher","Anonymous"};
    private var avatar: String? = null
    private val image: IntArray = intArrayOf(
        R.drawable.avatar01,
        R.drawable.avatar02,
        R.drawable.avatar03,
        R.drawable.avatar04,
        R.drawable.avatar05,
        R.drawable.avatar06
    )
    private val headline: Array<String> =
        arrayOf("Student", "Worker", "Anonymous", "City employee", "Professor", "Teacher")
    private val drawableName: Array<String> =
        arrayOf("avatar01", "avatar02", "avatar03", "avatar04", "avatar05", "avatar06")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()
        setInfo()
        listeners()
    }

    private fun init() {
        editText_userName = findViewById<View>(R.id.editText_userName) as TextInputLayout?
        back_btn = findViewById<View>(R.id.back_image) as ImageView?
        update_btn = findViewById<View>(R.id.update_btn) as Button?
        userEntityToUpdate =
            getIntent().getSerializableExtra(LoginActivity.Companion.USER) as UserEntity?

        // static method
        val okHttpClient: OkHttpClient = createOkHttpClient()
        val retrofit: Retrofit = createRetrofit(okHttpClient, LoginActivity.Companion.USER)
        userService = retrofit.create(UserService::class.java)

        // ListView
        listView = findViewById(R.id.list_view)
        val list: MutableList<HashMap<String, String?>> = ArrayList()
        for (i in 0..5) {
            val hashMap: HashMap<String, String?> = HashMap()
            hashMap.put("image", Integer.toString(image.get(i)))
            hashMap.put("headline", headline.get(i))
            list.add(hashMap)
        }
        val from: Array<String> = arrayOf("image", "headline")
        val to: IntArray = intArrayOf(R.id.imageViewLogo, R.id.textViewHeadline1)
        val simpleAdapter: SimpleAdapter =
            SimpleAdapter(getBaseContext(), list, R.layout.list_view_activity, from, to)
        listView.setAdapter(simpleAdapter)
        listView.setOnItemClickListener(object : OnItemClickListener {
            public override fun onItemClick(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                view.setSelected(true)
                avatar = drawableName.get(position)
            }
        })
    }

    private fun setInfo() {
        editText_userName!!.getEditText()!!.setText(userEntityToUpdate!!.username)
    }

    private fun listeners() {
        back_btn!!.setOnClickListener(View.OnClickListener({ v: View? ->
            val mainActivityIntent: Intent = Intent(this@SettingsActivity, MainActivity::class.java)
            mainActivityIntent.putExtra("user", userEntityToUpdate)
            startActivity(mainActivityIntent)
            finish()
        }))
        update_btn!!.setOnClickListener(View.OnClickListener({ v: View? -> updateUser() }))
    }

    private fun updateUser() {
        val callUser: Call<Void?>? = userService!!.updateUserDetails(
            userEntityToUpdate!!.userId!!.domain, userEntityToUpdate!!.userId!!.email, UserEntity(
                userEntityToUpdate!!.userId,
                userEntityToUpdate!!.role,
                editText_userName!!.getEditText()!!
                    .getText().toString().trim({ it <= ' ' }),
                avatar
            )
        )
        callUser!!.enqueue(object : Callback<Void?> {
            public override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (!response.isSuccessful()) {
                    Log.i("TAG", "onResponse: " + response.code())
                    Toast.makeText(
                        this@SettingsActivity,
                        "Something wrong: " + response.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else {
                    Log.i("TAG", "onResponse: " + response.code())
                    Toast.makeText(this@SettingsActivity, "Update Player User", Toast.LENGTH_SHORT)
                        .show()
                    userEntityToUpdate!!.avatar = avatar
                    userEntityToUpdate!!.username =
                        editText_userName!!.getEditText()!!.getText().toString().trim({ it <= ' ' })
                    val mainActivityIntent: Intent =
                        Intent(this@SettingsActivity, MainActivity::class.java)
                    mainActivityIntent.putExtra(LoginActivity.Companion.USER, userEntityToUpdate)
                    startActivity(mainActivityIntent)
                    finish()
                }
            }

            public override fun onFailure(call: Call<Void?>, t: Throwable) {
                Toast.makeText(this@SettingsActivity, "Failure create User", Toast.LENGTH_SHORT)
                    .show()
                Log.i("TAG", "onFailure: " + t)
                return
            }
        })
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        val mainActivityIntent: Intent = Intent(this@SettingsActivity, MainActivity::class.java)
        mainActivityIntent.putExtra("user", userEntityToUpdate)
        startActivity(mainActivityIntent)
        finish()
    }
}