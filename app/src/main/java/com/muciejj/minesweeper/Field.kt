package com.muciejj.minesweeper

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor

class Field constructor(context : Context) : androidx.appcompat.widget.AppCompatButton(context) {
    enum class Type{
        BLANK,
        MINE,
    }

    enum class State{
        CLICKED,
        FLAGGED,
        COVERED
    }

    var row : Int = 0
    var col : Int = 0
    var type : Type = Type.BLANK
    var state : State = State.COVERED
    var minesNearby : Int = 0
    lateinit var board : GameBoard

    init {
        setCovered()
        textAlignment = LinearLayout.TEXT_ALIGNMENT_CENTER
        setOnClickListener(){
            onClick()
        }
    }

    fun setParams(b : GameBoard, row : Int, col : Int){
        this.board = b
        this.row = row
        this.col = col
    }

    fun onClick() {
        if(state == State.CLICKED)
            return
        else{
            if(board.clickEffect == GameBoard.ClickEffects.REVEAL) changeState(State.CLICKED)
            else if(board.clickEffect == GameBoard.ClickEffects.FLAG && state == State.COVERED) changeState(State.FLAGGED)
            else if(board.clickEffect == GameBoard.ClickEffects.FLAG && state == State.FLAGGED) {
                board.mineUnflagged()
                changeState(State.COVERED)
            }
        }

    }

    fun changeState(newState : State){
        when(newState){
            State.CLICKED -> setClicked()
            State.FLAGGED -> setFlagged()
            State.COVERED -> setCovered()
        }
    }

    private fun setCovered() {
        this.text = ""
        this.state = State.COVERED
        this.setBackgroundColor(getColor(context, R.color.hidden))
    }

    private fun setFlagged() {
        this.state = State.FLAGGED
        this.setBackgroundColor(getColor(context, R.color.flagged))
        this.text = context.getString(R.string.flag)
        board.mineflagged()
    }

    private fun setClicked() {
        this.text = ""
        this.state = State.CLICKED
        if(type == Type.BLANK) {
            this.setBackgroundColor(getColor(context, R.color.revealed))
            if(minesNearby > 0){
                this.text = minesNearby.toString()
            }
            if(minesNearby == 0) board.clickNeighbours(row, col)
            board.checkWin()
        }
        else {
            this.setBackgroundColor(getColor(context, R.color.mine))
            this.setText(R.string.bomb)
            board.gameOver()
        }
    }

    fun setMineCount(count : Int){
        this.minesNearby = count
    }
}