package com.example.batttleshipv2.Main.data

import android.os.Parcelable
import com.example.batttleshipv2.Main.FieldOccupiedException
import kotlinx.android.parcel.Parcelize
import java.util.*


@Parcelize
data class Player(var playerName: String, var score: Int = 0) : Parcelable {
    fun addScore(points: Int) {
        score += points
    }
    private var ship: Ship? = null

    fun generateShips(theBoard: Board) {
        val random = Random()
        val max: Int = theBoard.boardX;
        val directions: Array<Orientation> = Orientation.values()
        val ships: Array<ShipShape> = ShipShape.values()
        val shipMap: EnumMap<ShipShape, Boolean> = EnumMap(
            ShipShape::class.java)
        for (i in 0..4) {
            shipMap[ships[i]] = true
        }
        for (entry in shipMap.entries) {
            while (entry.value) {
                val dir: Orientation = directions[random.nextInt(directions.size)]
                val x: Int = random.nextInt(max);
                val y: Int = random.nextInt(max);
                var coord: Coordinate
                ship = Ship(entry.key)
                for (i in 0 until ship?.ShipShape!!.size) {
                    coord = if (dir == Orientation.VERTICAL) {
                        Coordinate(x + i, y)
                    } else {
                        Coordinate(x, y + i)
                    }
                    ship?.coords?.add(coord)
                }
                try {
                    if (theBoard.availableGrid(this.ship!!)) {
                        theBoard.putBoat(ship!!)
                        entry.setValue(false)
                    }
                } catch (foe: FieldOccupiedException) {
                    println("Error: $foe")
                    foe.printStackTrace()
                }
            }
        }
    }

    fun tryingBoat(theBoard: Board, theShip: Ship, startPoint: Coordinate) {
        var coord: Coordinate
        for (i in 0 until theShip.ShipShape.size) {
            coord = if (theShip.orientation == Orientation.VERTICAL) {
                Coordinate(
                    startPoint.x + i,
                    startPoint.y
                )
            } else {
                Coordinate(
                    startPoint.x,
                    startPoint.y + i
                )
            }
            theShip.coords.add(coord)
        }
        try {
            if (theBoard.availableGrid(theShip)) {
                theBoard.putBoat(theShip)
            }
        } catch (foe: FieldOccupiedException) {
            throw FieldOccupiedException(startPoint, foe.toString())
        }

    }

    }