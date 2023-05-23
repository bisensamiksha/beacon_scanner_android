package com.example.beakonpoc.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.beakonpoc.R
import com.example.beakonpoc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var requiredPermissions = mutableListOf<String>()
    private var permissionsToGrantList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
                    binding.fragmentContainerView.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                    val shouldShowRationale = permissionsToGrantList.any {
                        shouldShowRequestPermissionRationale(it)
                    }
                    if (shouldShowRationale) {
                        showRationale(permissionsToGrantList)
                    }
                } else {
                    binding.fragmentContainerView.visibility = View.VISIBLE
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

        setBottomNavigation()

        if (!checkPermissions()) {
            requestBLEPermissions()
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            commit()
        }

    private fun setBottomNavigation() {
        val scannerFragment = ScannerFragment()
        val emitterFragment = EmitterFragment()

        setCurrentFragment(scannerFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.scannerFragment -> setCurrentFragment(scannerFragment)
                R.id.emitterFragment -> setCurrentFragment(emitterFragment)
            }
            true
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