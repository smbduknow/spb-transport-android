package me.smbduknow.transport.domain.repository

import io.reactivex.Single
import me.smbduknow.transport.domain.model.Coordinates

interface UserLocationRepository {

    fun getUserLocation(): Single<Coordinates>

}