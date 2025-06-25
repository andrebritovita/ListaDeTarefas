package com.devspace.taskbeats.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["Key"],
            childColumns = ["Category"]
        )
    ]
)
data class TaskEntity(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    @ColumnInfo ("name") val name: String,
    @ColumnInfo ("Category") val category: String
)
