package com.example.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token:String,
    val msg:String,
    val userid: String?=null
        )