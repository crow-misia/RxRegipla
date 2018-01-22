package io.github.crowmisia.regipla

interface Callback {
    /**
     * 検索中に呼び出される.
     */
    fun onScanning()

    /**
     * 接続中に呼び出される.
     */
    fun onConnectinng()

    /**
     * 接続に呼び出される.
     */
    fun onConnected()

    /**
     * 切断時に呼び出される.
     */
    fun onDisconnected()

    /**
     * ボタンイベント.
     */
    fun onPushButton()

    /**
     * バッテリーイベント.
     */
    fun onBattery()
}
