package app.log

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import app.util.toHexString
import io.github.crowmisia.regipla.ble.EventsHandler
import timber.log.Timber

class BleLogEventsHandler : EventsHandler {
    override fun onConnectionStateChange(status: Int, newState: Int) {
        Timber.tag("BLE").v("onConnectionStateChange with status %d and newState %d", status, newState)
    }

    override fun onReadRemoteRssi(rssi: Int, status: Int) {
        Timber.tag("BLE").v("onReadRemoteRssi with rssi %d and status %s", rssi, status)
    }

    override fun onServicesDiscovered(status: Int) {
        Timber.tag("BLE").v("onServicesDiscovered with status %d", status)
    }

    override fun onMtuChanged(mtu: Int, status: Int) {
        Timber.tag("BLE").v("onMtuChanged with mtu %d and status %d", mtu, status)
    }

    override fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int) {
        Timber.tag("BLE").v("onCharacteristicRead for characteristic %s, value %s, permissions %d, properties %d and status %d",
                characteristic.uuid, characteristic.value.toHexString(), characteristic.permissions, characteristic.properties, status)
    }

    override fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int) {
        Timber.tag("BLE").v("onCharacteristicWrite for characteristic %s, value %s, permissions %d, properties %d and status %d",
                characteristic.uuid, characteristic.value.toHexString(), characteristic.permissions, characteristic.properties, status)
    }

    override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {
        Timber.tag("BLE").v("onCharacteristicChanged for characteristic %s, value %s, permissions %d, properties %d",
                characteristic.uuid, characteristic.value.toHexString(), characteristic.permissions, characteristic.properties)
    }

    override fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int) {
        Timber.tag("BLE").v("onDescriptorRead for descriptor %s, value %s, permissions %d and status %d",
                descriptor.uuid, descriptor.value.toHexString(), descriptor.permissions, status)
    }

    override fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int) {
        Timber.tag("BLE").v("onDescriptorWrite for descriptor %s, value %s, permissions %d and status %d",
                descriptor.uuid, descriptor.value.toHexString(), descriptor.permissions, status)
    }

    override fun onReliableWriteCompleted(status: Int) {
        Timber.tag("BLE").v("onReliableWriteCompleted with status %d", status)
    }

}
