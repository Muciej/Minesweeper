package com.muciejj.minesweeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var heightEdit : EditText
    private lateinit var widthEdit : EditText
    private lateinit var mineEdit : EditText
    private var height = 9
    private var width = 9
    private var mine = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        heightEdit = findViewById(R.id.edit_height)
        widthEdit = findViewById(R.id.edit_width)
        mineEdit = findViewById(R.id.edit_mine_count)
    }

    fun startClick(view : View){
        val boardIntent = Intent(this, GameBoard::class.java)
        if(heightEdit.text.toString().trim().isNotEmpty()){
            height = heightEdit.text.toString().toInt()
        }
        if(widthEdit.text.toString().trim().isNotEmpty()){
            width = widthEdit.text.toString().toInt()
        }

        if(mineEdit.text.toString().trim().isNotEmpty()){
            mine = mineEdit.text.toString().toInt()
        }

        boardIntent.putExtra("height", height)
        boardIntent.putExtra("width", width)
        boardIntent.putExtra("mines", mine)
        startActivity(boardIntent)
    }
}