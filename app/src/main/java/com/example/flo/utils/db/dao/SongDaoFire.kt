package com.example.flo.utils.db.dao

import com.example.flo.data.entities.Song
import com.google.firebase.database.*
import com.google.android.gms.tasks.Task

class SongDaoFire {
    private var _databaseReference :DatabaseReference? = null
    private var databaseReference
        get() = _databaseReference!!
        set(value){ _databaseReference = value }

    init{
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("song")
    }

    // 등록
    fun add (song: Song?): Task<Void> {
        return databaseReference.push().setValue(song)
    }

    //조회
    fun getSongs(): Query? {
        return databaseReference
    }
}