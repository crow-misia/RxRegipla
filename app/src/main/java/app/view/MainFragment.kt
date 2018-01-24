package app.view

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import app.databinding.FragmentMainBinding
import app.log.BleLogEventsHandler
import app.util.rx.SchedulerProvider
import app.util.toHexString
import dagger.android.support.DaggerFragment
import io.github.crowmisia.regipla.RxRegiPla
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import permissions.dispatcher.*
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
class MainFragment @Inject constructor() : DaggerFragment() {
    @Inject @JvmField var bluetoothAdapter: BluetoothAdapter? = null
    @Inject lateinit var schedulerProvider: SchedulerProvider

    private lateinit var binding: FragmentMainBinding
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        scanWithPermissionCheck()
    }

    override fun onPause() {
        disposable.clear()

        super.onPause()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionsResult(requestCode, grantResults)
    }

    @NeedsPermission(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun scan() {
        bluetoothAdapter?.let {
            RxRegiPla(it, BleLogEventsHandler()).connect(activity!!, "2c66")
                    .subscribeOn(schedulerProvider.ui())
                    .observeOn(schedulerProvider.ui())
                    .subscribeBy(
                            onNext = { binding.buttonState.text = it.toHexString() },
                            onError = { Timber.e(it) }
                    )
                    .addTo(disposable)
        }
    }

    @OnShowRationale(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showRationaleForBluetooth(request: PermissionRequest) {
        AlertDialog.Builder(activity!!)
                .setMessage("rationale bluetooth")
                .create()
                .show()
    }

    @OnPermissionDenied(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onBluetoothDenied() {
        Toast.makeText(activity!!, "permission denied bluetooth", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onBluetoothNerverAskAgain() {
        Toast.makeText(activity!!, "permission never ask again bluetooth", Toast.LENGTH_SHORT).show()
    }
}
