package app.findp.activites.Parking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.MainActivity
import app.findp.activites.Settings.SettingsActivity
import app.findp.activites.Start.LoginActivity
import app.findp.entities.UserEntity

class MainActivity : AppCompatActivity() {
    private var avatar_imageView: ImageView? = null
    private var logout_btn: ImageView? = null
    private var search_btn: ImageView? = null
    private var user_role: TextView? = null
    private var settings_btn: Button? = null
    private var playerUserEntity: UserEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        listeners()
    }

    private fun init() {
        avatar_imageView = findViewById<View>(R.id.avatar_imageView) as ImageView
        logout_btn = findViewById<View>(R.id.logout_btn) as ImageView
        search_btn = findViewById<View>(R.id.search_button) as ImageView
        user_role = findViewById<View>(R.id.user_name) as TextView
        settings_btn = findViewById<View>(R.id.settings_button) as Button
        playerUserEntity = intent.getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        user_role!!.text = playerUserEntity!!.username
        val context = avatar_imageView!!.context
        val id = context.resources.getIdentifier(
            playerUserEntity!!.avatar,
            "drawable",
            this@MainActivity.packageName
        )
        avatar_imageView!!.setImageResource(id)
    }

    private fun listeners() {
        logout_btn!!.setOnClickListener { v: View? ->
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
        }
        search_btn!!.setOnClickListener { v: View? ->
            val scanningActivityIntent = Intent(this@MainActivity, ScanningActivity::class.java)
            scanningActivityIntent.putExtra(LoginActivity.Companion.USER, playerUserEntity)
            startActivity(scanningActivityIntent)
        }
        settings_btn!!.setOnClickListener { v: View? ->
            val settingActivityIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            settingActivityIntent.putExtra(LoginActivity.Companion.USER, playerUserEntity)
            startActivity(settingActivityIntent)
        }
    }
}