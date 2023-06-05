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
import com.example.beakonpoc.databinding.FragmentEmitterBinding
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconType
import com.example.beakonpoc.utils.Utils
import com.example.beakonpoc.viewmodels.EmitterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmitterFragment : Fragment() {

    private val viewModel: EmitterViewModel by viewModels()
    private lateinit var binding: FragmentEmitterBinding
    lateinit var beaconList: MutableList<BeaconDataModel>

    private lateinit var beaconEmitterListAdapter: BeaconEmitterListAdapter

    lateinit var bluetoothActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_emitter, container, false)
        binding.emitterViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        bluetoothActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    initUI()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.switch_bluetooth_on),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        beaconList = ArrayList()

        if (checkBluetoothState()) {
            addBeacons()
        } else {
            requestBluetoothEnable()
        }

        beaconEmitterListAdapter = BeaconEmitterListAdapter { beacon, isStart ->
            if (isStart) {
                when (beacon.type) {
                    BeaconType.IBEACON -> viewModel.startIBeacon(beacon)
                    BeaconType.EDDYSTONE -> viewModel.startEddyStone(beacon)
                }
            } else {
                viewModel.stopEmitter(beacon.uuid!!)
            }
        }

        binding.emitterRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beaconEmitterListAdapter
        }

        beaconEmitterListAdapter.setData(beaconList)
    }

    private fun addBeacons() {
        beaconList.add(
            BeaconDataModel(
                BeaconType.IBEACON,
                "2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6",
                "13",
                "15"
            )
        )
        beaconList.add(
            BeaconDataModel(
                BeaconType.EDDYSTONE,
                "0102030405060708090a000000000002",
                null,
                null,
                null,
                "0102030405060708090a",
                "000000000002"
            )
        )
        beaconList.add(
            BeaconDataModel(
                BeaconType.IBEACON,
                "426C7565-4368-6172-6D42-6561636F6E73",
                "3838",
                "4949"
            )
        )
        beaconList.add(
            BeaconDataModel(
                BeaconType.EDDYSTONE,
                "02C074C1AAF42FD2B822000000000002",
                null,
                null,
                null,
                "02C074C1AAF42FD2B822",
                "000000000001"
            )
        )
    }

    fun checkBluetoothState(): Boolean {
        return Utils.isBluetoothEnabled(requireContext())
    }

    fun requestBluetoothEnable() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothActivityResultLauncher.launch(enableIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconList.forEach {
            viewModel.stopEmitter(it.uuid!!)
        }
    }

}