package com.example.thread

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)
        val a=A() //객체
        val b=B()

        a.start() //스레드 실행
        a.join()//a스레드가 끝난 이후에 b 스레드가 작동하는 것을 볼 수 있음
        b.start()
    }
    class  A:Thread(){
        override fun run(){
            super.run()
            for(i in 1..1000){
                Log.d("test","first:$i")
            }
        }
    }
    class  B:Thread(){
        override fun run(){
            super.run()
            for(i in 1000 downTo 1){
                Log.d("test","second:$i")
            }
        }
    }
}

