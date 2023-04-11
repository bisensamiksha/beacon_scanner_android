package com.example.beakonpoc

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewMode: MainActivityViewMode
    private lateinit var distanceTV: TextView
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isBluetoothPermissionGranted = false
    private var isBluetoothAdminPermissionGranted = false
    private var isBluetoothConnectPermissionGranted = false
    private var isBluetoothScanPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isFineLocationPermissionGranted = false
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    private var bluetoothActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if(Utils.isBLESupported(bluetoothAdapter, this)) bluetoothAdapter.bluetoothLeScanner.startScan(callback)
                else Toast.makeText(applicationContext,"This device does not support BLE.",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    applicationContext,
                    "Please switch Bluetooth ON to use the app",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private var callback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("BLE Logs", "Success ${result.toString()}")

            result?.let { scanResult ->
                val payload = scanResult.scanRecord?.manufacturerSpecificData?.get(76)
                if (payload != null && payload.size >= 23) {
                    val uuidBytes =
                        ByteBuffer.wrap(payload, 2, 18).order(ByteOrder.LITTLE_ENDIAN).long
                    val major =
                        ((payload[18].toInt() and 0xff) shl 8) or (payload[19].toInt() and 0xff)
                    val minor =
                        ((payload[20].toInt() and 0xff) shl 8) or (payload[21].toInt() and 0xff)
                    val txPowerBeacon = payload[22].toInt()

                    Log.d(
                        "BLE Logs",
                        "UUID: $uuidBytes, Major: $major, Minor: $minor, TxPower: $txPowerBeacon"
                    )

                    val rssi = scanResult.rssi
                    runOnUiThread {
                        distanceTV.text = rssi.toString()
                    }

                }
                //for Eddystone
                //val manufactuerIdGoogle = scanResult.scanRecord?.manufacturerSpecificData.get(0x0118)
            }

        }

        override fun onScanFailed(errorCode: Int) {
            Toast.makeText(applicationContext, "Scanning Failed", Toast.LENGTH_LONG).show()
            Log.d("BLE Logs", "Failed $errorCode")
            super.onScanFailed(errorCode)

        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewMode = ViewModelProvider(this).get(MainActivityViewMode::class.java)

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

        mainActivityViewMode.updateDistance().observe(this, Observer {
            distanceTV.text = it.toString()
        })

        startBtn = findViewById(R.id.startScan)
        stopBtn = findViewById(R.id.stopScan)

        startBtn.setOnClickListener {
            initScanner()
        }

        stopBtn.setOnClickListener {
            bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
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
            bluetoothAdapter.bluetoothLeScanner.startScan(callback)
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

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        bluetoothAdapter.bluetoothLeScanner.stopScan(callback)
    }


}