package com.devspace.taskbeats.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo("Key", ) val name: String,
    @ColumnInfo ("isSelected") val isSelected: Boolean
)
