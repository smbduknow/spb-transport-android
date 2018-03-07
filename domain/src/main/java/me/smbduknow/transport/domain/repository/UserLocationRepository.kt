package me.smbduknow.transport.domain.repository

import me.smbduknow.transport.domain.model.Coordinates
import rx.Observable

interface UserLocationRepository {

    fun getUserLocation(): Observable<Coordinates>

}