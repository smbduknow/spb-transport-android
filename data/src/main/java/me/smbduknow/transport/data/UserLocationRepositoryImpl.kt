package me.smbduknow.transport.data

import me.smbduknow.transport.data.playservices.PlayServiceProvider
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.repository.UserLocationRepository
import javax.inject.Inject

class UserLocationRepositoryImpl @Inject constructor(
        private val playServicesProvider: PlayServiceProvider
) : UserLocationRepository {

    override fun getUserLocation() = playServicesProvider.getLastLocation()
            .map { Coordinates(it.latitude, it.longitude) }

}