package com.example.interfaces

interface Notification {
    suspend fun GenerateNotification(
        tokenid: String,
        Title: String,
        message:String,
        time: String,
        sender: String,
        imageurl: String
    ) }