package com.zhandos.numberplatecv

import android.app.Application
import android.util.Log
import com.zhandos.numberplatecv.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.OpenCVLoader.initAsync

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initOpenCV()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    private fun initOpenCV() {
        val engineInitialized = OpenCVLoader.initDebug()
        if (engineInitialized) {
            Log.i(TAG, "The OpenCV was successfully initialized in debug mode using .so libs.")
        } else {
            initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, object : LoaderCallbackInterface {
                override fun onManagerConnected(status: Int) {
                    when (status) {
                        LoaderCallbackInterface.SUCCESS -> Log.d(
                            TAG,
                            "OpenCV successfully started."
                        )
                        LoaderCallbackInterface.INIT_FAILED -> Log.d(TAG, "Failed to start OpenCV.")
                        LoaderCallbackInterface.MARKET_ERROR -> Log.d(
                            TAG,
                            "Google Play Store could not be invoked. Please check if you have the Google Play Store app installed and try again."
                        )
                        LoaderCallbackInterface.INSTALL_CANCELED -> Log.d(
                            TAG,
                            "OpenCV installation has been cancelled by the user."
                        )
                        LoaderCallbackInterface.INCOMPATIBLE_MANAGER_VERSION -> Log.d(
                            TAG,
                            "This version of OpenCV Manager is incompatible. Possibly, a service update is required."
                        )
                    }
                }

                override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface?) {
                    Log.d(TAG, "OpenCV Manager successfully installed from Google Play.")
                }
            })
        }
    }

    companion object {
        private const val TAG = "OPENCV"
    }
}