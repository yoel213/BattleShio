package com.example.batttleshipv2.Main.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Ship(val ShipShape: ShipShape,
           var orientation: Orientation = Orientation.VERTICAL,
           var coords: ArrayList<Coordinate> = arrayListOf()) : Parcelable {

    fun hit(coord: Coordinate?): Unit {
        val iterator = coords.iterator()
        for(i in iterator){
            if(i.isSameCoordinate(coord!!)){
                println("Hit done")
                iterator.remove()
            }
        }
    }

    fun arDestroyed(): Boolean {
        return coords.isEmpty()
    }

    fun hasCoordinates(theCoord: Coordinate?): Boolean {
        for (coord in coords) {
            if (coord.isSameCoordinate(theCoord!!)) {
                println("Ship.hasCoordinates(): true")
                return true
            }
        }
        return false
    }

}
