package com.example.beakonpoc.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beakonpoc.R
import com.example.beakonpoc.databinding.ItemBeaconBinding
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconType
import com.example.beakonpoc.utils.BeaconDiffUtil

class BeaconEmitterListAdapter(private val onItemClick: (BeaconDataModel, isStart: Boolean) -> Unit) :
    RecyclerView.Adapter<BeaconEmitterListAdapter.BeaconViewHolder>() {

    private var beaconList = emptyList<BeaconDataModel>()

    inner class BeaconViewHolder(val binding: ItemBeaconBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeaconViewHolder {
        val binding = ItemBeaconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeaconViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return beaconList.size
    }

    override fun onBindViewHolder(holder: BeaconViewHolder, position: Int) {

        with(holder) {
            with(beaconList[position]) {
                binding.emitSwitch.visibility = View.VISIBLE
                binding.distance.visibility = View.GONE
                if (this.type == BeaconType.IBEACON) {
                    binding.uuidTv.text = this.uuid
                    binding.majorTv.text = this.major
                    binding.minorTv.text = this.minor
                    binding.bluetoothIcon.setImageResource(R.drawable.ibeacon)
                } else {
                    binding.uuidLayout.visibility = View.GONE
                    binding.majorTag.setText(R.string.namespace)
                    binding.minorTag.setText(R.string.instance)
                    binding.majorTv.text = this.namespace
                    binding.minorTv.text = this.instance
                    binding.bluetoothIcon.setImageResource(R.drawable.eddystone)
                }
            }

            binding.emitSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) onItemClick(beaconList[position], true)
                else onItemClick(beaconList[position], false)
            }

        }

    }

    fun setData(newList: List<BeaconDataModel>) {
        val diffUtil = BeaconDiffUtil(beaconList, newList)
        val diffResults = DiffUtil.calculateDiff(diffUtil, true)
        beaconList = newList
        diffResults.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }
}