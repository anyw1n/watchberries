package alexeyzhizhensky.watchberries.di

import alexeyzhizhensky.watchberries.network.WbApiService
import alexeyzhizhensky.watchberries.network.WbConnectivityManagerDefault
import alexeyzhizhensky.watchberries.network.WbConnectivityManagerLegacy
import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideWbApiService() = WbApiService.create()

    @Singleton
    @Provides
    fun provideWbConnectivityManager(@ApplicationContext context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            WbConnectivityManagerDefault(context)
        } else {
            WbConnectivityManagerLegacy(context)
        }
}
