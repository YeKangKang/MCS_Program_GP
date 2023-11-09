package com.example.sensor.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
class User(
    @PrimaryKey
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "userName") val name: String,
    @ColumnInfo(name = "gender") val gender: String,
    @ColumnInfo(name = "avatar") val avatar: Int,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "energy") var totalEnergy: Double
)