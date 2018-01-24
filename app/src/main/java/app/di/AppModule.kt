package app.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import app.App
import app.util.rx.SchedulerProvider
import app.util.rx.AppSchedulerProvider

import dagger.Module
import dagger.Provides

@Module class AppModule() {
    @Provides fun provideBluetoothAdapter(app: App): BluetoothAdapter? {
        val manager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides fun provideSchedulerProvider(): SchedulerProvider = AppSchedulerProvider
}
