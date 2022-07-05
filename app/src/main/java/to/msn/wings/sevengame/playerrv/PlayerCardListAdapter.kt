package to.msn.wings.sevengame.playerrv

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.R

class PlayerCardListAdapter (private val data: List<PlayerListItem>) : RecyclerView.Adapter<PlayerCardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        return PlayerCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: PlayerCardViewHolder, position: Int) {
        holder.pId.text = data[position].pId.toString()
        holder.pNumber.text = data[position].pNumber
        holder.pMark.text = data[position].pMark.toString()
        holder.pTag.text = data[position].pTag

        // holder.itemView でルート要素のビュー の CardViewが取得できます
        val cardView : CardView = holder.itemView.findViewById(R.id.playerCardView);
        cardView.setOnClickListener {
            Log.i("ok", it.toString() + "です" + it.javaClass)  // it　は CardViewですね！！
            val pTag = it.findViewById<TextView>(R.id.pTag)
            Log.i("ok", pTag.text.toString() + "です")
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}