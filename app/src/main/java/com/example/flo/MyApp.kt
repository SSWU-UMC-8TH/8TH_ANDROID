package com.example.flo

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.kakao_native_app_key)
    }
}