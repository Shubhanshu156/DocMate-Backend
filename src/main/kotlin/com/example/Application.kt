package com.example

import com.example.Implements.*
import com.example.Security.JwtTokenService
import com.example.Security.TokenConfig
import com.example.Security.hasing.SHA256HashingService
import com.example.plugins.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.io.FileInputStream


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

lateinit var TokenConfig: TokenConfig

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongopw = "i8g6kdMbxbPBzfgG"
    val dbname = "DocMate"

    val db: CoroutineDatabase by inject()

    val userDataSource: MongoUserDataSource by inject()
    val hashingService: SHA256HashingService by inject()
    val tokenService: JwtTokenService by inject()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 30L * 24L * 60L * 60L * 1000L,
        secret = "jwt_secret"
    )
    configureSecurity(tokenConfig)
    configureKoin()
    configureRouting(
        userDataSource,
        hashingService,
        tokenService,
        tokenConfig,
        AdminServicesImp(db),
        DoctorServiceImpl(db, FirebaseNotification()),
        PatientServiceImpl(db, FirebaseNotification())
    )
    configureSerialization()
    configureMonitoring()


}


fun initializeFirebase() {
    val serviceAccount = FileInputStream("src/main/resources/docmate.json")

    val options = FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    FirebaseApp.initializeApp(options)
}