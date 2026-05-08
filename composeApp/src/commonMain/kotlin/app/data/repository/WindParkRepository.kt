package app.data.repository

import app.core.model.WindPark

interface WindParkRepository {
    suspend fun getWindParks(): List<WindPark>
    suspend fun getWindPark(id: String): WindPark?
}
