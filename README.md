# MINESWEEPER

-Minesweeper is a single-player puzzle video game. The objective of the game is to clear a rectangular board containing hidden "mines" or bombs without detonating any of them, with help from clues about the number of neighbouring mines in each field.

-Working of app :-
 The app containes 2 activities- MainActivity as launcher activity and GameActivity.
 1. The main activity displays the user his best game time and last game time.
    The user has the option to choose from following levels :-
     a) Easy - This levels has 9 rows, 9 columns and 20 mines.
     b) Medium - This levels has 16 rows, 16 columns and 63 mines. This level is selected by default.
     c) Hard - This levels has 30 rows, 16 columns and 119 mines.
     d) Custom - In this level, user chooses the number of rows, columns and mines in the game.
    The number of mines is always less than 1/4th of the board’s buttons to avoid overcrowding of mines.
   
 2. The game activity contains the final game. The top bezel displays information about the game (number of mines left to be flagged and timer) as well as provides buttons to restart the game and choose flag. UI is dynamic as it changes according to level selected by the user.
	The image button next to restart button toggles the flag state. By default, the reveal option is selected and it shows flag image.
	The first click is free, i.e. the mines should be set on the first click to ensure that the user’s first click doesn’t detonate a mine.
	The timer starts when user plays his first move and stops when user wins or looses the game. The time turns into pause state if activity is paused and resumes when activity resumes.
	If a button is revealed, then it is disabled and no more clicks are allowed on it. User cannot reveal a button marked as flag. When a button is flagged, it shows flag image.
	At the end of the game, the game time is stored and board buttons are disabled. High score ie best game time is only updated if the user wins the game. The high scores are displayed on the main activity along with their last game time.
	If the user wins the round, he can share his playing time with his friends in the form of message with the help of messaging apps (like Gmail, Whatsapp, etc) installed on his device.
	