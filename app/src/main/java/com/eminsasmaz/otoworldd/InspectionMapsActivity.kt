
package com.eminsasmaz.otoworldd

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.eminsasmaz.otoworldd.databinding.ActivityInspectionMapsBinding
import com.eminsasmaz.otoworldd.databinding.ActivityMapsBinding
import com.eminsasmaz.otoworldd.model.CarparkModel
import com.eminsasmaz.otoworldd.model.InspectionModel
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class InspectionMapsActivity : AppCompatActivity(), OnMapReadyCallback,OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityInspectionMapsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolean: Boolean?=null
    private var selectedLatitude:Double?=null
    private var selectedLongitude:Double?=null
    private lateinit var inspectionArrayList:ArrayList<InspectionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInspectionMapsBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        db= Firebase.firestore
        auth= Firebase.auth

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()

        sharedPreferences=this.getSharedPreferences("com.eminsasmaz.otoworldd", MODE_PRIVATE)
        trackBoolean=false
        selectedLatitude=0.0
        selectedLongitude=0.0

        inspectionArrayList=ArrayList<InspectionModel>()
    }
    private fun getData(){

        db.collection("InspectionFirms").addSnapshotListener { value, error ->

            if(error!=null){
                Toast.makeText(this,"Error occured",Toast.LENGTH_LONG).show()
                Log.e("InspectionMapsActivity","Error occured",error)
                return@addSnapshotListener
            }else{
                if(value!=null){
                    if(!value.isEmpty){
                        val documents= value.documents

                        for (document in documents) {
                            val inspectionAdress = document.getString("inspectionAdress") ?: "No Address"
                            val inspectionContact = document.getString("inspectionContact") ?: "No Contact"
                            val inspectionFirmName = document.getString("inspectionFirmName") ?: "No Firm Name"
                            val inspectionImageUrl = document.getString("inspectionImageUrl") ?: "No Image"
                            val location = document.getGeoPoint("location")
                            val inspectionPriceList = document.getString("inspectionPriceList") ?: "No Price List"
                            val inspectionStatus = document.getBoolean("inspectionStatus") ?: false
                            val inspectionWorkingHours = document.getString("inspectionWorkingHours") ?: "No Working Hours"

                            if (location != null) {
                                val inspectionList = InspectionModel(
                                    inspectionAdress,inspectionContact,inspectionFirmName,inspectionImageUrl,location,
                                    location.latitude,location.longitude,inspectionPriceList,inspectionStatus,inspectionWorkingHours
                                )
                                println(inspectionAdress)
                                inspectionArrayList.add(inspectionList)
                                mMap.addMarker(MarkerOptions().title(inspectionFirmName).position(LatLng(location.latitude, location.longitude)))
                            }

                        }

                    }
                }
            }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        getData()
        locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener= object : LocationListener{
            override fun onLocationChanged(location: Location) {
                trackBoolean=sharedPreferences.getBoolean("trackBoolean",false)
                if(trackBoolean!!){
                    val userLocation=LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    sharedPreferences.edit().putBoolean("trackBoolean",true).apply()
                }

            }
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){

                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                //requset permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            //permission granted
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            val lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation!=null){
                val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
            }
            mMap.isMyLocationEnabled=true
        }

        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

    }
    private fun registerLauncher(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){

                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastLocation!=null){
                        val lastUserLocation=LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }
                    mMap.isMyLocationEnabled=true
                }

            }else{
                //permission denied
                Toast.makeText(this,"Permission needed!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))

        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude
    }
}
