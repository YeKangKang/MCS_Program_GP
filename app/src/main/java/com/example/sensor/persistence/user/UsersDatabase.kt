package com.example.sensor.persistence.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sensor.persistence.ins.UserIns
import com.example.sensor.persistence.ins.UserInsDao

@Database(entities = [User::class, UserIns::class], version = 1)
abstract class UsersDatabase() : RoomDatabase(){

    abstract fun userDao(): UserDao

    private var loginUser: User? = null

    abstract fun insDao(): UserInsDao

    companion object {

        @Volatile private var INSTANCE: UsersDatabase? = null

        fun getInstance(context: Context): UsersDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also{
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, UsersDatabase::class.java, "sensor.db").build()
    }

    fun queryUsers(): List<User>? {
        return userDao().getAllUsers()
    }

    fun insertUser(user: User) {
        userDao().insertUser(user)
    }

    fun setLoginUser(user: User) {
        this.loginUser = user
    }

    fun getLoginUser(): User {
        return loginUser!!
    }

    fun updateUserEnergy(value: Double) {
        if(loginUser == null) {
            return
        }
        loginUser!!.totalEnergy = loginUser!!.totalEnergy + value
        insertUser(loginUser!!)
    }
}