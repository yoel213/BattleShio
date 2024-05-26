package com.example.batttleshipv2.Main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Coordinate (val x: Int, val y: Int) : Parcelable {

    fun isSameCoordinate (coord: Coordinate): Boolean {
        return this.x == coord.x && this.y == coord.y
    }
}
enum class Orientation {

    HORIZONTAL {
        override fun translate(startPoint: Coordinate?, stepsToMove: Int): Coordinate {
            return Coordinate(
                startPoint!!.x + stepsToMove,
                startPoint.y
            )
        }
    },
    VERTICAL {
        override fun translate(startPoint: Coordinate?, stepsToMove: Int): Coordinate {
            return Coordinate(
                startPoint!!.x,
                startPoint.y + stepsToMove
            )
        }
    };

    abstract fun translate(startPoint: Coordinate?, stepsToMove: Int): Coordinate?
}