package com.smarthome.users.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

@Document(collection = "users")
data class User(
    @Id var id: String = ObjectId.get().toString(),
    val name: String,
    val phone: String? = null,
    @Indexed(unique = true)
    val email: String,
    val activeAt: Instant = Instant.now(),
    val createdAt: Instant = Instant.now()
)

interface UserRepository : MongoRepository<User, String> {
    fun findByEmail(name: String): List<User>
}