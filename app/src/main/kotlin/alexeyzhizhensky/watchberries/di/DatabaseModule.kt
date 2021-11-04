package alexeyzhizhensky.watchberries.di

import alexeyzhizhensky.watchberries.data.WbDatabase
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
    fun provideDatabase(@ApplicationContext context: Context) = WbDatabase.getInstance(context)

    @Provides
    fun provideUserDao(database: WbDatabase) = database.userDao()
}
