package com.example

import com.example.Implements.*
import com.example.Security.JwtTokenService
import com.example.Security.TokenConfig
import com.example.Security.hasing.SHA256HashingService
import com.example.interfaces.DoctorService
import com.example.plugins.*
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.koin
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.io.FileInputStream


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)
lateinit var TokenConfig:TokenConfig
@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongopw = System.getenv("MONGO_PW")
    val dbname = "DocMate"

    val db:CoroutineDatabase by inject()

    val userDataSource: MongoUserDataSource by inject()
    val hashingService: SHA256HashingService by inject()
    val tokenService: JwtTokenService by inject()
    val adminServices: AdminServicesImp by inject()
    val doctorService: DoctorServiceImpl by inject()
    val patientService: PatientServiceImpl by inject()
    val tokenConfig=TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )
    configureSecurity(tokenConfig)
    configureKoin()
    configureRouting(
        userDataSource,
        hashingService,
        tokenService,
        tokenConfig,
        AdminServicesImp(db),
        DoctorServiceImpl(db,FirebaseNotification()),
        PatientServiceImpl(db,FirebaseNotification())
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