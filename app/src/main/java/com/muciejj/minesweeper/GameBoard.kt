package com.muciejj.minesweeper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import kotlin.random.Random

class GameBoard : AppCompatActivity() {

    enum class ClickEffects{
        REVEAL,
        FLAG
    }

    private var height : Int = 9
    private var width : Int = 9
    private var mines : Int = 10
    private var minesLeft : Int = 10
    var clickEffect = ClickEffects.REVEAL
    private lateinit var board : Array<Array<Field>>
    private lateinit var minesLeftText : TextView
    private lateinit var clickTypeBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_board)
        height = intent.getIntExtra("height", 9)
        width = intent.getIntExtra("width", 9)
        mines = intent.getIntExtra("mines", 10)
        minesLeft = mines
        clickTypeBtn = findViewById(R.id.clickTypeBtn)
        minesLeftText = findViewById(R.id.mineLeftCountTxt)
        setUpBoard()
    }

    private fun setUpBoard(){
        board = Array(height) { Array(width) { Field(this)} }
        //creating rows of buttons
        val vertLinLay = findViewById<LinearLayout>(R.id.rows_layout)
        //layout params
        val btnLayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
        btnLayoutParams.setMargins(4)
        val lineLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        for(row in 0 until height){

            val currentRow = LinearLayout(this)
            currentRow.orientation = LinearLayout.HORIZONTAL
            currentRow.layoutParams = lineLayoutParams;
            lineLayoutParams.weight = 1.0F

            for( col in 0 until width){
                val btn = Field(this)
                btn.setParams(this, row, col)
                btn.setPadding(0)
                btn.layoutParams = btnLayoutParams
                btnLayoutParams.weight = 1.0F
                currentRow.addView(btn)
                board[row][col] = btn
            }
            vertLinLay.addView(currentRow)
        }

        //setting up mines
        placeMines()
        updateMineCount()
        minesLeftText.text = getString(R.string.mines_left, minesLeft)
    }

    private fun placeMines() {
        var placed = 0
        while (placed < mines){
            val row = Random.nextInt(height)
            val col = Random.nextInt(width)
            if(board[row][col].type != Field.Type.MINE){
                board[row][col].type = Field.Type.MINE
                placed++
            }
        }
    }

    private fun updateMineCount(){
        for (row in 0 until height){
            for (col in 0 until width)
                board[row][col].setMineCount(checkNeighbourhoodForMines(row, col))
        }
    }

    private fun checkNeighbourhoodForMines(row : Int, col : Int) : Int{
        var count = 0;
        if(checkForMine(row-1, col-1)) count++
        if(checkForMine(row, col-1)) count++
        if(checkForMine(row+1, col-1)) count++
        if(checkForMine(row-1, col)) count++
        if(checkForMine(row+1, col)) count++
        if(checkForMine(row+1, col+1)) count++
        if(checkForMine(row, col+1)) count++
        if(checkForMine(row-1, col+1)) count++

        return count
    }

    /**
     * Returns true if this field contains a mine and false otherwise or when filed is out of board bounds
     */
    private fun checkForMine(row : Int, col : Int) : Boolean{
        return if(row < 0 || row >=width || col < 0 || col >= height)
            false
        else
            board[row][col].type == Field.Type.MINE
    }

    private fun checkBlank(row : Int, col : Int) : Boolean{
        return if(row < 0 || row >=width || col < 0 || col >= height)
            false
        else
            board[row][col].type == Field.Type.BLANK
    }

    fun gameOver(){
        Toast.makeText(this, "You lost!", Toast.LENGTH_SHORT).show()
        for (row in board){
            for (field in row)
                field.isClickable = false
        }
        findViewById<Button>(R.id.restetBtn).visibility = Button.VISIBLE
        findViewById<Button>(R.id.backBtn).visibility = Button.VISIBLE
        clickTypeBtn.visibility = Button.INVISIBLE
    }

    fun clickNeighbours(row : Int, col : Int){
        Log.i("Clicking", "Trying to click $row, $col")
        if(checkBlank(row-1, col-1)) board[row-1][col-1].onClick()
        if(checkBlank(row, col-1))        board[row][col-1].onClick()
        if(checkBlank(row+1, col-1)) board[row+1][col-1].onClick()
        if(checkBlank(row-1, col))       board[row-1][col].onClick()
        if(checkBlank(row+1, col))       board[row+1][col].onClick()
        if(checkBlank(row-1, col+1)) board[row-1][col+1].onClick()
        if(checkBlank(row, col+1))        board[row][col+1].onClick()
        if(checkBlank(row+1, col+1)) board[row+1][col+1].onClick()
    }

    fun mineflagged(){
        minesLeft--;
        minesLeftText.text = getString(R.string.mines_left, minesLeft)
        checkWin()
    }

    fun mineUnflagged(){
        minesLeft++
        minesLeftText.text = getString(R.string.mines_left, minesLeft)
    }

    fun checkWin(){
        var allFlagged = true
        var coveredLeft = 0
        for(row in board){
            for(field in row){
                if(field.state == Field.State.COVERED || field.state == Field.State.FLAGGED) coveredLeft++
                if(field.type == Field.Type.MINE && field.state != Field.State.FLAGGED) allFlagged = false
                if(field.type == Field.Type.BLANK && field.state == Field.State.FLAGGED) allFlagged = false
            }
        }

        Log.i("WinCheck", "allFlaged: $allFlagged, covered: $coveredLeft")

        if(allFlagged || coveredLeft == mines){
            gameWon()
        }
    }

    private fun gameWon(){
        Toast.makeText(this, "Congratulations, you won!", Toast.LENGTH_SHORT).show()
        for (row in board){
            for (field in row)
                field.isClickable = false
        }

        findViewById<Button>(R.id.restetBtn).visibility = Button.VISIBLE
        findViewById<Button>(R.id.backBtn).visibility = Button.VISIBLE
        clickTypeBtn.visibility = Button.INVISIBLE
    }

    fun resetGame(view : View){
        minesLeft = mines
        val vertLinLay = findViewById<LinearLayout>(R.id.rows_layout)
        vertLinLay.removeAllViews()
        setUpBoard()
        findViewById<Button>(R.id.restetBtn).visibility = Button.INVISIBLE
        findViewById<Button>(R.id.backBtn).visibility = Button.INVISIBLE
        clickTypeBtn.visibility = Button.VISIBLE
    }

    fun backToMenu(view : View){
        val menuIntent = Intent(this, MainActivity::class.java)
        startActivity(menuIntent)
    }

    fun toggleClickMode(view : View){
        if(clickEffect == ClickEffects.REVEAL){
            clickEffect = ClickEffects.FLAG
            clickTypeBtn.text = getString(R.string.flag)
        } else {
            clickEffect = ClickEffects.REVEAL
            clickTypeBtn.text = getString(R.string.bomb)
        }
    }
}