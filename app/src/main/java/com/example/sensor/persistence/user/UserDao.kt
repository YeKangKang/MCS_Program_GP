package com.example.sensor.persistence.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Get a user by id.
    @Query("SELECT * FROM Users WHERE email = :id")
    fun getUserById(id: String): User?

    // Insert a user in the database. If the user already exists, replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    // Delete all users.
    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM USERS")
    fun getAllUsers(): List<User>?
}