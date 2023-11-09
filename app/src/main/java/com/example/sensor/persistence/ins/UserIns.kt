package com.example.sensor.persistence.ins

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_data")
class UserIns(
    @PrimaryKey
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "user_avatar") val userAvatar: Int,
    @ColumnInfo(name = "date") val date: String
)