package com.example.batttleshipv2.Main

import com.example.batttleshipv2.Main.data.Coordinate

open class Rules (message: String = ""): Exception(message)

// Exception class for handling situations when a ship is already on the board
class FieldOccupiedException (problemCoord: Coordinate, message: String = ""): Rules()

// Exception-class for handling problematic shots fired
class InvalidShotException (problemCoord: Coordinate, message: String = ""): Rules()