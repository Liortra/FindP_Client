package app.findp.activites.Settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import app.findp.R
import app.findp.activites.Parking.AddOrUpdateParkingActivity
import app.findp.activites.Settings.ManagerActivity
import app.findp.activites.Start.LoginActivity
import app.findp.entities.ElementEntity
import app.findp.entities.UserEntity
import app.findp.entities.utils.ElementId
import app.findp.entities.utils.Util.createOkHttpClient
import app.findp.entities.utils.Util.createRetrofit
import app.findp.service.ElementService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ManagerActivity constructor() : AppCompatActivity() {
    private var managerView_name: TextView? = null
    private var addParkingBtn: Button? = null
    private var updateParkingBtn: Button? = null
    private var editChoice: Button? = null
    private var logout_btn: ImageView? = null
    var children: Array<ElementEntity?>
    private var managerUserEntity: UserEntity? = null
    private var managerElementEntity: ElementEntity? = null
    private var elementService: ElementService? = null
    private var parkingElement: ElementEntity? = null
    private var parkToUpdate: ElementEntity? = null
    private var mTableLayout: TableLayout? = null
    private var elementId: MutableMap<Int, ElementId?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        init()
        allChildrenOfManager
    }

    private fun init() {
        // static method
        val okHttpClient: OkHttpClient = createOkHttpClient()
        val retrofit: Retrofit = createRetrofit(okHttpClient, ELEMENT)
        elementService = retrofit.create(ElementService::class.java)
        elementId = HashMap()

        // Get USER MANAGER
        managerUserEntity = getIntent().getSerializableExtra(USER) as UserEntity?
        // Get ELEMENT MANAGER
        managerElementEntity = getIntent().getSerializableExtra(ELEMENT) as ElementEntity?
        managerView_name = findViewById<View>(R.id.manager_name) as TextView?
        managerView_name!!.setText(EMPTY)
        managerView_name!!.setText(managerUserEntity!!.username)
        parkingElement = ElementEntity()
        parkToUpdate = ElementEntity()
        logout_btn = findViewById<View>(R.id.logout_btn) as ImageView?
        addParkingBtn = findViewById<View>(R.id.add_parking) as Button?
        updateParkingBtn = findViewById<View>(R.id.update_parking) as Button?
        updateParkingBtn!!.setTextColor(Color.parseColor("#1F000000"))
        editChoice = findViewById<View>(R.id.edit_choice) as Button?
        editChoice!!.setVisibility(View.INVISIBLE)
        addParkingBtn!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                // Jump to ADD parking Activity
                val addParking: Intent =
                    Intent(this@ManagerActivity, AddOrUpdateParkingActivity::class.java)
                addParking.putExtra(USER, managerUserEntity)
                addParking.putExtra(ELEMENT, managerElementEntity)
                startActivityForResult(addParking, SUCCESSFULLY_BACK)
            }
        })
        updateParkingBtn!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                // Jump to UPDATE parking Activity
                val updateParking: Intent = Intent(this@ManagerActivity, UpdateActivity::class.java)
                updateParking.putExtra(PARK_ELEMENT_TO_UPDATE, parkToUpdate)
                updateParking.putExtra(ELEMENT, managerElementEntity)
                updateParking.putExtra(USER, managerUserEntity)
                startActivityForResult(updateParking, SUCCESSFULLY_BACK2)
            }
        })
        editChoice!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                updateParkingBtn!!.setEnabled(false)
                updateParkingBtn!!.setTextColor(Color.parseColor("#1F000000"))
                arrangeAllChildrenInScrollView()
                v.setVisibility(View.INVISIBLE)
                mTableLayout!!.setEnabled(true)
                mTableLayout!!.setClickable(true)
            }
        })
        logout_btn!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                startActivity(Intent(getApplicationContext(), LoginActivity::class.java))
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            (SUCCESSFULLY_BACK) -> {
                if (resultCode == RESULT_OK) {
                    // Bring back CHILD that created
                    parkingElement = data!!.getSerializableExtra(ADD_PARKING) as ElementEntity?
                    val createParkingElement: Call<ElementEntity?>? =
                        elementService!!.createNewElement(
                            managerUserEntity!!.userId!!.domain,
                            managerUserEntity!!.userId!!.email,
                            parkingElement
                        )
                    createParkingElement!!.enqueue(object : Callback<ElementEntity> {
                        public override fun onResponse(
                            call: Call<ElementEntity>,
                            response: Response<ElementEntity>
                        ) {
                            Log.i("TAG", "onResponse: " + response.code())
                            Toast.makeText(
                                this@ManagerActivity,
                                "*** Success with creating parking place ***",
                                Toast.LENGTH_SHORT
                            ).show()

                            // bind parking place to City(Manager)
                            val bindChildToParent: Call<Void?>? =
                                elementService!!.bindParentElementToChildElement(
                                    managerElementEntity!!.elementId!!.domain,
                                    managerUserEntity!!.userId!!.email,
                                    managerElementEntity!!.elementId!!.domain,
                                    managerElementEntity!!.elementId!!.id,
                                    ElementId(
                                        response.body()!!.elementId!!.domain,
                                        response.body()!!.elementId!!.id
                                    )
                                )
                            bindChildToParent!!.enqueue(object : Callback<Void?> {
                                public override fun onResponse(
                                    call: Call<Void?>,
                                    response: Response<Void?>
                                ) {
                                    Log.i("TAG", "onResponse: " + response.code())
                                    Toast.makeText(
                                        this@ManagerActivity,
                                        "*** Success on binding CHILD to PARENT ***",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    allChildrenOfManager
                                }

                                public override fun onFailure(call: Call<Void?>, t: Throwable) {
                                    Toast.makeText(
                                        this@ManagerActivity,
                                        "*** Failure to binding CHILD to PARENT ***",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.i("TAG", "onFailure: " + t)
                                    return
                                }
                            })
                        }

                        public override fun onFailure(call: Call<ElementEntity>, t: Throwable) {
                            Toast.makeText(
                                this@ManagerActivity,
                                "*** Failure with creating parking place ***",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("TAG", "onFailure: " + t)
                            return
                        }
                    })
                }
            }
            SUCCESSFULLY_BACK2 -> {
                allChildrenOfManager
                updateParkingBtn!!.setEnabled(false)
                updateParkingBtn!!.setTextColor(Color.parseColor("#1F000000"))
                editChoice!!.setVisibility(View.INVISIBLE)
                mTableLayout!!.setEnabled(true)
                mTableLayout!!.setClickable(true)
            }
        }
    }// GET ARRAY of all children of Manager

    // GET all children of Manager
    private val allChildrenOfManager: Unit
        private get() {
            // GET all children of Manager
            val allChildrenElements: Call<Array<ElementEntity?>?>? =
                elementService!!.getAllChildrenElements(
                    managerElementEntity!!.elementId!!.domain,
                    managerUserEntity!!.userId!!.email,
                    managerElementEntity!!.elementId!!.domain,
                    managerElementEntity!!.elementId!!.id
                )
            allChildrenElements!!.enqueue(object : Callback<Array<ElementEntity?>> {
                public override fun onResponse(
                    call: Call<Array<ElementEntity?>>,
                    response: Response<Array<ElementEntity?>>
                ) {
                    Log.i("TAG", "onResponse: " + response.code())
                    Toast.makeText(
                        this@ManagerActivity,
                        "Success on fetching all childrens of parent",
                        Toast.LENGTH_SHORT
                    ).show()
                    // GET ARRAY of all children of Manager
                    children = (response.body())!!
                    arrangeAllChildrenInScrollView()
                }

                public override fun onFailure(call: Call<Array<ElementEntity?>>, t: Throwable) {
                    Toast.makeText(
                        this@ManagerActivity,
                        "Failure to find children",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("TAG", "onFailure: " + t)
                    return
                }
            })
        }

    // setup the table
    private fun arrangeAllChildrenInScrollView() {
        createTableLayout()
        startLoadData()
    }

    private fun createTableLayout() {
        mTableLayout = findViewById<View>(R.id.tableInvoices) as TableLayout?
        mTableLayout!!.setStretchAllColumns(true)
    }

    private fun createTableLayoutParams(): TableLayout.LayoutParams {
        val trParams: TableLayout.LayoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.MATCH_PARENT,
            TableLayout.LayoutParams.WRAP_CONTENT
        )
        trParams.setMargins(0, 0, 0, 0)
        return trParams
    }

    fun startLoadData() {
        val numOfRows: Int = children.size
        var textSpacer: TextView? = null
        //Clean the table before adding all rows UI.
        mTableLayout!!.removeAllViews()

        // -1 means heading row
        for (i in -1 until numOfRows) {
            var elementEntity: ElementEntity? = null
            if (i > -1) {
                elementEntity = children.get(i)
                elementId!!.put(i, elementEntity!!.elementId)
            } else {
                textSpacer = TextView(this)
                textSpacer.setText("")
            }

            //create data columns
            val elementName: TextView = createCellUI(i, "Name", elementEntity)
            val elementActive: TextView = createCellUI(i, "Active", elementEntity)
            val elementTimeStamp: TextView = createCellUI(i, "Created Time", elementEntity)
            val elementLocation: TextView = createCellUI(i, "Location", elementEntity)

            //Create tableLayout params
            val trParams: TableLayout.LayoutParams = createTableLayoutParams()

            //Create row UI
            val tr: TableRow = createRowUI(
                elementName,
                elementActive,
                elementTimeStamp,
                elementLocation,
                i,
                trParams
            )
            mTableLayout!!.addView(tr, trParams)
            if (i > -1) // if it is not the header
                listenersOfTextViews(
                    i,
                    mTableLayout,
                    tr,
                    elementName,
                    elementActive,
                    elementTimeStamp,
                    elementLocation
                )
        }
    }

    private fun listenersOfTextViews(
        indexOfElement: Int,
        mTableLayout: TableLayout?,
        tr: TableRow,
        elementName: TextView,
        elementActive: TextView,
        elementTimeStamp: TextView,
        elementLocation: TextView
    ) {
        tr.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if (!updateParkingBtn!!.isEnabled()) {
                    updateParkingBtn!!.setEnabled(true)
                    updateParkingBtn!!.setTextColor(Color.parseColor("#FBB03B"))
                    tr.setBackgroundColor(Color.parseColor("#FBB03B"))
                    mTableLayout!!.setEnabled(false)
                    mTableLayout.setClickable(false)
                    editChoice!!.setVisibility(View.VISIBLE)

                    // GET Specific ELEMENT
                    val specElement: Call<ElementEntity?>? = elementService!!.getElement(
                        managerElementEntity!!.elementId!!.domain,
                        managerUserEntity!!.userId!!.email,
                        elementId!!.get(indexOfElement)!!.domain,
                        elementId!!.get(indexOfElement)!!.id
                    )
                    specElement!!.enqueue(object : Callback<ElementEntity> {
                        public override fun onResponse(
                            call: Call<ElementEntity>,
                            response: Response<ElementEntity>
                        ) {
                            Log.i("TAG", "onResponse: " + response.code())
                            Toast.makeText(
                                this@ManagerActivity,
                                "Success on fetching specific element",
                                Toast.LENGTH_SHORT
                            ).show()
                            // GET ARRAY of all children of Manager
                            Log.d("simba", response.body().toString())
                            parkToUpdate = response.body()
                            //                            parkToUpdate.setName(elementName.getText().toString().trim());
//                            parkToUpdate.setActive(elementActive.getText().toString().trim() == "yes" ?true:false);
//                            String st = elementLocation.getText().toString().trim().replace("(","");
//                            st = st.replace(")","");
//                            String[] latlng = st.split(",");
//                            parkToUpdate.setLocation(new Location(
//                                    Double.parseDouble(latlng[0]),
//                                    Double.parseDouble(latlng[1])));
                        }

                        public override fun onFailure(call: Call<ElementEntity>, t: Throwable) {
                            Toast.makeText(
                                this@ManagerActivity,
                                "Failure to fetching specific element",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.i("TAG", "onFailure: " + t)
                            return
                        }
                    })
                }
            }
        })
    }

    private fun createRowUI(
        elementName: TextView,
        elementActive: TextView,
        elementTimeStamp: TextView,
        elementLocation: TextView,
        index: Int,
        trParams: TableLayout.LayoutParams
    ): TableRow {
        val tr: TableRow = TableRow(this)
        tr.setId(index + 1)
        tr.setPadding(0, 0, 0, 0)
        tr.setLayoutParams(trParams)
        tr.addView(elementName)
        tr.addView(elementActive)
        tr.addView(elementTimeStamp)
        tr.addView(elementLocation)
        return tr
    }

    private fun createCellUI(index: Int, headerName: String, rowData: ElementEntity?): TextView {
        val textSize: Int = getResources().getDimension(R.dimen.font_size_small).toInt()
        val smallTextSize: Int = getResources().getDimension(R.dimen.font_size_small).toInt()
        val tv: TextView = TextView(this)
        tv.setLayoutParams(
            TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
        )
        tv.setGravity(Gravity.CENTER)
        tv.setPadding(0, 0, 0, 0)
        if (index == -1) { // Header Column.
            tv.setText(headerName)
            tv.setBackgroundColor(Color.parseColor("#29ABE2"))
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize.toFloat())
        } else { // Data Column
            if ((headerName == "Name")) {
                tv.setText(rowData!!.name)
            } else {
                if ((headerName == "Active")) tv.setText(rowData!!.active.toString()) else {
                    if ((headerName == "Created Time")) tv.setText(rowData!!.createdTimestamp.toString()) else tv.setText(
                        "(" + rowData!!.location!!.lat + ", " + rowData.location!!.lng + ")"
                    )
                }
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
        tv.setTextColor(Color.BLACK)
        return tv
    }

    companion object {
        val ELEMENT: String = "element"
        val PARK_ELEMENT_TO_UPDATE: String = "park element to update"
        val USER: String = "user"
        private val SUCCESSFULLY_BACK: Int = 1
        private val SUCCESSFULLY_BACK2: Int = 2
        private val ADD_PARKING: String = "ADD PARKING"
        private val EMPTY: String = ""
    }
}