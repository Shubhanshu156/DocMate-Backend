package com.example.Implements

import com.example.interfaces.Notification
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message

class FirebaseNotification : Notification {
    override suspend fun GenerateNotification(
        tokenid: String,
        Title: String,
        time: String,
        sender: String,
        imageurl: String,
        message:String
    ) {
        val message = Message.builder()
            .putData("title", Title)
            .putData("Senders Name", sender)
            .putData("Meet Time", time)
            .putData("image", imageurl)
            .putData("message",message)
            .setToken(tokenid)
            .build()
        FirebaseMessaging.getInstance().sendAsync(message)
    }
}
