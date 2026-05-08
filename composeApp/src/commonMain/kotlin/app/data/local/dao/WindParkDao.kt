package app.data.local.dao

import app.data.local.entity.WindParkEntity

interface WindParkDao {
    suspend fun getAll(): List<WindParkEntity>
    suspend fun getById(id: String): WindParkEntity?
}
