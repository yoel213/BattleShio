package com.example.batttleshipv2.Main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.batttleshipv2.Main.data.*

class ConfigViewModel : ViewModel() {

    var board: Board = Board()
    var boats: ArrayList<Ship> = arrayListOf()

    var owner = Player("N/A", 0)
    var rival = Player("N/A", 0)
    var actualMatch: String = ""
    var roleName: String = ""
    val refreshBoardLiveData = MutableLiveData<Board>()
    val refreshShipsLiveData = MutableLiveData<ArrayList<Ship>>()
    val shipsLiveData = MutableLiveData<ArrayList<Ship>>()
    val blinkLiveData = MutableLiveData<View>()
    private var selectedShip: Ship? = null
    private var shipDirection = Orientation.VERTICAL

    var boatsVisibility = false
    var startGameVisibility = false



    fun initShips() {
        boats = arrayListOf()
        boats.apply {
            add(Ship(ShipShape.BOAT2))
            add(Ship(ShipShape.BOAT3))
            add(Ship(ShipShape.BOAT4))
            add(Ship(ShipShape.BOAT1))
            add(Ship(ShipShape.BOAT5))
        }
        shipsLiveData.value = boats
    }

    fun selectedShip(ship: Ship) {
        selectedShip = ship
        selectedShip?.orientation = shipDirection
    }
    fun rotateShip() {
        shipDirection = if (shipDirection == Orientation.VERTICAL)
            Orientation.HORIZONTAL
        else
            Orientation.VERTICAL
        selectedShip?.orientation = shipDirection
    }
    fun handleBoardClick(view: View, position: Int) {
        val x: Int = kotlin.math.floor((position / board.boardX).toDouble()).toInt()
        val y: Int = position % board.boardX

        if (selectedShip != null) {

            val ship = selectedShip

            try {
                owner.tryingBoat(board, ship!!, Coordinate(x, y))

                boats.remove(ship)
                selectedShip = null

                refreshBoardLiveData.value = board
                refreshShipsLiveData.value = boats

            } catch (foe: FieldOccupiedException) {
                blinkLiveData.value = view
                ship!!.coords.clear()
            }
        }
    }

    fun isShipListEmpty(): Boolean {
        return boats.isEmpty()
    }
}
