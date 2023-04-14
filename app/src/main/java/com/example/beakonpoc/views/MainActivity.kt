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
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.beakonpoc.R
import com.example.beakonpoc.databinding.ActivityMainBinding
import com.example.beakonpoc.utils.Utils
import com.example.beakonpoc.viewmodels.MainActivityViewModel
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var requiredPermissions = mutableListOf<String>()
    private var permissionsToGrantList = mutableListOf<String>()
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingPermission")
    private var bluetoothActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (Utils.isBLESupported(bluetoothAdapter, this)) {
                    startScanning()
                } else Toast.makeText(
                    applicationContext,
                    "This device does not support BLE.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainActivityViewModel
        binding.lifecycleOwner = this

        requiredPermissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                if (!checkPermissions()) {
                    binding.startScan.isEnabled = false
                    binding.startScan.alpha = 0.7f
                    binding.errorText.visibility = View.VISIBLE
                    val shouldShowRationale = permissionsToGrantList.any {
                        shouldShowRequestPermissionRationale(it)
                    }
                    if (shouldShowRationale) {
                        showRationale(permissionsToGrantList)
                    }
                } else {
                    binding.startScan.isEnabled = true
                    binding.startScan.alpha = 1f
                    binding.errorText.visibility = View.GONE
                    Toast.makeText(applicationContext, "All permissions granted", Toast.LENGTH_LONG)
                        .show()
                }

            }


        initUI()

    }


    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun initUI() {

        if (!checkPermissions()) {
            requestBLEPermissions()
        }

        toggleBtn(false)

        binding.startScan.setOnClickListener {
            checkBluetoothState()
        }

        binding.stopScan.setOnClickListener {
            mainActivityViewModel.stopScan()
            toggleBtn(false)
        }

    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermissions(): Boolean {
        permissionsToGrantList.clear()
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                permissionsToGrantList.add(permission)
        }
        return permissionsToGrantList.isEmpty()
    }

    private fun requestBLEPermissions() {
        val shouldShowRationale = permissionsToGrantList.any {
            shouldShowRequestPermissionRationale(it)
        }

        if (shouldShowRationale) {
            showRationale(permissionsToGrantList)
        } else {
            if (permissionsToGrantList.isNotEmpty()) {
                permissionLauncher.launch(permissionsToGrantList.toTypedArray())
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun checkBluetoothState() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothActivityResultLauncher.launch(enableIntent)
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        mainActivityViewModel.startScan()
        toggleBtn(true)
    }

    private fun toggleBtn(isScanning: Boolean) {
        if (isScanning) {
            binding.startScan.isEnabled = false
            binding.startScan.alpha = 0.7f
            binding.stopScan.alpha = 1f
            binding.stopScan.isEnabled = true
        } else {
            binding.startScan.isEnabled = true
            binding.stopScan.alpha = 0.7f
            binding.startScan.alpha = 1f
            binding.stopScan.isEnabled = false
        }
    }


    private fun showRationale(permissionsToGrantList: MutableList<String>) {
        val dialog = AlertDialog.Builder(this)
            .setMessage("Please grant permissions to function properly.")
            .setTitle("Permission Required")
            .setPositiveButton("OK") { _, _ ->
                permissionLauncher.launch(permissionsToGrantList.toTypedArray())
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(
                    applicationContext,
                    "Please grant permissions to continue using app",
                    Toast.LENGTH_LONG
                ).show()
            }
            .create()

        dialog.show()
    }
}