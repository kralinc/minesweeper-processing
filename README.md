# minesweeper-processing
This was a project I created in April 2019. The primary goal was to test my new scene interface, which would let me make more complex games.

The interface works by implementing its own versions of processing specific methods, such as draw(), start(), onMouseClicked(), and a few others.
These classes are all put into an array in the main file, and the index of the current scene can be switched from within each scene.
The methods of the class that the index points to are run in the main file.
Some room for improvement still remains, such as handling the values of variables which must remain consistent across scenes.
The current method of doing so simply makes such variables global, however I am sure that a better way exists.

I tried to make clicking on tiles as efficient as possible. Each of the tiles are loaded into a 2 dimensional array, then when a tile is clicked, the mouse coordinates are converted to an index. This way no iteration is required.
Iteration is done when rendering tiles. I will look into more efficient ways of doing this.

Playing the game is very straightforward, with instructions given on the main menu. One must already know how to play minesweeper though.
This repository contains packages of the completed executable for windows and linux. Simply download the one for your system, and run.
