package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class Search(
    val name:String?=null,
    val category:String?=null
)
@Serializable
data class SignUpResponse(
    val msg:String
)
@Serializable
data class SearchbyId(
    val id:String
)

@Serializable
data class SlotRequest(
    val id:String,
    val date:Int,
    val month:Int,
    val year:Int,
)