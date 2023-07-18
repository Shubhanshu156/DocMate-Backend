package com.example.models

import com.example.data.request.Gender
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Doctor(
    val username:String?=null,
    @BsonId val id:ObjectId,
    val age:String?=null,
    val category:String?=null,
    val token:String?=null,
    val fullname:String?=null,
    val about:String?=null,
    val payment:Int?=null,
    val gender:Gender?=null,
    val working_hour_start: Int?=null,
    val working_hour_end: Int?=null,
    val PrevSession:Int?=0,
    val rating:Int=0,
    val url:String?=null,
    val ratingArray: List<Int> = List(5) { 0 },
    val reviews:List<Review> = emptyList()
)
