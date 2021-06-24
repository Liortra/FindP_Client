package app.findp.activites.Parking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.SorryActivity
import app.findp.activites.Start.LoginActivity
import app.findp.entities.UserEntity

class SorryActivity : AppCompatActivity(), View.OnClickListener {
    private var search_again: Button? = null
    private var playerUserEntity: UserEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sorry)
        search_again = findViewById<View>(R.id.search_again_btn) as Button
        playerUserEntity = intent.getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        findViewById<View>(R.id.search_again_btn).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.search_again_btn -> goToMainActivity()
            else -> {
            }
        }
    }

    private fun goToMainActivity() {
        val sorryActivityIntent = Intent(this@SorryActivity, MainActivity::class.java)
        sorryActivityIntent.putExtra(LoginActivity.Companion.USER, playerUserEntity)
        startActivity(sorryActivityIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToMainActivity()
    }
}