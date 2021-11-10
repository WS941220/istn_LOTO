package kr.co.istn.loto.di

import co.kr.istn.imatedata.ImateDataAdapter
import co.kr.istn.smartLock.SmartLockAdapter
import com.noke.nokemobilelibrary.NokeDeviceManagerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kr.co.istn.loto.di.noke.NokeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideNokeService(): NokeRepository = NokeRepository(NokeDeviceManagerService())

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideImateService(): ImateDataAdapter = ImateDataAdapter(baseUrl = "https://192.168.0.3/iMATEWebAPIB2-POS/", userId =  "iacm_system", userPassword =  "a#12!08@", sslIgnore =  true)

    @Singleton
    @Provides
    fun provideSmartLockService(): SmartLockAdapter = SmartLockAdapter(baseUrl = "https://192.168.0.3/iMATEWebAPIB2-POS/", userId =  "iacm_system", userPassword =  "a#12!08@", sslIgnore =  true)


}
