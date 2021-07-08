package app.findp.activites.Map

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.FragmentActivity
import app.findp.R
import app.findp.activites.Map.MapActivity
import app.findp.activites.Parking.ResultsActivity
import app.findp.activites.Parking.ScanningActivity
import app.findp.data.ParkingData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : FragmentActivity(), OnMapReadyCallback {
    var mapAPI: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var back_btn: ImageView? = null
    lateinit var data: Array<ParkingData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        back_btn = findViewById<View>(R.id.back_image_2) as ImageView
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_API) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        data = intent.getSerializableExtra("PARKING_DATA_CLASS") as Array<ParkingData>
        back_btn!!.setOnClickListener {
            val backToResultActivityIntent = Intent(this@MapActivity, ResultsActivity::class.java)
            startActivity(backToResultActivityIntent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapAPI = googleMap
        for (i in data.indices) {
            val location = LatLng(data[i].location.lat!!, data[i].location.lng!!)
            mapAPI!!.addMarker(MarkerOptions().position(location).title("address" + (i + 1)))
        }
        mapAPI!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    ScanningActivity.Companion.userLocationMockup!!.lat!!,
                    ScanningActivity.Companion.userLocationMockup!!.lng!!
                ), 16f
            )
        )
    }
}