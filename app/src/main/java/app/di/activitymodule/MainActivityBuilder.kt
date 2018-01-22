package app.di.activitymodule

import app.di.FragmentBuildersModule
import app.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module interface MainActivityBuilder {
    @ContributesAndroidInjector(modules = [
        FragmentBuildersModule::class,
        MainActivityModule::class
    ])
    fun contributeMainActivity(): MainActivity
}
