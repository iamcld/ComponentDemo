package com.example.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author: chenlida
 * @Date: 2022/11/7 17:11
 * @Description: 学生表
 */
@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "studentName")
    var studentName: String = "",

    @ColumnInfo(name = "studentNo")
    var studentNo: String = ""
)
