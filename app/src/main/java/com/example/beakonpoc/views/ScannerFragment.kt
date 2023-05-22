package com.example.beakonpoc.views

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beakonpoc.R
import com.example.beakonpoc.databinding.FragmentScannerBinding
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.utils.Utils
import com.example.beakonpoc.viewmodels.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private val viewModel: ScannerViewModel by viewModels()

    @Inject
    lateinit var beaconListAdapter: BeaconListAdapter
    private lateinit var binding: FragmentScannerBinding
    private lateinit var beaconList: MutableList<BeaconDataModel>

    private lateinit var bluetoothActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scanner, container, false)
        binding.sacnnerViewModel = viewModel
        binding.lifecycleOwner = this

        bluetoothActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    startScanning()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.switch_bluetooth_on),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        initUI()

        return binding.root
    }

    private fun initUI() {

        enableStartScanBtn(true)
        enableStopScanBtn(false)

        beaconList = ArrayList()

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

        binding.beaconRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.beaconRecyclerView.adapter = beaconListAdapter
        beaconListAdapter.setData(beaconList)

        viewModel.beaconLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                beaconListAdapter.setData(it)
            }
        })

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
        if (binding.startScan.isEnabled) {
            enableStartScanBtn(false)
            enableStopScanBtn(true)
        } else {
            enableStartScanBtn(true)
            enableStopScanBtn(false)
        }
    }

    private fun startScanning() {
        viewModel.startScan()
        toggleBtn()
    }

    private fun stopScanning() {
        viewModel.stopScan()
        toggleBtn()
    }

    private fun checkBluetoothState(): Boolean {
        return Utils.isBluetoothEnabled(context)
    }

    private fun requestBluetoothEnable() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothActivityResultLauncher.launch(enableIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScanning()
    }
}