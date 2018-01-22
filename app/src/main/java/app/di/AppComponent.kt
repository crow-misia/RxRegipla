package app.di

import app.App
import app.di.activitymodule.MainActivityBuilder
import app.di.activitymodule.MainActivityModule
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton
import dagger.android.support.AndroidSupportInjectionModule

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    MainActivityBuilder::class
])
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder() : AndroidInjector.Builder<App>()
}
