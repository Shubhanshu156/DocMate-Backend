package com.example.Implements

import com.example.FirebaseAdmin
import com.example.interfaces.Notification
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message

class FirebaseNotification : Notification {
    override suspend fun GenerateNotification(
        Title: String,
        message: String,
        imageurl: String,
        tokenid: String,
        time: String,
        sender: String,

    ) {
        val app = FirebaseApp.getApps().find { it.name == FirebaseApp.DEFAULT_APP_NAME }
        val firebaseApp = app ?: FirebaseAdmin.init()
        val notificationMessage = Message.builder()
            .putData("title", Title)
            .putData("SendersName", sender)
            .putData("MeetTime", time)
            .putData("image", imageurl)
            .putData("message", message)
            .setToken(tokenid)
            .build()
        println("result is $Title$sender$time$imageurl$message")
        FirebaseMessaging.getInstance(firebaseApp).sendAsync(notificationMessage)
    }
}
