package com.example.testbluetooth.android


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.testbluetooth.domain.BluetoothLeController
import com.example.testbluetooth.domain.BluetoothLeControllerFactory
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothLeController = ViewModelProvider(this, BluetoothLeControllerFactory(this))
            .get(BluetoothLeController::class.java)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BluetoothScanScreen(bluetoothLeController)
                }
            }
        }
    }

    private val REQUEST_CODE_BLUETOOTH = 1

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                REQUEST_CODE_BLUETOOTH
            )
        }
    }
}

@Composable
fun BluetoothScanScreen(bluetoothLeController: BluetoothLeController) {
    val scannedDevices by bluetoothLeController.scannedDevices.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Button(onClick = { bluetoothLeController.startScan() }) {
            Text("Start Scan")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { bluetoothLeController.stopScan() }) {
            Text("Stop Scan")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(scannedDevices) { device ->
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED) {
                    Text(text = device.name ?: "Unknown Device")
                } else {
                    Text(text = "Permission required to access device name")
                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        val context = LocalContext.current
        val dummyBluetoothLeController = BluetoothLeController(context)
        BluetoothScanScreen(bluetoothLeController = dummyBluetoothLeController)
    }
}