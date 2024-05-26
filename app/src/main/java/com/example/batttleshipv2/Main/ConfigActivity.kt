package com.example.batttleshipv2.Main

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.batttleshipv2.Main.MainActivity.Companion.OWNER
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTROLE
import com.example.batttleshipv2.Main.MainActivity.Companion.CURRENTMATCH
import com.example.batttleshipv2.Main.MainActivity.Companion.RIVAL
import com.example.batttleshipv2.Main.data.Board
import com.example.batttleshipv2.Main.data.BoardGridAdapter
import com.example.batttleshipv2.Main.data.Ship
import com.example.batttleshipv2.Main.data.ShipListAdapter
import com.example.batttleshipv2.R
import com.example.batttleshipv2.getViewModel
import kotlinx.coroutines.*

class ConfigActivity : AppCompatActivity(), Animation.AnimationListener {

    companion object {
        const val BOARD = "BOARD"
        const val BOATS = "BOATS"
    }

    private lateinit var shipAdapter: ShipListAdapter
    private lateinit var boardAdapter: BoardGridAdapter
    private lateinit var startButton: Button
    private lateinit var rotateButton: Button
    private lateinit var shipsLayout: View
    private lateinit var boardGridView: GridView
    private lateinit var shipListView: ListView
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val viewModel by lazy {
        getViewModel { ConfigViewModel() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        startButton = findViewById(R.id.startButton)
        shipsLayout = findViewById(R.id.shipsLayout)
        boardGridView = findViewById(R.id.boardGridView)
        shipListView = findViewById(R.id.shipListView)
        rotateButton = findViewById(R.id.rotateButton)
        initObservers()
        initBoardAdapter()
        initShipAdapter()
        viewModel.initShips()
        viewModel.boatsVisibility = true
        updateVisibility()

        viewModel.actualMatch = intent.getStringExtra(CURRENTMATCH).toString()
        viewModel.roleName = intent.getStringExtra(CURRENTROLE).toString()

        viewModel.owner = intent.getParcelableExtra(OWNER)!!
        viewModel.rival = intent.getParcelableExtra(RIVAL)!!


        rotateButton.setOnClickListener {
            viewModel.rotateShip()
        }

        startButton.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(CURRENTMATCH, viewModel.actualMatch)
            bundle.putString(CURRENTROLE, viewModel.roleName)
            bundle.putParcelable(OWNER, viewModel.owner)
            bundle.putParcelable(RIVAL, viewModel.rival)
            bundle.putSerializable(BOARD, viewModel.board.fieldStatus)
            bundle.putParcelableArrayList(BOATS, viewModel.board.ships)

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtras(bundle)
            this.startActivity(intent)
            finish()
        }

    }

    private fun initObservers() {
        viewModel.apply {
            refreshBoardLiveData.observe(this@ConfigActivity,
                Observer { board -> refreshBoard(board) })
            shipsLiveData.observe(this@ConfigActivity,
                Observer { ships -> addDataToShipAdapter(ships) })
            blinkLiveData.observe(this@ConfigActivity,
                Observer { view -> setBlinkAnimation(view) })
            refreshShipsLiveData.observe(this@ConfigActivity,
                Observer { shipList -> refreshShips(shipList) })
        }
    }

    override fun onResume() {
        super.onResume()
        updateVisibility()
    }

    private fun updateVisibility() {
        if (!viewModel.startGameVisibility) {
            shipsLayout.visibility = if (viewModel.boatsVisibility) View.VISIBLE else View.GONE
        } else {
            startButton.visibility = View.VISIBLE
        }
    }

    private fun refreshBoard(board: Board) {
        boardAdapter.refresh(board.fieldStatus)
    }

    private fun initBoardAdapter() {
        boardAdapter =
            BoardGridAdapter(
                this,
                viewModel.board.fieldStatus
            )
            { view: View, position: Int -> handleBoardClick(view, position) }

        boardGridView.adapter = boardAdapter
    }


    private fun addDataToShipAdapter(ships: ArrayList<Ship>) {
        shipAdapter.refreshShipList(ships)
    }

    private fun refreshShips(shipList: ArrayList<Ship>) {
        shipAdapter.selectedPosition = -1
        shipAdapter.refreshShipList(shipList)

        if (viewModel.isShipListEmpty()) {
            rotateButton.visibility = View.GONE
            startButton.visibility = View.VISIBLE
            viewModel.startGameVisibility = true
        }
    }

    private fun initShipAdapter() {
        shipAdapter = ShipListAdapter(this)
        shipListView.adapter = shipAdapter

        shipListView.setOnItemClickListener { _, _, position, _ ->
            val selectedShip = shipAdapter.getItem(position) as Ship
            viewModel.selectedShip(selectedShip)
            shipAdapter.selectedPosition = position;
            shipAdapter.notifyDataSetChanged();
        }
    }

    private fun handleBoardClick(view: View, position: Int) {
        viewModel.handleBoardClick(view, position)
    }

    private fun setBlinkAnimation(view: View) {
        val animBlink: Animation =
            AnimationUtils.loadAnimation(this@ConfigActivity, R.anim.blink_in);
        animBlink.setAnimationListener(this@ConfigActivity)

        view.startAnimation(animBlink)

        scope.launch {
            delay(500)
            view.clearAnimation()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {

    }

    override fun onAnimationStart(animation: Animation?) {

    }

}