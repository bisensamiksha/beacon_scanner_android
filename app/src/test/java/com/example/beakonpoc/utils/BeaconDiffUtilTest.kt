package com.example.beakonpoc.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beakonpoc.models.BeaconDataModel
import com.example.beakonpoc.models.BeaconType
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.junit.jupiter.api.Test

@RunWith(AndroidJUnit4::class)
class BeaconDiffUtilTest {

    @Test
    fun test_getOldListSize() {
        val oldList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "1"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "2")
        )
        val newList = emptyList<BeaconDataModel>()
        val diffUtil = BeaconDiffUtil(oldList, newList)

        val result = diffUtil.oldListSize

        assertEquals(oldList.size, result)
    }

    @Test
    fun test_getNewListSize() {
        val oldList = emptyList<BeaconDataModel>()
        val newList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "1"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "2")
        )
        val diffUtil = BeaconDiffUtil(oldList, newList)

        val result = diffUtil.newListSize

        assertEquals(newList.size, result)
    }

    @Test
    fun test_areItemsTheSame() {
        val oldList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "1"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "2")
        )
        val newList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "3"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "4")
        )
        val diffUtil = BeaconDiffUtil(oldList, newList)

        val result = diffUtil.areItemsTheSame(0, 0)

        assertEquals(true, result)
    }

    @Test
    fun test_areContentsTheSame() {
        val oldList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "1","1","1"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "1","1","2")
        )
        val newList = listOf(
            BeaconDataModel(BeaconType.IBEACON,"uuid1", "1","1","3"),
            BeaconDataModel(BeaconType.IBEACON,"uuid2", "1","1","4")
        )

        val diffUtil = BeaconDiffUtil(oldList, newList)

        val result = diffUtil.areContentsTheSame(0, 0)

        assertEquals(false, result)
    }
}