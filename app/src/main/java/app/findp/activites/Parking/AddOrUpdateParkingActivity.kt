package app.findp.activites.Parking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.AddOrUpdateParkingActivity
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.*
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.general.ViewDialog
import app.findp.service.ElementService
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddOrUpdateParkingActivity : AppCompatActivity() {
    private var elementEntity: ElementEntity? = null
    private var addBtn: Button? = null
    private var nameTextBX: TextInputLayout? = null
    private var latTextBX: TextInputLayout? = null
    private var lngTextBX: TextInputLayout? = null
    private var activeBtn: RadioGroup? = null
    private var elementService: ElementService? = null
    private var managerUserEntity: UserEntity? = null
    private var managerElementEntity: ElementEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_update_parking)
        elementEntity = ElementEntity()
        init()
    }

    private fun init() {
        // static method
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient, ELEMENT)
        elementService = retrofit.create(ElementService::class.java)

        // Get USER MANAGER
        managerUserEntity = intent.getSerializableExtra(USER) as UserEntity
        // Get ELEMENT MANAGER
        managerElementEntity = intent.getSerializableExtra(ELEMENT) as ElementEntity
        addBtn = findViewById<View>(R.id.add_parking) as Button
        nameTextBX = findViewById<View>(R.id.parking_name) as TextInputLayout
        latTextBX = findViewById<View>(R.id.parking_lat) as TextInputLayout
        lngTextBX = findViewById<View>(R.id.parking_lng) as TextInputLayout
        activeBtn = findViewById<View>(R.id.radioGroup) as RadioGroup
        addBtn!!.setOnClickListener {
            elementEntity!!.type = DEFAULT_TYPE //2131362021
            val radioText =
                (findViewById<View>(activeBtn!!.checkedRadioButtonId) as RadioButton).text as String
            elementEntity!!.active = if (radioText == "Yes") true else false
            elementEntity!!.name = nameTextBX!!.editText!!.text.toString().trim { it <= ' ' }
            elementEntity!!.location = Location(
                latTextBX!!.editText!!.text.toString().trim { it <= ' ' }
                    .toDouble(), lngTextBX!!.editText!!
                    .text.toString().trim { it <= ' ' }.toDouble()
            )
            val elementAttributes = HashMap<String, Any>()
            elementAttributes["isTaken"] = false
            elementEntity!!.elementAttributes = elementAttributes

            // Before creating parking place we need to check that in the same place their is no other parking
            checkIfParkingIsFree(
                latTextBX!!.editText!!.text.toString().trim { it <= ' ' },
                lngTextBX!!.editText!!.text.toString().trim { it <= ' ' })
        }
    }

    private fun checkIfParkingIsFree(lat: String, lng: String) {
        // GET all children of Manager
        val allParkingWithDistanceZero = elementService!!.getAllElementsByLocation(
            managerElementEntity!!.elementId!!.domain,
            managerUserEntity!!.userId!!.email,
            lat,
            lng,
            DISTANCE,
            10,
            0
        )
        allParkingWithDistanceZero!!.enqueue(object : Callback<Array<ElementEntity?>?> {
            override fun onResponse(
                call: Call<Array<ElementEntity?>?>,
                response: Response<Array<ElementEntity?>?>
            ) {
                Log.i("TAG", "onResponse: " + response.code())
                Toast.makeText(
                    this@AddOrUpdateParkingActivity,
                    "Parking With Distance Zero",
                    Toast.LENGTH_SHORT
                ).show()
                val distanceZero = response.body()!!
                if (distanceZero.size == 0) { // PARK is free
                    val resultIntent = Intent()
                    resultIntent.putExtra(ADD_PARKING, elementEntity)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else { // PARK is not free
                    val alert = ViewDialog()
                    alert.showDialog(
                        this@AddOrUpdateParkingActivity,
                        "** Parking location is already in use **"
                    )
                }
            }

            override fun onFailure(call: Call<Array<ElementEntity?>?>, t: Throwable) {
                Toast.makeText(
                    this@AddOrUpdateParkingActivity,
                    "Failure to find children",
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("TAG", "onFailure: $t")
                return
            }
        })
    }

    companion object {
        private const val ADD_PARKING = "ADD PARKING"
        private const val DEFAULT_TYPE = "park"
        const val ELEMENT = "element"
        const val USER = "user"
        private const val DISTANCE = "0"
    }
}