package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class Search(
    val name:String
)
@Serializable
data class SignUpResponse(
    val msg:String
)