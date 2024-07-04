package com.example.testbluetooth.domain

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BluetoothLeControllerFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothLeController::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BluetoothLeController(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}