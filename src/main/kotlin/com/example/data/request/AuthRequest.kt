package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username:String,
    val password:String,
    val type:String,
)
