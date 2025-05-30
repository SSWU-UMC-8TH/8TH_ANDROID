package com.example.flo

data class AuthResponse(val is_success:Boolean,
                        val code: String,
                        val message:String,
                        val result: Result?=null)

data class Result(val memberId: Int,
                  val createdAt: String,
                  val updatedAt: String)
