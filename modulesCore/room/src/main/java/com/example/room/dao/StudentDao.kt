package com.example.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.room.entity.StudentEntity

/**
 * @Author: chenlida
 * @Date: 2022/11/7 17:09
 * @Description:
 */
@Dao
interface StudentDao {
    @Insert
    fun insertStudentData(vararg studentEntity: StudentEntity)

    @Query("select * from student")
    fun queryAllStudentData(): List<StudentEntity>
}