package com.example

import com.example.Implements.AdminServicesImp
import com.example.Implements.MongoUserDataSource
import com.example.Security.JwtTokenService
import com.example.Security.TokenConfig
import com.example.Security.TokenService
import com.example.Security.hasing.HashingService
import com.example.Security.hasing.SHA256HashingService
import com.example.models.User
import io.ktor.server.application.*
import com.example.plugins.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongopw = System.getenv("MONGO_PW")
    val dbname = "DocMate"

    val db =
        KMongo.createClient(connectionString = "mongodb+srv://admin-shubhanshu:$mongopw@cluster0.f0g4klw.mongodb.net/$dbname?retryWrites=true&w=majority")
            .coroutine
            .getDatabase(dbname)

    val userDataSource = MongoUserDataSource(db)

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig,AdminServicesImp(db))
    configureSerialization()
    configureMonitoring()



}
