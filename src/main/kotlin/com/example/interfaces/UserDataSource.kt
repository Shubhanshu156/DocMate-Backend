package com.example.interfaces

import com.example.models.User

interface UserDataSource {
    suspend fun getUserByUserNameType(username:String,type:String): User?
    suspend fun insertUser(user: User):Boolean
    suspend fun UpdateToken(type:String,id:String,token:String):Boolean
}