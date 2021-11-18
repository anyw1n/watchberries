package alexeyzhizhensky.watchberries.di

import alexeyzhizhensky.watchberries.data.room.WbDatabase
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = WbDatabase.create(context)

    @Provides
    fun provideUserDao(database: WbDatabase) = database.userDao()

    @Provides
    fun provideProductRemoteKeyDao(database: WbDatabase) = database.productRemoteKeyDao()

    @Provides
    fun provideProductDao(database: WbDatabase) = database.productDao()

    @Provides
    fun providePriceDao(database: WbDatabase) = database.priceDao()
}
