package com.example.batttleshipv2.Main.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.batttleshipv2.R

class BoardGridAdapter(private val context: Context, private val fieldStatus: Array<Array<Int>>,
                       private val clickListener: (View, Int) -> Unit): BaseAdapter() {

    var fields = fieldStatus.flatten()

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.board_box,null)
        val grid: GridView = parent as GridView
        val size = grid.columnWidth
        val cell = view.findViewById<TextView>(com.example.batttleshipv2.R.id.cellView)
        cell.setBackgroundColor(ContextCompat.getColor(context, getStatusColor(fields[position])))
        cell.width = size
        cell.height = size
        cell.setOnClickListener { clickListener(cell, position) }
        return view
    }

    private fun getStatusColor(status: Int): Int {
        return when (status) {
            0 -> R.color.ocean
            1 -> R.color.colorMissed
            2 -> R.color.gray_light
            else -> {
                R.color.colorPrimarySurface
            }
        }
    }

    override fun getItem(position: Int): Any? {
        return fields[position]
    }

    fun getItem(columnNum: Int, rowNum: Int): Any {
        return fieldStatus[columnNum][rowNum];
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return fields.size
    }

    override fun isEnabled(position: Int): Boolean {
        return super.isEnabled(position)
    }

    fun refresh(fieldStatus: Array<Array<Int>>) {
        fields = fieldStatus.flatten()
        notifyDataSetChanged()
    }
}
class ShipListAdapter(private val context: Context): BaseAdapter(){

    var selectedPosition: Int = -1
    private var dataSource = arrayListOf<Ship>()

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun refreshShipList(shipList: ArrayList<Ship>) {
        dataSource.clear()
        dataSource.addAll(shipList)
        notifyDataSetChanged()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.boat, parent, false)

        val shipView = rowView.findViewById(R.id.shipView) as TextView

        shipView.text = dataSource[position].ShipShape.shipName

        if (selectedPosition == position) {
            shipView.setTypeface(null, Typeface.BOLD)
        } else {
            shipView.setTypeface(null, Typeface.NORMAL)
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSource.size
    }
}
data class PersonScore(val name: String, val score: String)

class ScoreAdapter(val points: ArrayList<PersonScore>) : RecyclerView.Adapter<ScoreAdapter.PersonScoreHolder>() {

    class PersonScoreHolder(val view: View) : RecyclerView.ViewHolder(view){

        private val name: TextView
        private val score: TextView

        init {
            name = view.findViewById(R.id.itemname)
            score = view.findViewById(R.id.itemscore)
        }

        fun bind(puntos2: PersonScore){
            name.text = puntos2.name
            score.text = puntos2.score

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonScoreHolder {
        val rvItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_score, parent, false)
        return PersonScoreHolder(rvItem)
    }

    override fun onBindViewHolder(holder: PersonScoreHolder, position: Int) {
        holder.bind(points[position])
    }

    override fun getItemCount(): Int {
        return points.size
    }
}
