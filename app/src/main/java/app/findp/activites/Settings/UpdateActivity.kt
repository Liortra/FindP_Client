package app.findp.activites.Settings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Settings.UpdateActivity
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.*
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.service.ElementService
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateActivity() : AppCompatActivity() {
    private var parkToUpdate: ElementEntity? = null
    private var elementService: ElementService? = null
    private var nameTextBX: TextInputEditText? = null
    private var latTextBX: TextInputEditText? = null
    private var lngTextBX: TextInputEditText? = null
    private var active: RadioButton? = null
    private var notActive: RadioButton? = null
    private var updateParkingBtn: Button? = null
    private var managerElementEntity: ElementEntity? = null
    private var userElementEntity: UserEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        nameTextBX = findViewById<View>(R.id.update_name) as TextInputEditText
        latTextBX = findViewById<View>(R.id.update_lat) as TextInputEditText
        lngTextBX = findViewById<View>(R.id.update_lng) as TextInputEditText
        active = findViewById<View>(R.id.update_active) as RadioButton
        notActive = findViewById<View>(R.id.update_not_active) as RadioButton
        updateParkingBtn = findViewById<View>(R.id.update_parking_btn) as Button
        managerElementEntity = intent.getSerializableExtra(ELEMENT) as ElementEntity
        userElementEntity = intent.getSerializableExtra(USER) as UserEntity
        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient, ELEMENT)
        elementService = retrofit.create(ElementService::class.java)
        // Get ELEMENT parking
        parkToUpdate = intent.getSerializableExtra(PARK_ELEMENT_TO_UPDATE) as ElementEntity
        initData()
    }

    private fun initData() {
        // put all details in the correct fields
        nameTextBX!!.setText(parkToUpdate!!.name)
        latTextBX!!.setText(parkToUpdate!!.location!!.lat.toString())
        lngTextBX!!.setText(parkToUpdate!!.location!!.lng.toString())
        if (parkToUpdate!!.active == true) {
            active!!.isChecked = true
            notActive!!.isChecked = false
        } else {
            notActive!!.isChecked = true
            active!!.isChecked = false
        }
        updateParkingBtn!!.setOnClickListener(View.OnClickListener {
            updateParking()

//                ElementEntity newElementAfterChange = new ElementEntity(parkToUpdate.getElementId(),
//                        parkToUpdate.getType(),
//                        nameTextBX.getText().toString(),
//                        active.isChecked() == true ? true:false,
//                        parkToUpdate.getCreatedTimestamp(),
//                        parkToUpdate.getCreatedBy(),
//                        new Location( Double.parseDouble(latTextBX.getText().toString()),
//                                Double.parseDouble(lngTextBX.getText().toString())),
//                        new HashMap<>());
            parkToUpdate!!.name = nameTextBX!!.text.toString().trim { it <= ' ' }
            parkToUpdate!!.active = if (active!!.isChecked == true) true else false
            //                String st = elementLocation.getText().toString().trim().replace("(","");
//                st = st.replace(")","");
//                String[] latlng = st.split(",");
            parkToUpdate!!.location = Location(
                latTextBX!!.text.toString().toDouble(),
                lngTextBX!!.text.toString().toDouble()
            )
            // update parking place in City(Manager)
            val updateExistingPark = elementService!!.updateElement(
                managerElementEntity!!.elementId!!.domain,
                userElementEntity!!.userId!!.email,
                parkToUpdate!!.elementId!!.domain,
                parkToUpdate!!.elementId!!.id,
                parkToUpdate
            )
            updateExistingPark!!.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    Log.i("TAG", "onResponse: " + response.code())
                    Toast.makeText(
                        this@UpdateActivity,
                        "** Success on UPDATE **",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Toast.makeText(
                        this@UpdateActivity,
                        "** Failure to UPDATE **",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onFailure: $t")
                    return
                }
            })
        })
    }

    private fun updateParking() {
        parkToUpdate!!.name = nameTextBX!!.text.toString().trim { it <= ' ' }
        parkToUpdate!!.location =
            Location(latTextBX!!.text.toString().toDouble(), lngTextBX!!.text.toString().toDouble())
        parkToUpdate!!.active = (active?.equals( "Yes"))
    }

    companion object {
        val PARK_ELEMENT_TO_UPDATE = "park element to update"
        val ELEMENT = "element"
        val USER = "user"
    }
}