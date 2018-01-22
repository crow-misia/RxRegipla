package io.github.crowmisia.regipla.ble

import android.bluetooth.*
import android.content.Context
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * BluetoothDevice 拡張メソッド.
 */
fun BluetoothDevice.rxConnectGatt(context: Context, autoConnect: Boolean = false, eventsHandler: EventsHandler = EmptyEventHandler): Single<BluetoothGatt> {
    return Single.create<BluetoothGatt> { emitter ->
        // GATTに紐付いたSubjectにpublishするコールバック
        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                eventsHandler.onConnectionStateChange(status, newState)
                if (newState == BluetoothAdapter.STATE_DISCONNECTED) {
                    gatt.close()
                }
                gatt.connectStateSubject.onNext(newState to status)
            }

            override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
                eventsHandler.onReadRemoteRssi(rssi, status)
                gatt.remoteRssiSubject.onNext(rssi to status)
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                eventsHandler.onServicesDiscovered(status)
                gatt.serviceDiscoveredSubject.onNext(status)
            }

            override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                eventsHandler.onMtuChanged(mtu, status)
                gatt.mtuChangedSubject.onNext(mtu to status)
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                eventsHandler.onCharacteristicRead(characteristic, status)
                gatt.characteristicReadSubject.onNext(characteristic to status)
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                eventsHandler.onCharacteristicWrite(characteristic, status)
                gatt.characteristicWriteSubject.onNext(characteristic to status)
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                eventsHandler.onCharacteristicChanged(characteristic)
                gatt.characteristicChangedSubject.onNext(characteristic)
            }

            override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                eventsHandler.onDescriptorRead(descriptor, status)
                gatt.descriptorReadSubject.onNext(descriptor to status)
            }

            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                eventsHandler.onDescriptorWrite(descriptor, status)
                gatt.descriptorWriteSubject.onNext(descriptor to status)
            }

            override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
                eventsHandler.onReliableWriteCompleted(status)
                gatt.reliableWriteCompletedSubject.onNext(status)
            }
        }

        // 接続
        connectGatt(context, autoConnect, callback).also {
            emitter.onSuccess(it)
            // GATTが取得出来ない場合、エラー発火
        } ?: emitter.tryOnError(LocalDeviceDoesNotSupportBluetoothException)
    }.subscribeOn(AndroidSchedulers.mainThread())
}
