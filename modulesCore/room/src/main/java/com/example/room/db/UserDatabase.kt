package com.example.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.room.dao.StudentDao
import com.example.room.dao.UserDao
import com.example.room.entity.StudentEntity
import com.example.room.entity.UserEntity

/**
 * @Author: chenlida
 * @Date: 2022/11/7 17:16
 * @Description:建立数据库，创建表，version = 1数据库版本
 */
@Database(entities = [StudentEntity::class, UserEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    abstract fun getStudentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            val tmpInstance = INSTANCE
            if (tmpInstance != null) {
                return tmpInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    UserDatabase::class.java, "testDb")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}