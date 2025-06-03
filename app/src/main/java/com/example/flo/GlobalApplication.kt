package com.example.flo

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

lateinit var _keyHash: String
val keyHash: String
    get() = _keyHash

lateinit var _NATIVE_APP_KEY: String
val NATIVE_APP_KEY: String
    get() = _NATIVE_APP_KEY

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        _NATIVE_APP_KEY= getString(R.string.native_app_key)
        Log.d("app_key", NATIVE_APP_KEY)

        _keyHash = Utility.getKeyHash(this)
        Log.d("keyHash", keyHash)
        // Kakao SDK 초기화
        KakaoSdk.init(this, NATIVE_APP_KEY)
    }
}