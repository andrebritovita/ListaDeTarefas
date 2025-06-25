package com.devspace.taskbeats.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("Select * from taskentity")
    fun getAll(): List<TaskEntity> //Envia um "List<TaskEntity>" para algum lugar

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll (taskEntity: List<TaskEntity>) //Recebe um um "List<TaskEntity>" de algum lugar

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert (taskEntity: TaskEntity)

    @Update
    fun upDate(taskEntity: TaskEntity)

    @Delete
    fun delete (taskEntity: TaskEntity)

    @Delete
    fun deleteAll (taskEntity: List<TaskEntity>)

    @Query("Select * from taskentity where Category = :categoryName")
    fun getAllByCategoryName(categoryName: String): List<TaskEntity>
}