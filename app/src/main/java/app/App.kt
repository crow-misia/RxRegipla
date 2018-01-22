package app

import android.support.v7.app.AppCompatDelegate
import app.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import app.di.DaggerAppComponent

open class App : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.builder().create(this)

    override fun onCreate() {
        super.onCreate()

        setupVectorDrawable()
        setupDagger()
    }

    private fun setupVectorDrawable() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    open fun setupDagger() {
        AppInjector.init(this)
    }
}
