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
import java.util.*

fun BluetoothGatt.rxRead(descriptor: BluetoothGattDescriptor): Maybe<ByteArray> = Single.create<Pair<BluetoothGattDescriptor, Int>> { emitter ->
    emitter.setDisposable(descriptorReadSubject.firstOrError().subscribe({ emitter.onSuccess(it)}, { emitter.tryOnError(it) }))
    if (readDescriptor(descriptor).not()) {
        emitter.tryOnError(IOException())
    }
}.flatMapMaybe { (readDescriptor, status) ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(readDescriptor.value)
    } else {
        Maybe.error(IOException())
    }
}

fun BluetoothGatt.rxWrite(descriptor: BluetoothGattDescriptor, value: ByteArray, checkIfAlreadyEnabled: Boolean = false): Maybe<BluetoothGattDescriptor> = Single.create<Pair<BluetoothGattDescriptor, Int>> { emitter ->
    if (checkIfAlreadyEnabled && Arrays.equals(descriptor.value, value)) {
        emitter.onSuccess(descriptor to BluetoothGatt.GATT_SUCCESS)
        return@create
    }

    emitter.setDisposable(descriptorWriteSubject.firstOrError().subscribe({ emitter.onSuccess(it)}, { emitter.tryOnError(it) }))
    descriptor.value = value
    if (writeDescriptor(descriptor).not()) {
        emitter.tryOnError(IOException())
    }
}.flatMapMaybe { (wroteDescriptor, status) ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(wroteDescriptor)
    } else {
        Maybe.error(IOException())
    }
}
