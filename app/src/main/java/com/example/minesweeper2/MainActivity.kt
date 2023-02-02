package com.example.minesweeper2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //make true when user wants custom board
    var customBoard = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get best time and last time from shared preferences
        val sharedPref = getSharedPreferences("Scores", Context.MODE_PRIVATE)
        val bestTime: Int = sharedPref.getInt("BEST_TIME", 0)
        val lastGameTime: Int = sharedPref.getInt("LAST_GAME_TIME", 0)

        //set the best time and last game time in their respective text views
        best_time.text = "Best Time: ${bestTime}s"
        last_game_time.text = "Last Game Time ${lastGameTime}s"

        btn_custom_board.setOnClickListener {
            //Toggle the visibility of EditTexts on each click
            if(!customBoard) {
                //make editText enable for taking inputs
                et_rows.visibility = View.VISIBLE
                et_columns.visibility = View.VISIBLE
                et_mines.visibility = View.VISIBLE

                //make radio buttons disable
                rb_easy.isEnabled = false
                rb_medium.isEnabled = false
                rb_hard.isEnabled = false
            } else {
                //make editText disable
                et_rows.visibility = View.INVISIBLE
                et_columns.visibility = View.INVISIBLE
                et_mines.visibility = View.INVISIBLE

                //make radio buttons enable
                rb_easy.isEnabled = true
                rb_medium.isEnabled = true
                rb_hard.isEnabled = true
            }
            customBoard = !customBoard
        }

        btn_start.setOnClickListener {
            //if custom is true, it means user chooses to make custom board
            //else user chooses between easy, medium and hard levels
            if(customBoard) {
                makeCustomBoard()
            } else {
                getSelectedBoard()
            }
        }
    }

    private fun getSelectedBoard() {
        /*
        Get the checked radio button
        By default,
        for easy mode, rows=9, columns=9, mines=20
        for medium mode, rows=16, columns=16, mines=63
        for hard mode, rows=30, columns=16, mines=119
        and invoke startGame function which take rows, columns and mines as parameters
         */
        when(rb_group.checkedRadioButtonId) {
            rb_easy.id -> {
                startGame(9, 9, 20)
            }
            rb_medium.id -> {
                startGame(16, 16, 63)
            }
            rb_hard.id -> {
                startGame(30, 16, 119)
            }
        }
    }

    private fun makeCustomBoard() {
        //Check if any edit text is blank or not
        if (et_rows.text.toString().isBlank()) {
            et_rows.error = "Required Field"
            return
        }
        if (et_columns.text.toString().isBlank()) {
            et_columns.error = "Required Field"
            return
        }
        if (et_mines.text.toString().isBlank()) {
            et_mines.error = "Required Field"
            return
        }

        //get the number of rows, columns and mines
        val rows = Integer.parseInt(et_rows.text.toString())
        val columns = Integer.parseInt(et_columns.text.toString())
        val mines = Integer.parseInt(et_mines.text.toString())

        //check if the number of mines is less than 1/4th of the board’s button to avoid
        // overcrowding of mines.
        val threshold = (rows * columns) / 4
        if (mines >= threshold) {
            et_mines.error = "Mines should be less than $threshold"
            return
        }

        //Invoke startGame function which take rows, columns and mines as parameters
        startGame(rows, columns, mines)
    }

    private fun startGame(rows: Int, column: Int, mines: Int) {
        //Start the GameActivity and pass it number of rows, columns and mines
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("ROWS", rows)
            putExtra("COLUMNS", column)
            putExtra("MINES", mines)
        }
        startActivity(intent)
    }

}