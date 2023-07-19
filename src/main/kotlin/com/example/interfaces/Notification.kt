package com.example.interfaces

interface Notification {
    suspend fun GenerateNotification(
        Title: String,
        message:String,
        imageurl: String,
        tokenid: String,
        time: String,
        sender: String,
    ) }