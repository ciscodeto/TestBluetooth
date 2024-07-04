package com.example.testbluetooth.domain

import platform.CoreBluetooth.*
import platform.Foundation.*
import platform.CoreLocation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothManager : NSObject(), CBCentralManagerDelegateProtocol, CBPeripheralDelegateProtocol, CLLocationManagerDelegateProtocol {
    private lateinit var centralManager: CBCentralManager
    private lateinit var locationManager: CLLocationManager
    private val _scannedDevices = MutableStateFlow<List<CBPeripheral>>(emptyList())
    val scannedDevices: StateFlow<List<CBPeripheral>> = _scannedDevices.asStateFlow()
    private var connectedPeripheral: CBPeripheral? = null

    override fun init(): BluetoothManager {
        super.init()
        centralManager = CBCentralManager(delegate = this, queue = null)
        locationManager = CLLocationManager()
        locationManager.delegate = this
        requestPermissions()
        return this
    }

    fun startScan() {
        _scannedDevices.value = emptyList()
        centralManager.scanForPeripheralsWithServices(null, null)
    }

    fun stopScan() {
        centralManager.stopScan()
    }

    private fun requestPermissions() {
        locationManager.requestWhenInUseAuthorization()
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        if (central.state != CBManagerStatePoweredOn) {
            println("Bluetooth is not available.")
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>?,
        RSSI: NSNumber
    ) {
        if (!_scannedDevices.value.contains(didDiscoverPeripheral)) {
            _scannedDevices.value = _scannedDevices.value + didDiscoverPeripheral
        }
    }

    fun connectToDevice(peripheral: CBPeripheral) {
        connectedPeripheral = peripheral
        centralManager.connectPeripheral(peripheral, null)
    }

    override fun centralManager(
        central: CBCentralManager,
        didConnectPeripheral: CBPeripheral
    ) {
        didConnectPeripheral.delegate = this
        didConnectPeripheral.discoverServices(null)
    }

    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverServices: NSError?
    ) {
        peripheral.services?.forEach { service ->
            peripheral.discoverCharacteristics(null, service as CBService)
        }
    }

    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverCharacteristicsForService: CBService,
        error: NSError?
    ) {
        didDiscoverCharacteristicsForService.characteristics?.forEach { characteristic ->
            println("Discovered characteristic: ${(characteristic as CBCharacteristic).UUID}")
        }
    }

    // CLLocationManagerDelegateProtocol
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        when (didChangeAuthorizationStatus) {
            kCLAuthorizationStatusAuthorizedAlways, kCLAuthorizationStatusAuthorizedWhenInUse -> {
                println("Location access granted")
            }
            kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                println("Location access denied/restricted")
            }
            kCLAuthorizationStatusNotDetermined -> {
                println("Location access not determined")
            }
            else -> {
                println("Unknown location access status")
            }
        }
    }
}