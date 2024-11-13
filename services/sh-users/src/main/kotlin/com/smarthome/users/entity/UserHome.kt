package com.smarthome.users.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

@Document(collection = "homes")
data class Home(
    @Id var id: String = ObjectId.get().toString(),
    val name: String,
    val createdAt : Instant = Instant.now()
)

interface HomeRepository : MongoRepository<Home, String>

@Document(collection = "user_homes")
data class UserHome(
    @Id var id: String = ObjectId.get().toString(),
    @Indexed
    val userId: String,
    @DBRef
    val home: Home,
    val isOwner : Boolean
)

interface UserHomeRepository : MongoRepository<UserHome, String> {
    fun findByUserId(userId: String): List<UserHome>
}

@Document(collection = "homes_devices")
data class HomeDevice(
    @Id var id: String = ObjectId.get().toString(),
    @Indexed
    val homeId: String,
    @Indexed(unique = true)
    val deviceId : String
)

interface HomeDeviceRepository : MongoRepository<HomeDevice, String> {
    fun findByHomeId(homeId: String): List<HomeDevice>

    fun findByDeviceId(deviceId: String): HomeDevice?
}