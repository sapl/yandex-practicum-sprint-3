package com.smarthome.users.service

import com.smarthome.users.dto.UserHomeDto
import com.smarthome.users.entity.Home
import com.smarthome.users.entity.HomeDevice
import com.smarthome.users.entity.HomeDeviceRepository
import com.smarthome.users.entity.HomeRepository
import com.smarthome.users.entity.User
import com.smarthome.users.entity.UserHome
import com.smarthome.users.entity.UserHomeRepository
import com.smarthome.users.entity.UserRepository
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserService(
    val homeDeviceRepository: HomeDeviceRepository,
    val homeRepository: HomeRepository,
    val userRepository: UserRepository,
    val userHomeRepository: UserHomeRepository
) {
    private val logger = LogFactory.getLog(javaClass)

    //TODO в реальном проекте будет проверка прав пользователя

    fun getUsers(): List<User> = userRepository.findAll()


    fun createUser(name: String, email: String): User =
        userRepository.save(User(name = name, email = email)).apply {
            logger.info("User $this has been created")
        }

    @Transactional
    fun createHome(userId: String, name: String) {
        homeRepository.save(Home(name = name)).apply {
            userHomeRepository.save(UserHome(userId = userId, home = this, isOwner = true))
            logger.info("Home $this created by userId=${userId}")
        }
    }

    fun getHomes(userId: String): List<UserHomeDto> =
        userHomeRepository.findByUserId(userId).map {
            UserHomeDto(id = it.home.id,
                name = it.home.name,
                isOwner = it.isOwner,
                deviceIds = homeDeviceRepository.findByHomeId(it.home.id).map { hd -> hd.deviceId })
        }

    @Transactional
    fun assignDevice(homeId: String, deviceId: String) {
        unAssignDevice(deviceId)
        homeDeviceRepository.save(HomeDevice(homeId = homeId, deviceId = deviceId));
    }

    fun unAssignDevice(deviceId: String) {
        homeDeviceRepository.findByDeviceId(deviceId)?.let { homeDeviceRepository.delete(it); }
    }

}