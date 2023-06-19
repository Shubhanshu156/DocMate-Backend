package com.example.models

import org.bson.types.ObjectId
import java.util.*

data class AppointMents(
    val date: Date?,
    val patientId:ObjectId,
    val meetlink:String?,
    )
