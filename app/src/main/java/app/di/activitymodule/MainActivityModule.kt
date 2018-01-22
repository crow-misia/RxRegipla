package app.di.activitymodule

import app.view.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module interface MainActivityModule {
    @ContributesAndroidInjector fun contributeMainActivityInjector(): MainActivity
}
