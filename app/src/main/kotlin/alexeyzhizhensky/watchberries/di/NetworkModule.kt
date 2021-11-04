package alexeyzhizhensky.watchberries.di

import alexeyzhizhensky.watchberries.api.WatchberriesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideWatchberriesApiService() = WatchberriesApiService.create()
}
