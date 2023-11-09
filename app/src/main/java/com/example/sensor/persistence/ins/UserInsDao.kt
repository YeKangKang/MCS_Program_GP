package com.example.sensor.persistence.ins

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIns(ins: UserIns)

    @Query("SELECT * FROM USER_DATA")
    fun getAllIns(): MutableList<UserIns>?
}