package com.example.batttleshipv2.Main.data

import android.os.Parcelable
import com.example.batttleshipv2.Main.FieldOccupiedException
import com.example.batttleshipv2.Main.InvalidShotException
import kotlinx.android.parcel.Parcelize

@Parcelize
class Board(var boardX: Int = 8, var boardY: Int = 8, var ships: ArrayList<Ship> = arrayListOf(),  var fieldStatus: Array<Array<Int>> = Array(boardX) { _ -> Array(boardY) { _ -> 0} }) :
    Parcelable {
    fun availableGrid(theShip: Ship): Boolean {
        val iterate: Iterator<Coordinate> = theShip.coords.iterator()
        while (iterate.hasNext()) {
            val fieldCoord: Coordinate = iterate.next()
            val x: Int = fieldCoord.x
            val y: Int = fieldCoord.y
            if (x >= boardX || y >= boardY) {
                throw FieldOccupiedException(fieldCoord, "Posion Invalida")
            }
            if (fieldStatus[x][y] != 0) {
                throw FieldOccupiedException(fieldCoord, "Posicion invalida")
            }
        }
        return true
    }
    fun availableShot(coord: Coordinate): Boolean {
        val x: Int = coord.x
        val y: Int = coord.y
        val field = fieldStatus[x][y]
        if (field == 0 || field == 2) {
            return true;
        }
        return false;
    }
    @Throws(FieldOccupiedException::class)
    fun putBoat(theShip: Ship) {
        val iterate: Iterator<Coordinate> = theShip.coords.iterator()
        while (iterate.hasNext()) {
            val placeCoord = iterate.next()
            val x = placeCoord.x
            val y = placeCoord.y
            if (fieldStatus[x][y] != 0) {
                throw FieldOccupiedException(placeCoord, "Posicion Invalida")
            } else {
                fieldStatus[x][y] = 2
            }
        }
        ships.add(theShip)
    }



    @Throws(InvalidShotException::class)
    fun boxOccuped(coord: Coordinate): Int {
        val x = coord.x
        val y = coord.y

        if (fieldStatus[x][y] == 1 || fieldStatus[x][y] == 3) {
            throw InvalidShotException(coord, "Ya has dado click aqui");
        } else if (fieldStatus[x][y] == 0) {
            fieldStatus[x][y] = 1
            return fieldStatus[x][y];
        } else {
            fieldStatus[x][y] = 3
            for (ship: Ship in this.ships) {
                if (ship.hasCoordinates(coord)) {
                    ship.hit(coord)
                }
            }
            return fieldStatus[x][y];
        }
    }

    fun boatKilledAdvise(theCoord: Coordinate): String {
        for (ship: Ship in this.ships) {
            if (ship.arDestroyed()) {
                return ship.ShipShape.shipName;
            }
        }
        return "";
    }

    fun scoreSum(theCoord: Coordinate?): Int {
        for (i in ships.indices) {
            if (ships[i].arDestroyed()) {
                val retval: Int = ships[i].ShipShape.points
                ships.removeAt(i)
                return retval
            }
        }
        return 0
    }

    fun gameOver(): Boolean {
        return ships.isEmpty()
    }

}