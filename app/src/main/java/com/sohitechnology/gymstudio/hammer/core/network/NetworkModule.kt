package com.sohitechnology.gymstudio.hammer.core.network

import com.google.gson.Gson
import com.sohitechnology.gymstudio.hammer.core.session.AppDataStore
import com.sohitechnology.gymstudio.hammer.core.session.SessionKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        createLoggingInterceptor()

    @Provides
    @Singleton
    fun provideHeaderInterceptor( dataStore: AppDataStore): HeaderInterceptor =
        HeaderInterceptor(
            tokenProvider = {
                // DataStore se token
                runBlocking {
                    dataStore.readOnce(SessionKeys.TOKEN, "")
                }
            }
        )

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient =
        createOkHttpClient(loggingInterceptor, headerInterceptor)


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.gymstudio.in/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}
