package io.github.crowmisia.regipla.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

/**
 * BluetoothAdapter 拡張メソッド.
 */
fun BluetoothAdapter.rxLeScan(filters: List<ScanFilter>, settings: ScanSettings): Flowable<ScanResult> {
    return Flowable.create<ScanResult>({ emitter ->
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                emitter.onNext(result)
            }

            override fun onScanFailed(errorCode: Int) {
                emitter.tryOnError(ScanException(errorCode))
            }
        }
        val scanner = bluetoothLeScanner ?: run {
            emitter.tryOnError(DeviceNotAvailableException)
            return@create
        }
        scanner.startScan(filters, settings, callback)

        emitter.setCancellable {
            scanner.stopScan(callback)
        }
    }, BackpressureStrategy.BUFFER)
}
