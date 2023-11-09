package com.example.sensor.persistence.ins

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.sensor.persistence.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Database(entities = [UserIns::class], version = 1)
abstract class UserInsDatabase() : RoomDatabase(){

    abstract fun insDao(): UserInsDao

    private var insList: MutableList<UserIns>? = null

    companion object {

        @Volatile private var INSTANCE: UserInsDatabase? = null

        fun getInstance(context: Context): UserInsDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also{
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext, UserInsDatabase::class.java, "sensor_ins.db").build()
    }

    fun queryIns(): MutableList<UserIns>? {
        if (insList.isNullOrEmpty()) {
            insList = insDao().getAllIns()
        }
        return insList
    }

    fun insertUserIns(ins: UserIns) {
        insDao().insertIns(ins)
        insList?.let {
            it.add(ins)
        }
    }

}