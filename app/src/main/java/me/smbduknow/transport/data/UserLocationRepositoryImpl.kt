package me.smbduknow.transport.data

import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.repository.UserLocationRepository

class UserLocationRepositoryImpl : UserLocationRepository {

    override fun getUserLocation() = PlayServiceProvider.getLastLocation()
            .map { Coordinates(it.latitude, it.longitude) }


}