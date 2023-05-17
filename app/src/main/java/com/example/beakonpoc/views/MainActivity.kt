package com.example.beakonpoc.views

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beakonpoc.R
import com.example.beakonpoc.databinding.ActivityMainBinding
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconEmitter
import com.example.beakonpoc.utils.Utils
import com.example.beakonpoc.viewmodels.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var beaconListAdapter: BeaconListAdapter

    @Inject
    lateinit var beaconEmitter: BeaconEmitter
    private lateinit var binding: ActivityMainBinding
    private lateinit var beaconList: MutableList<BeaconDataModel>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var requiredPermissions = mutableListOf<String>()
    private var permissionsToGrantList = mutableListOf<String>()
    var isScanning = false

    var bluetoothActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (Utils.isBLESupported(this)) {
                    startScanning()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.ble_not_supported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.switch_bluetooth_on),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainActivityViewModel
        binding.lifecycleOwner = this

        requiredPermissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!checkPermissions()) {
                    enableStartScanBtn(false)
                    enableStartEmitBtn(false)
                    binding.errorText.visibility = View.VISIBLE
                    val shouldShowRationale = permissionsToGrantList.any {
                        shouldShowRequestPermissionRationale(it)
                    }
                    if (shouldShowRationale) {
                        showRationale(permissionsToGrantList)
                    }
                } else {
                    enableStartScanBtn(true)
                    enableStartEmitBtn(true)
                    binding.errorText.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.permissions_granted),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        initUI()
    }

    private fun initUI() {
        beaconList = ArrayList()
        isScanning = false
        enableStopScanBtn(false)
        enableStartScanBtn(false)
        enableStopEmitBtn(false)
        enableStartEmitBtn(false)

        if (!checkPermissions()) {
            requestBLEPermissions()
        } else {
            enableStartScanBtn(true)
            enableStartEmitBtn(true)
        }

        binding.startScan.setOnClickListener {
            if (checkBluetoothState()) {
                startScanning()
            } else {
                requestBluetoothEnable()
            }
        }

        binding.stopScan.setOnClickListener {
            stopScanning()
        }

        binding.beaconRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beaconRecyclerView.adapter = beaconListAdapter
        beaconListAdapter.setData(beaconList)

        mainActivityViewModel.beaconLiveData.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                beaconListAdapter.setData(it)
            }
        })

        binding.startEmitter.setOnClickListener {
            if (checkBluetoothState()) {
                //beaconEmitter.startEddystone("0102030405060708090a","000000000002")
                beaconEmitter.startIBeacon("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6", 13, 15)
                toggleEmitBtn()
            } else {
                requestBluetoothEnable()
            }

        }

        binding.stopEmitter.setOnClickListener {
            beaconEmitter.stopAdvertising()
            toggleEmitBtn()
        }

    }

    fun checkPermissions(): Boolean {
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

    fun checkBluetoothState(): Boolean {
        return mainActivityViewModel.isBluetoothEnable()
    }

    fun requestBluetoothEnable() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothActivityResultLauncher.launch(enableIntent)
    }

    private fun startScanning() {
        mainActivityViewModel.startScan()
        isScanning = true
        toggleBtn()
    }

    private fun stopScanning() {
        mainActivityViewModel.stopScan()
        isScanning = false
        toggleBtn()
    }

    private fun enableStartScanBtn(isEnable: Boolean) {
        if (isEnable) {
            binding.startScan.isEnabled = true
            binding.startScan.alpha = 1f
        } else {
            binding.startScan.isEnabled = false
            binding.startScan.alpha = 0.7f
        }
    }

    private fun enableStopScanBtn(isEnable: Boolean) {
        if (isEnable) {
            binding.stopScan.isEnabled = true
            binding.stopScan.alpha = 1f
        } else {
            binding.stopScan.isEnabled = false
            binding.stopScan.alpha = 0.7f
        }
    }

    private fun toggleBtn() {
        if (isScanning) {
            enableStartScanBtn(false)
            enableStopScanBtn(true)
        } else {
            enableStartScanBtn(true)
            enableStopScanBtn(false)
        }
    }

    private fun toggleEmitBtn() {
        if (binding.startEmitter.isEnabled) {
            enableStartEmitBtn(false)
            enableStopEmitBtn(true)
        } else {
            enableStartEmitBtn(true)
            enableStopEmitBtn(false)
        }
    }

    private fun enableStartEmitBtn(isEnable: Boolean) {
        if (isEnable) {
            binding.startEmitter.isEnabled = true
            binding.startEmitter.alpha = 1f
        } else {
            binding.startEmitter.isEnabled = false
            binding.startEmitter.alpha = 0.7f
        }
    }

    private fun enableStopEmitBtn(isEnable: Boolean) {
        if (isEnable) {
            binding.stopEmitter.isEnabled = true
            binding.stopEmitter.alpha = 1f
        } else {
            binding.stopEmitter.isEnabled = false
            binding.stopEmitter.alpha = 0.7f
        }
    }

    private fun showRationale(permissionsToGrantList: MutableList<String>) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.request_permission_grant))
            .setTitle(getString(R.string.permission_required))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                permissionLauncher.launch(permissionsToGrantList.toTypedArray())
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                Toast.makeText(
                    applicationContext,
                    getString(R.string.grant_permission_message),
                    Toast.LENGTH_LONG
                ).show()
            }
            .create()

        dialog.show()
    }
}