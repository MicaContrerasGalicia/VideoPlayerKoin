package com.globant.videoplayerproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.globant.videoplayerproject.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@MainActivity)
            modules(
                listOf(
                    viewModel,
                    repositoryApiService,
                    repositoryApiServiceToken,
                    repositoryApiStreamUrlVideo,
                    repositoryApi,
                    retrofitModule
                )
            )
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
}