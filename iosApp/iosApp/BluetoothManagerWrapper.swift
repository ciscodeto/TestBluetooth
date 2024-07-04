import SwiftUI
import shared

class BluetoothManagerWrapper: ObservableObject {
    private var bluetoothManager: BluetoothManager

    @Published var scannedDevices: [(name: String?, address: String)] = []

    init() {
        bluetoothManager = BluetoothManager().init()
        bluetoothManager.scannedDevices.collect { devices in
            self.scannedDevices = devices.map { device in
                let name = device.name
                let address = device.identifier.UUIDString
                return (name, address)
            }
        } completionHandler: { (throwable) in
            print("Error collecting scanned devices: \(throwable.localizedDescription)")
        }
    }

    func startScan() {
        bluetoothManager.startScan()
    }

    func stopScan() {
        bluetoothManager.stopScan()
    }
}
