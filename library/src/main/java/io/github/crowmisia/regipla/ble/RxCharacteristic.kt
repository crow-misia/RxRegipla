package io.github.crowmisia.regipla.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.IOException

fun BluetoothGatt.rxEnableNotification(characteristic: BluetoothGattCharacteristic, indication: Boolean = false, checkIfAlreadyEnabled: Boolean = true) =
        rxChangeNotification(characteristic, if (indication) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, true, checkIfAlreadyEnabled)

fun BluetoothGatt.rxDisableNotification(characteristic: BluetoothGattCharacteristic, checkIfAlreadyDisabled: Boolean = true) =
        rxChangeNotification(characteristic, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, false, checkIfAlreadyDisabled)

internal fun BluetoothGatt.rxChangeNotification(characteristic: BluetoothGattCharacteristic, byteArray: ByteArray, enable: Boolean, checkIfAlreadyChanged: Boolean) = Maybe.defer {
    if (setCharacteristicNotification(characteristic, enable).not()) {
        Maybe.error(IOException())
    } else {
        characteristic.getDescriptor(Const.CLIENT_CHARACTERISTIC_CONFIG)?.let {
            // 書き込み権限がある場合のみ書き込むする
            if (it.permissions and BluetoothGattDescriptor.PERMISSION_WRITE > 0) {
                rxWrite(it, byteArray, checkIfAlreadyChanged).map { characteristic }
            } else {
                Maybe.just(characteristic)
            }
        } ?: Maybe.error(IOException())
    }
}

fun BluetoothGatt.rxRead(characteristic: BluetoothGattCharacteristic): Maybe<ByteArray> = Single.create<Pair<BluetoothGattCharacteristic, Int>> { emitter ->
    emitter.setDisposable(characteristicReadSubject.firstOrError().subscribe({ emitter.onSuccess(it)}, { emitter.tryOnError(it) }))
    if (readCharacteristic(characteristic).not()) {
        emitter.tryOnError(IOException())
    }
}.flatMapMaybe { (readCharacteristic, status) ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(readCharacteristic.value)
    } else {
        Maybe.error(IOException())
    }
}

fun BluetoothGatt.rxWrite(characteristic: BluetoothGattCharacteristic, value: ByteArray): Maybe<BluetoothGattCharacteristic> = Single.create<Pair<BluetoothGattCharacteristic, Int>> { emitter ->
    emitter.setDisposable(characteristicWriteSubject.firstOrError().subscribe({ emitter.onSuccess(it)}, { emitter.tryOnError(it) }))
    characteristic.value = value
    if (writeCharacteristic(characteristic).not()) {
        emitter.tryOnError(IOException())
    }
}.flatMapMaybe { (wroteCharacteristic, status) ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(wroteCharacteristic)
    } else {
        Maybe.error(IOException())
    }
}

fun BluetoothGatt.rxChanged(characteristic: BluetoothGattCharacteristic): Flowable<ByteArray> =
        Flowable.defer { characteristicChangedSubject.toFlowable(BackpressureStrategy.BUFFER) }
                .filter { c -> c.uuid == characteristic.uuid }
                .map { it.value }
