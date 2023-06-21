package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class AddCategory(
    val category:String
)
@Serializable
data class Token(
    val token:String
)
