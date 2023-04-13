package com.example.beakonpoc.views

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.beakonpoc.viewmodels.MainActivityViewModel
import com.example.beakonpoc.R
import com.example.beakonpoc.utils.Utils
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var distanceTV: TextView
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var uuidTV: TextView
    private lateinit var majorTV: TextView
    private lateinit var minorTV: TextView
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isBluetoothPermissionGranted = false
    private var isBluetoothAdminPermissionGranted = false
    private var isBluetoothConnectPermissionGranted = false
    private var isBluetoothScanPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isFineLocationPermissionGranted = false
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var isScanning = false

    @SuppressLint("MissingPermission")
    private var bluetoothActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if(Utils.isBLESupported(bluetoothAdapter, this)) mainActivityViewModel.startScan()
                else Toast.makeText(applicationContext,"This device does not support BLE.",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    applicationContext,
                    "Please switch Bluetooth ON to use the app",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                isBluetoothPermissionGranted =
                    permissions[Manifest.permission.BLUETOOTH] ?: isBluetoothPermissionGranted
                isBluetoothScanPermissionGranted = permissions[Manifest.permission.BLUETOOTH_SCAN]
                    ?: isBluetoothScanPermissionGranted
                isBluetoothAdminPermissionGranted = permissions[Manifest.permission.BLUETOOTH_ADMIN]
                    ?: isBluetoothAdminPermissionGranted
                isBluetoothConnectPermissionGranted =
                    permissions[Manifest.permission.BLUETOOTH_CONNECT]
                        ?: isBluetoothConnectPermissionGranted
                isCoarseLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION]
                        ?: isCoarseLocationPermissionGranted
                isFineLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                        ?: isFineLocationPermissionGranted

            }

        requestBluetoothScanPermission()
        initUI()

    }


    @SuppressLint("MissingPermission")
    private fun initUI() {

        distanceTV = findViewById(R.id.distance)
        startBtn = findViewById(R.id.startScan)
        stopBtn = findViewById(R.id.stopScan)
        uuidTV = findViewById(R.id.uuidTv)
        majorTV = findViewById(R.id.majorTv)
        minorTV = findViewById(R.id.minorTv)

        toggleBtn(isScanning)

        mainActivityViewModel.updateRSSI().observe(this, androidx.lifecycle.Observer {
            if(it == null){
                distanceTV.text = "00"
                uuidTV.text = "00"
                majorTV.text = "00"
                minorTV.text = "00"
            }else{
                distanceTV.text = it.rssi
                uuidTV.text = it.uuid
                majorTV.text = it.major
                minorTV.text = it.minor
            }
        })

        startBtn.setOnClickListener {
            initScanner()
            mainActivityViewModel.startScan()
            isScanning = true
            toggleBtn(isScanning)
        }

        stopBtn.setOnClickListener {
            mainActivityViewModel.stopScan()
            isScanning = false
            toggleBtn(isScanning)
        }

    }


    @SuppressLint("MissingPermission")
    fun initScanner() {
        Log.d("BLE Logs", "initScanner")
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        checkBluetoothState(bluetoothAdapter)
    }


    @SuppressLint("MissingPermission")
    private fun checkBluetoothState(bluetoothAdapter: BluetoothAdapter) {
        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothActivityResultLauncher.launch(enableIntent)
        }else{
            mainActivityViewModel.startScan()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothScanPermission() {
        val permissionsList = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isBluetoothScanPermissionGranted = false
            permissionsList.add(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            isBluetoothScanPermissionGranted = true
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isCoarseLocationPermissionGranted = false
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            isCoarseLocationPermissionGranted = true
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isFineLocationPermissionGranted = false
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            isFineLocationPermissionGranted = true
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isBluetoothConnectPermissionGranted = false
            permissionsList.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            isBluetoothConnectPermissionGranted = true
        }

        if (permissionsList.isNotEmpty()) {
            permissionLauncher.launch(permissionsList.toTypedArray())
        }


    }

    private fun toggleBtn(isScanning: Boolean){
        if (isScanning){
            startBtn.isEnabled = false
            startBtn.alpha = 0.7f
            stopBtn.alpha = 1f
            stopBtn.isEnabled = true
        }else{
            startBtn.isEnabled = true
            stopBtn.alpha = 0.7f
            startBtn.alpha = 1f
            stopBtn.isEnabled = false
        }
    }

}