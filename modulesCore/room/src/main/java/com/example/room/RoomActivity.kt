package com.example.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.libbase.LineLog
import com.example.room.databinding.ActivityRoomBinding
import com.example.room.db.UserDatabase
import com.example.room.entity.StudentEntity
import com.example.room.entity.UpdateNameBean
import com.example.room.entity.UserEntity

class RoomActivity : AppCompatActivity() {
    private val TAG = "RoomActivity--->"
    private val userDao by lazy {
        UserDatabase.getInstance(this).getUserDao()
    }
    private val studentDao by lazy {
        UserDatabase.getInstance(this).getStudentDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityRoomBinding.inflate(LayoutInflater.from(this), null, false)
        setContentView(viewBinding.root)
        //添加数据
        viewBinding.btnAdd.setOnClickListener {
            Thread {
                userDao.addUser(UserEntity(name = "张三", age = 28))
            }.start()
        }

        //查询数据
        viewBinding.btnQueryAll.setOnClickListener {
            Thread {
                val userList = userDao.queryAllUser()
                userList.forEach {
                    LineLog.d(TAG, "查询的数据库数据是: $it")
                }
            }.start()
        }

        //修改数据
        viewBinding.btnUpdate.setOnClickListener {
            Thread {
                userDao.updateUser(UserEntity(3, "李四", 18))
            }.start()
        }

        //删除数据
        viewBinding.btnDelete.setOnClickListener {
            Thread {
                userDao.deleteUser(UserEntity(2, "张三", 28))
            }.start()
        }

        //student表增加数据
        viewBinding.btnAddStudentData.setOnClickListener {
            Thread {
                studentDao.insertStudentData(
                    StudentEntity(
                        studentName = "王五",
                        studentNo = "20220115"
                    )
                )
            }.start()
        }

        viewBinding.btnQueryStudentAllData.setOnClickListener {
            Thread {
                studentDao.queryAllStudentData().forEach {
                    LineLog.d(TAG, "学生表的数据: $it")
                }
            }.start()
        }

        //只修改用户名字
        viewBinding.btnUpdateUsername.setOnClickListener {
            Thread {
                userDao.updateUserByName(UpdateNameBean(4, "张三"))
            }.start()
        }

    }

}