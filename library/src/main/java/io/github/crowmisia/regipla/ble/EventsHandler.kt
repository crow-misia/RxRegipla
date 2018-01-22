package io.github.crowmisia.regipla.ble

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

interface EventsHandler {
    fun onConnectionStateChange(status: Int, newState: Int)
    fun onReadRemoteRssi(rssi: Int, status: Int)
    fun onServicesDiscovered(status: Int)
    fun onMtuChanged(mtu: Int, status: Int)
    fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int)
    fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int)
    fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic)
    fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int)
    fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int)
    fun onReliableWriteCompleted(status: Int)
}

object EmptyEventHandler : EventsHandler {
    override fun onConnectionStateChange(status: Int, newState: Int) = Unit
    override fun onReadRemoteRssi(rssi: Int, status: Int) = Unit
    override fun onServicesDiscovered(status: Int) = Unit
    override fun onMtuChanged(mtu: Int, status: Int) = Unit
    override fun onCharacteristicRead(characteristic: BluetoothGattCharacteristic, status: Int) = Unit
    override fun onCharacteristicWrite(characteristic: BluetoothGattCharacteristic, status: Int) = Unit
    override fun onCharacteristicChanged(characteristic: BluetoothGattCharacteristic) = Unit
    override fun onDescriptorRead(descriptor: BluetoothGattDescriptor, status: Int) = Unit
    override fun onDescriptorWrite(descriptor: BluetoothGattDescriptor, status: Int) = Unit
    override fun onReliableWriteCompleted(status: Int) = Unit
}
