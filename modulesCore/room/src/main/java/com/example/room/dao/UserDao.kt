package com.example.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.room.entity.UpdateNameBean
import com.example.room.entity.UserEntity

/**
 * @Author: chenlida
 * @Date: 2022/11/7 17:20
 * @Description:增删改查
 */
@Dao
interface UserDao {
    /**
     * 增加用户
     */
    @Insert
    fun addUser(vararg userEntity: UserEntity)

    /**
     * 删除用户
     */
    @Insert
    fun deleteUser(vararg userEntity: UserEntity)

    /**
     * 修改用户信息
     */
    @Update
    fun updateUser(vararg userEntity: UserEntity)

    @Update(entity = UserEntity::class)
    fun updateUserByName(vararg updateNameBean: UpdateNameBean)

    @Query("select * from user")
    fun queryAllUser(): List<UserEntity>
}