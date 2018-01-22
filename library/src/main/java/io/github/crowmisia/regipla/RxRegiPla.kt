package io.github.crowmisia.regipla

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.support.annotation.RequiresPermission
import io.github.crowmisia.regipla.ble.*
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.util.*


/**
 * レジプラ 制御クラス.
 */
class RxRegiPla constructor(private val adapter: BluetoothAdapter, private val eventsHandler: EventsHandler) {
    /**
     * レジプラに接続を行う.
     *
     * @param id レジプラのID
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    fun connect(context: Context, id: String): Flowable<ByteArray> {
        val filters = Arrays.asList(ScanFilter.Builder()
                .setDeviceName(ID_PREFIX + id)
                .build())
        val settings = ScanSettings.Builder().build()

        return adapter.rxLeScan(filters, settings)
                .firstOrError()
                .flatMap { it.device.rxConnectGatt(context, false, eventsHandler) }
                .flatMapPublisher { gatt ->
                    // 接続できるまで待機 TODO:メソッド化する
                    gatt.connectStateSubject.filter { it.first == BluetoothGatt.STATE_CONNECTED }
                            .firstElement()
                            .flatMap { gatt.rxDiscoverServices() }
                            .flatMapPublisher {
                        // サービスが取得出来たら、PIO_INPUTの通知を有効にする
                        val service = it.firstOrNull { it.uuid == SERVICE_UUID }
                        service?.getCharacteristic(PIO_INPUT_NOTIFICATION_CHARACTERISTIC_UUID)?.also {
                            return@flatMapPublisher gatt.rxEnableNotification(it).flatMapPublisher { gatt.rxChanged(it) }
                        }
                        return@flatMapPublisher Flowable.error<ByteArray>(IOException())
                    }
                }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        const val ID_PREFIX = "regipla-"
        val SERVICE_UUID = UUID.fromString("C4C80000-3958-4A9E-8B40-9E8001469771") as UUID
        val PIO_SETTINGS_CHARACTERISTIC_UUID = UUID.fromString("C4C80001-3958-4A9E-8B40-9E8001469771") as UUID
        val PIO_PULLUP_CHARACTERISTIC_UUID = UUID.fromString("C4C80002-3958-4A9E-8B40-9E8001469771") as UUID
        val PIO_OUTPUT_CHARACTERISTIC_UUID = UUID.fromString("C4C80003-3958-4A9E-8B40-9E8001469771") as UUID
        val PIO_INPUT_NOTIFICATION_CHARACTERISTIC_UUID = UUID.fromString("C4C80004-3958-4A9E-8B40-9E8001469771") as UUID
        val SOFTWARE_RESET_CHARACTERISTIC_UUID = UUID.fromString("C4C80005-3958-4A9E-8B40-9E8001469771") as UUID
        val LOW_BATTERY_NOTIFICATION_CHARACTERISTIC_UUID = UUID.fromString("C4C80006-3958-4A9E-8B40-9E8001469771") as UUID
        val LED_INDICATOR_SETTINGS_CHARACTERISTIC_UUID = UUID.fromString("C4C80007-3958-4A9E-8B40-9E8001469771") as UUID
        val TIMER_AVAILABLE_CHARACTERISTIC_UUID = UUID.fromString("C4C80008-3958-4A9E-8B40-9E8001469771") as UUID
    }
}
