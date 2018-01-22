package app.di

import app.view.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module interface FragmentBuildersModule {
    @ContributesAndroidInjector fun contributeMainFragment(): MainFragment
}
