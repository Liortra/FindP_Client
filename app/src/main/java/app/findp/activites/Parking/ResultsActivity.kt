package app.findp.activites.Parking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Map.MapActivity
import app.findp.activites.Parking.ResultsActivity
import app.findp.activites.Start.LoginActivity
import app.findp.data.ParkingData
import app.findp.entities.UserEntity

class ResultsActivity : AppCompatActivity() {
    private var mTableLayout: TableLayout? = null

    //ProgressDialog mProgressBar;
    //    private ElementEntity[] data;
    private lateinit var data: Array<ParkingData>
    private var playerUserEntity: UserEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        //mProgressBar = new ProgressDialog(this);
        data =
            intent.getSerializableExtra(ScanningActivity.Companion.ELEMENT_ENTITIES) as Array<ParkingData>
        playerUserEntity = intent.getSerializableExtra(LoginActivity.Companion.USER) as UserEntity
        // setup the table
        createTableLayout()
        startLoadData()
    }

    private fun createTableLayout() {
        mTableLayout = findViewById<View>(R.id.tableInvoices) as TableLayout
        mTableLayout!!.isStretchAllColumns = true
    }

    private fun createTableLayoutParams(): TableLayout.LayoutParams {
        val leftRowMargin = 0
        val topRowMargin = 0
        val rightRowMargin = 0
        val bottomRowMargin = 0
        val trParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin)
        return trParams
    }

    fun startLoadData() {
        loadData()
    }

    fun loadData() {
        val numOfRows = data.size
        var textSpacer: TextView? = null

        //Clean the table before adding all rows UI.
        mTableLayout!!.removeAllViews()

        // -1 means heading row
        for (i in -1 until numOfRows) {
            var parkingData: ParkingData? = null
            if (i > -1) {
                parkingData = data[i]
            } else {
                textSpacer = TextView(this)
                textSpacer.text = ""
            }

            //create data columns
            val placesTV = createCellUI(i, "Place", parkingData)
            val distanceTV = createCellUI(i, "Distance", parkingData)


            //Create tableLayout params
            val trParams = createTableLayoutParams()

            //Create row UI
            val tr = createRowUI(placesTV, distanceTV, i, trParams)
            if (i > -1) {
                tr.tag = i
                tr.setOnClickListener { v ->
                    val tr = v as TableRow
                    val resultsActivityIntent =
                        Intent(this@ResultsActivity, ParkingActivity::class.java)
                    resultsActivityIntent.putExtra(PARKING_DATA, data[tr.tag as Int])
                    resultsActivityIntent.putExtra(LoginActivity.Companion.USER, playerUserEntity)
                    startActivity(resultsActivityIntent)
                }
            }
            mTableLayout!!.addView(tr, trParams)
        }
    }

    private fun ChooseParkingPlace() {
        //TODO- Moving to success parking place
    }

    private fun createRowUI(
        placesTV: TextView,
        distanceTV: TextView,
        index: Int,
        trParams: TableLayout.LayoutParams
    ): TableRow {
        val tr = TableRow(this)
        tr.id = index + 1
        tr.setPadding(0, 0, 0, 0)
        tr.layoutParams = trParams
        tr.addView(placesTV)
        tr.addView(distanceTV)
        //tr.addView(layAmounts);
        return tr
    }

    private fun createCellUI(index: Int, headerName: String, rowData: ParkingData?): TextView {
        val textSize = resources.getDimension(R.dimen.font_size_verysmall).toInt()
        val smallTextSize = resources.getDimension(R.dimen.font_size_small).toInt()
        val tv = TextView(this)
        tv.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tv.gravity = Gravity.CENTER
        tv.setPadding(5, 15, 0, 15)
        if (index == -1) {
            //Header Column.
            tv.text = headerName
            tv.setBackgroundColor(Color.parseColor("#f0f0f0"))
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
        } else {
            //Data Column
            tv.setBackgroundColor(Color.parseColor("#f8f8f8"))
            if (headerName == "Place") {
                tv.text = rowData!!.name
            } else {
                tv.text = "" + rowData!!.distance + " KM"
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        return tv
    }

    fun goToMap(view: View?) {
        val mapActivityIntent = Intent(this@ResultsActivity, MapActivity::class.java)
        mapActivityIntent.putExtra("PARKING_DATA_CLASS", data)
        startActivity(mapActivityIntent)
    }

    companion object {
        var PARKING_DATA = "parkingData"
    }
}