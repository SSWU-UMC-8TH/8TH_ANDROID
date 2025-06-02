package com.example.flo.utils.api

import com.example.flo.data.remote.AuthResponse
import com.example.flo.data.remote.TestResponse

interface TestView {
    fun onTestSuccess(response: TestResponse)
    fun onTestFailure(response: TestResponse)
}