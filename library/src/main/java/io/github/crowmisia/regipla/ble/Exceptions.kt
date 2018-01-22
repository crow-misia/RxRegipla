package io.github.crowmisia.regipla.ble

import android.bluetooth.BluetoothDevice
import java.io.IOException

abstract class BluetoothException : Exception()

/**
 * Bluetoothを未サポート例外.
 */
object LocalDeviceDoesNotSupportBluetoothException : BluetoothException()

/**
 * デバイス無効例外.
 */
object DeviceNotAvailableException : BluetoothException()

/**
 * スキャン失敗例外.
 */
class ScanException(val errorCode: Int) : BluetoothException()

/**
 * サービス探索失敗例外.
 */
class ServiceDiscoverFailedException(val reason: Int, val device: BluetoothDevice) : BluetoothException()

/**
 * MTU要求失敗例外.
 */
class MtuRequestingException(val reason: Int, val device: BluetoothDevice) : BluetoothException()

/**
 * RSSI読取り失敗例外.
 */
class RssiReadingException(val reason: Int, val device: BluetoothDevice) : BluetoothException()
