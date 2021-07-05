package com.globant.videoplayerproject.di

import com.globant.videoplayerproject.api.*
import com.globant.videoplayerproject.ui.exoplayer.ExoPlayerViewModel
import com.globant.videoplayerproject.ui.topGames.TopGamesViewModel
import com.globant.videoplayerproject.ui.topStream.TopStreamViewModel
import com.globant.videoplayerproject.utils.BASE_URL
import com.globant.videoplayerproject.utils.BASE_URL_STREAM_VIDEOS
import com.globant.videoplayerproject.utils.BASE_URL_TOKEN
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val viewModel = module {
    viewModel { TopGamesViewModel(get(), get()) }
    viewModel { TopStreamViewModel(get()) }
    viewModel { ExoPlayerViewModel(get()) }
}

val repositoryApiService = module {
    single { ApiServiceRepository(get()) }
}

val repositoryApiServiceToken = module {
    single { ApiServiceTokenRepository(get()) }
}

val repositoryApiStreamUrlVideo = module {
    single { ApiStreamUrlVideoRepository(get()) }
}

val repositoryApi = module {
    fun createApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    single { createApiService(get(named("apiService"))) }

    fun createApiServiceToken(retrofitToken: Retrofit): ApiServiceToken {
        return retrofitToken.create(ApiServiceToken::class.java)
    }

    single { createApiServiceToken(get(named("apiServiceToken"))) }

    fun createApiServiceStreams(retrofitStream: Retrofit): ApiStreamUrlVideo {
        return retrofitStream.create(ApiStreamUrlVideo::class.java)
    }
    single { createApiServiceStreams(get(named("apiServiceStreams"))) }
}

val retrofitModule = module {
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    single { provideGson() }
    fun provideInterceptor(): Interceptor {
        return HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { provideInterceptor() }

    fun provideHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()
    }

    single { provideHttpClient(get()) }

    fun provideApiServiceRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
    }

    single(named("apiService")) { provideApiServiceRetrofit(get(), get()) }

    fun provideApiServiceTokenRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL_TOKEN)
            .client(client)
            .build()
    }

    single(named("apiServiceToken"))  { provideApiServiceTokenRetrofit(get(), get()) }

    fun provideApiServiceStreamRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL_STREAM_VIDEOS)
            .client(client)
            .build()
    }

    single(named("apiServiceStreams"))  { provideApiServiceStreamRetrofit(get(), get()) }
}