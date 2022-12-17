package com.zhandos.numberplatecv.di

import com.zhandos.numberplatecv.camera.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CameraViewModel() }
}