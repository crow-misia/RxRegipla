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

fun BluetoothGatt.disconnect(): Completable = Completable.create { emitter ->
    disconnect()
    emitter.onComplete()
}

fun BluetoothGatt.rxReadRssi(): Maybe<Int> = Single.create<Pair<Int, Int>> { emitter ->
    emitter.setDisposable(remoteRssiSubject.firstOrError().subscribe({ emitter.onSuccess(it) }, { emitter.tryOnError(it) }))
    if (readRemoteRssi().not()) {
        emitter.tryOnError(RssiReadingException(BluetoothGatt.GATT_FAILURE, device))
    }
}.flatMapMaybe { (value, status) ->
    if (status != BluetoothGatt.GATT_SUCCESS) {
        Maybe.error(RssiReadingException(status, device))
    } else {
        Maybe.just(value)
    }
}.subscribeOn(AndroidSchedulers.mainThread())

fun BluetoothGatt.rxDiscoverServices(): Maybe<List<BluetoothGattService>> = Single.create<Int> { emitter ->
    emitter.setDisposable(serviceDiscoveredSubject.firstOrError().subscribe({ emitter.onSuccess(it) }, { emitter.tryOnError(it) }))
    if (discoverServices().not()) {
        emitter.tryOnError(ServiceDiscoverFailedException(BluetoothGatt.GATT_FAILURE, device))
    }
}.flatMapMaybe { status ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(services)
    } else {
        Maybe.error(ServiceDiscoverFailedException(status, device))
    }
}.subscribeOn(AndroidSchedulers.mainThread())

fun BluetoothGatt.rxRequestMtu(mtu: Int): Maybe<Int> = Single.create<Pair<Int, Int>> { emitter ->
    emitter.setDisposable(mtuChangedSubject.firstOrError().subscribe({ emitter.onSuccess(it) }, { emitter.tryOnError(it) }))
    if (requestMtu(mtu).not()) {
        emitter.tryOnError(MtuRequestingException(BluetoothGatt.GATT_FAILURE, device))
    }
}.flatMapMaybe { (mtu, status) ->
    if (status == BluetoothGatt.GATT_SUCCESS) {
        Maybe.just(mtu)
    } else {
        Maybe.error(MtuRequestingException(status, device))
    }
}.subscribeOn(AndroidSchedulers.mainThread())

internal var BluetoothGatt.connectStateSubject: BehaviorSubject<Pair<Int, Int>> by FieldProperty { BehaviorSubject.create() }
internal var BluetoothGatt.remoteRssiSubject: PublishSubject<Pair<Int, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.serviceDiscoveredSubject: PublishSubject<Int> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.mtuChangedSubject: PublishSubject<Pair<Int, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.characteristicReadSubject: PublishSubject<Pair<BluetoothGattCharacteristic, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.characteristicWriteSubject: PublishSubject<Pair<BluetoothGattCharacteristic, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.characteristicChangedSubject: PublishSubject<BluetoothGattCharacteristic> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.descriptorReadSubject: PublishSubject<Pair<BluetoothGattDescriptor, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.descriptorWriteSubject: PublishSubject<Pair<BluetoothGattDescriptor, Int>> by FieldProperty { PublishSubject.create() }
internal var BluetoothGatt.reliableWriteCompletedSubject: PublishSubject<Int> by FieldProperty { PublishSubject.create() }
