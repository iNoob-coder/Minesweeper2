package com.example.minesweeper2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity() {

    //a constant symbol for mine
    private val MINE = -1

    //default count of rows=9, columns=9, mines=20
    private var rows: Int = 9
    private var columns: Int = 9
    private var mines: Int = 20

    private lateinit var board: Array<Array<MineButton>>
    private var flag = false// true if flag button is selected
    private val movement = intArrayOf(-1, 0, 1)

    //the following variables are used for timer working
    private var time = "0" //to store time elapsed
    private var seconds = 0 //this variable increases by 1 per second
    private var wasRunning = false //stores the state of the timer before the activity goes in paused state
    private var running = false //stores the state of the game
    private val handler = Handler()
    private lateinit var runnable: Runnable

    private var count: Int = 0 //integer variable to check first move
    private var flagCount: Int = 0 //it contains count of buttons marked as flag


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        startTimer() //initializes the working of timer

        //gets the row, column and mine count according to user selection
        rows = intent.getIntExtra("ROWS", 9)
        columns = intent.getIntExtra("COLUMNS", 9)
        mines = intent.getIntExtra("MINES", 20)

        //initialize the game board of size rows * columns as an object of MineButton class
        board = Array(rows) { Array(columns) { MineButton() } }

        setupBoard() //setup the board according to user selection

        btn_restart.setOnClickListener {
            restartGame() //restart the game
        }

        btn_flag.setOnClickListener {
                //this button toggles the flag-mine selection and selects the background accordingly
                if (flag)
                    btn_flag.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flag))
                else
                    btn_flag.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mine))

                flag = !flag
            }
    }

    private fun restartGame() {
        count = 0 //to record user first move
        running = false //to stop the timer
        seconds = 0 //reset the time to 0
        flagCount = mines //reset the number of remaining mines
        flag = false //by default mine button is selected

        /*
        Iterate through all the buttons and do the following :-
        1. Set its neighbouring mine count to 0
        2. Set the isMarked as false as no flag is marked
        3. Set the isRevealed as false as no button is revealed
        4. Set the button text to blank
        5. Enable the button for future clicks
        6. Update its UI
         */
        for (x in 0 until rows) {
            for (y in 0 until columns) {
                board[x][y].value = 0
                board[x][y].isMarked = false
                board[x][y].isRevealed = false

                val button = getButton(x, y)
                button.text = ""
                button.isEnabled = true
                button.setBackgroundResource(R.drawable.button)
            }
        }

        text_mines.text = "$flagCount"
        //Reset the mine button
        btn_flag.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flag))
    }

    override fun onPause() {
        super.onPause()
        /*
        If the activity pauses, then set the wasRunning parameter as the current running variable
        and pause the timer
         */
        wasRunning = running
        running = false
    }

    override fun onResume() {
        super.onResume()
        /*
        When activity resumes, the if the timer was running before pause, then resume the timer
        also ie make running true
         */
        if (wasRunning)
            running = true
    }

    private fun setupBoard() {
        //parameters for linear layout
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )

        //parameters for buttons ie board boxes
        val params2 = LinearLayout.LayoutParams(
            0,
            150
        )

        for (i in 0 until rows) {
            //create linear layout and set its attributes
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = params1
            params1.weight = 1.0F

            for (j in 0 until columns) {

                val button = Button(this)
                button.layoutParams = params2
                params2.weight = 1.0F
                button.setBackgroundResource(R.drawable.button)

                button.setOnClickListener {
                    //calls the function if any button is clicked and passes its position
                    recordMove(i, j)
                }
                linearLayout.addView(button) //add the created button to the created linear layout
            }

            linear_layout_board.addView(linearLayout) //add the created liner layout to the main layout
        }
        //initialize the remaining mines count and timer
        flagCount = mines
        text_mines.text = "$flagCount"
        text_time.text = "$time"

    }

    private fun startTimer() {
        //timer uses Runnable and Handler classes to work properly
        runnable = Runnable { doJob() }

        //Handler is used to perform action after each second
        handler.post(runnable)
    }

    private fun doJob() {
        //this function converts second to hours and minutes
        val hours: Int = seconds / 3600
        val minutes: Int = seconds % 3600 / 60
        val secs: Int = seconds % 60

        //convert the elapsed time to proper format ie H:MM:SS
        time = String.format(
                Locale.getDefault(),
                "%d:%02d:%02d", hours,
                minutes, secs
            )

        //update the text view
        text_time.text = time

        //increment the seconds by 1 after each
        if (running)
            seconds++

        //to set a delay of 1 second ie 1000 milli-seconds
        handler.postDelayed(runnable, 1000)
    }

    private fun recordMove(x: Int, y: Int) {
        //get the selected button
        val button = getButton(x, y)

        /*
        If the count==0 ie user played his first move, then 3 things are done :-
        1. Mines are set
        2. Immediate non-mine neighbours to the selected button are revealed
        3. Timer is started
         */
        if (count == 0) {
            count++
            //start the timer
            running = true
            //set the mines
            setMines(x, y)
            //reveal immediate non-mine neighbours
            for (i in movement) {
                for (j in movement) {
                    if (!(i == 0 && j == 0) && ((x + i) in 0 until rows) && ((y + j) in 0 until columns)) {
                        reveal(x + i, y + j) //reveal the neighbours
                    }
                }
            }
        }

        if (flag) {
            /*
            This toggles the button state as flagged or not
             */
            if (board[x][y].isMarked) {
                //If the button is already marked as flag, then un-mark it and update its attributes to default state
                board[x][y].isMarked = !board[x][y].isMarked
                button.text = "" //set the button as blank as it is not flagged and not revealed now
                button.setBackgroundResource(R.drawable.button)
                //update the flag count
                flagCount++
                text_mines.text = "$flagCount"
            } else {
                /*
                If the flagCount=0, it means user has marked all the mines as flag. Hence, no
                further flags are allowed.
                 */
                if (flagCount > 0) {
                    //Mark the button as flag
                    board[x][y].isMarked = !board[x][y].isMarked
                    //update the button UI
                    button.setBackgroundResource(R.drawable.flag_button)
                    button.background = ContextCompat.getDrawable(this, R.drawable.flag)
                    //Decrease the flag count
                    flagCount--
                    text_mines.text = "$flagCount"
                }
            }
        } else {
            if (board[x][y].isMarked || board[x][y].isRevealed) {
                //If the button is marked as flag or has been already revealed, then do nothing
                return
            }
            if (board[x][y].value == MINE) {
                //If the user clicks a mine, then he losses the game
                gameLost()
            } else {
                //Reveal the button
                reveal(x, y)
            }
        }

        //check whether user has completed the game
        if (isComplete()) {
            //If true, it means user has won the game
            running = false //stop the timer
            disableAllButtons() //disable the board for further moves
            //update the user that he has won
            Toast.makeText(this, "Congratulations! You won.", Toast.LENGTH_LONG).show()
            updateScore("won") //update the scores
            showDialog() //show a share dialog
        }
    }

    private fun getButton(x: Int, y: Int): Button {
        //get the x row of the board
        val rowLayout = linear_layout_board.getChildAt(x) as LinearLayout

        //return the y button in the x row
        return rowLayout.getChildAt(y) as Button
    }

    private fun setMines(row: Int, column: Int) {
        /*
        row and column represent the position of the first button clicked. So that a mine is not
        set on it
         */
        var i = 1
        //set the number of mines selected by the user
        while (i <= mines) {
            //get a random position of button to be set as mine
            val x = (0 until rows).random()
            val y = (0 until columns).random()
            //checks validations and set the mine
            if (x != row && y != column && board[x][y].value != MINE) {
                board[x][y].value = MINE
                //to update the neighbouring button's mine count
                updateNeighbours(x, y)
                i++
            }
        }
    }

    private fun updateNeighbours(row: Int, column: Int) {
        /*
        This function takes the position of last button set as mine and update its neighbouring
        button's mine count
         */
        for (i in movement) {
            for (j in movement) {
                if (((row + i) in 0 until rows) && ((column + j) in 0 until columns) && board[row + i][column + j].value != MINE)
                //if the neighbour is in the board and is not a mine, its value is incremented
                //by 1 as a mine is placed in its neighbourhood
                    board[row + i][column + j].value++
            }
        }
    }

    private fun isComplete(): Boolean {
        /*
        This function returns true if all mines have been marked or all the non-mine buttons have
        been revealed
         */

        //this variable is true if all the mines have been marked as flag
        var minesMarked = true

        board.forEach { row ->
            row.forEach {
                if (it.value == MINE) {
                    if (!it.isMarked)
                        minesMarked = false
                }
            }
        }

        //this variable is true if all the non-mine buttons have been revealed
        var valuesRevealed = true
        //Iterate through all the buttons and check if all non-mine buttons have been revealed
        board.forEach { row ->
            row.forEach {
                if (it.value != MINE) {
                    if (!it.isRevealed)
                        valuesRevealed = false
                }
            }
        }

        return minesMarked || valuesRevealed
    }

    private fun gameLost() {
        //Reveal all the mines to make user aware of his wrong move
        revealAllMines()
        //Disable all buttons on board so that no further move is possible
        disableAllButtons()
        //stop the timer
        running = false
        //Update the user that he has lost the round
        Toast.makeText(this, "You Loose. Keep trying!", Toast.LENGTH_LONG).show()
        //update the scores
        updateScore("lost")
    }

    private fun reveal(x: Int, y: Int) {
        if (!board[x][y].isRevealed && !board[x][y].isMarked && board[x][y].value != MINE) {

            val button = getButton(x, y)
            button.text = board[x][y].value.toString() //sets the mine count as text
            button.isEnabled = false //disable the button for future clicks
            board[x][y].isRevealed = true //set the button isRevealed property to true

            button.setBackgroundResource(R.drawable.disable_button)
            button.setTextColor(ContextCompat.getColor(this, R.color.disabledButtonTextColor))

            /*
            If the value of button is 0 ie there is no mine in its neighbourhood,
            reveal all its neighbours
             */
            if (board[x][y].value == 0) {
                for (i in movement)
                    for (j in movement)
                        if (!(i == 0 && j == 0) && ((x + i) in 0 until rows) && ((y + j) in 0 until columns))
                            reveal(x + i, y + j)
            }
        }
    }

    private fun revealAllMines() {
        //Iterate through all the buttons and reveal them
        for (i in 0 until rows) {
            for (j in 0 until columns) {
                if (board[i][j].value == MINE) {
                    val button = getButton(i, j)
                    button.setBackgroundResource(R.drawable.mine)
                }
            }
        }
    }

    private fun disableAllButtons() {
        //Iterate through all the buttons on the board, disable them and update its UI
        for (x in 0 until rows) {
            for (y in 0 until columns) {
                val button = getButton(x, y) //get the required button
                button.isEnabled = false
                button.setTextColor(ContextCompat.getColor(this, R.color.disabledButtonTextColor))
            }
        }
    }

    private fun updateScore(status: String) {
        //Get the previous best time
        val sharedPreferences = getSharedPreferences("Scores", Context.MODE_PRIVATE)
        var bestTime = sharedPreferences.getInt("BEST_TIME", 0)

        if (status == "won") {
            //best time is only updated if user wins and his score is better than previous score
            if (bestTime == 0) {
                //if best time=0, it means user has won for the first time
                bestTime = seconds
            } else {
                if (seconds < bestTime)
                    bestTime = seconds
            }
        }
        //save the updated scores
        val sharedPreferencesUpdate = getSharedPreferences("Scores", Context.MODE_PRIVATE)
        with(sharedPreferencesUpdate.edit()) {
            putInt("BEST_TIME", bestTime)
            putInt("LAST_GAME_TIME", seconds)
            commit()
        }
    }

    private fun showDialog() {
        /*
        This function shows a dialog to the user.
        The dialog asks user whether he wants to share his score with his contacts or not
         */
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        with(builder) {
            setTitle("Share Your Win")
            setMessage("Let others know your great win. Do you wish to share your scores with your friends?")

            setPositiveButton("Yes") { dialog, which ->
                //if user wants to share his score, launch the required intent
                val intent = Intent(Intent.ACTION_SEND);
                val body =
                    "Hello, I won the great Minesweeper game. I finished in $seconds seconds."
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "I won !")
                intent.putExtra(Intent.EXTRA_TEXT, body)
                startActivity(Intent.createChooser(intent, "Share your win on..."))
            }
            setNegativeButton(
                "No"
            ) { dialog, which -> }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}