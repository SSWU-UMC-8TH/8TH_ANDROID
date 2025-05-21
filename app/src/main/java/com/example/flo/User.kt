package com.example.flo

import androidx.room.*

@Entity(tableName = "UserTable")
data class User(
    var email: String,
    var password: String
){
    @PrimaryKey(autoGenerate = true) var id:Int=0
}