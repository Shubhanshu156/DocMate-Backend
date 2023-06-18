package com.example.Security.hasing

data class SaltedHash(
    val hash:String,
    val salt:String
)
