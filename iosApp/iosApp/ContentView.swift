import SwiftUI

struct ContentView: View {
    @StateObject var bluetoothManager = BluetoothManagerWrapper()

    var body: some View {
        VStack {
            Button(action: {
                bluetoothManager.startScan()
            }) {
                Text("Start Scan")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()

            Button(action: {
                bluetoothManager.stopScan()
            }) {
                Text("Stop Scan")
                    .padding()
                    .background(Color.red)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()

            List(bluetoothManager.scannedDevices, id: \.address) { device in
                VStack(alignment: .leading) {
                    Text(device.name ?? "Unknown Device")
                        .font(.headline)
                    Text(device.address)
                        .font(.subheadline)
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}