package me.smbduknow.transport.domain.interactor

import io.reactivex.Single
import me.smbduknow.transport.domain.MapInteractor
import me.smbduknow.transport.domain.model.Coordinates
import me.smbduknow.transport.domain.model.Vehicle
import me.smbduknow.transport.domain.repository.TransportRepository
import me.smbduknow.transport.domain.repository.UserLocationRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class MapInteractorTest {

    @Mock lateinit var transportRepo: TransportRepository
    @Mock lateinit var userLocationRepo: UserLocationRepository

    lateinit var interactor: MapInteractor

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = MapInteractorImpl(transportRepo, userLocationRepo)
    }

    @Test
    fun test_getVehicles() {
        val result = listOf<Vehicle>()

        `when`(transportRepo.getAllVehicles( any(), any() ))
                .thenReturn(Single.just(result))

        interactor.getVehicles().test()
                .assertNoErrors()
                .assertComplete()
                .assertValue(result)
    }

    @Test
    fun test_getUserLocation() {
        val result = Coordinates(0.0, 0.0)

        `when`(userLocationRepo.getUserLocation())
                .thenReturn(Single.just(result))

        interactor.getUserLocation().test()
                .assertNoErrors()
                .assertComplete()
                .assertValue(result)
    }


    private fun <T> any(): T = Mockito.any<T>() as T

}