package com.example.beakonpoc.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.beakonpoc.models.BeaconDataModel

class BeaconDiffUtil(
    private val oldList: List<BeaconDataModel>,
    private val newList: List<BeaconDataModel>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].uuid == newList[newItemPosition].uuid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].rssi == newList[newItemPosition].rssi
    }

}