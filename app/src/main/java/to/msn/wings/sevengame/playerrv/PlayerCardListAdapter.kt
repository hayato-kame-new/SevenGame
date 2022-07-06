package to.msn.wings.sevengame.playerrv

import android.graphics.Color
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


        val pMark: TextView = holder.itemView.findViewById(R.id.pMark)
        val pNumber: TextView = holder.itemView.findViewById(R.id.pNumber)
        val pTag: TextView = holder.itemView.findViewById(R.id.pTag)

        // バインドするときに もし、markが 1 4 なら 黒にする 2 3 なら 赤にする
        if (data[position].pMark.toString() == "1" || data[position].pMark.toString() == "4") {
            pMark.setTextColor(Color.parseColor("#FF000000"))
        } else if (data[position].pMark.toString() == "2" || data[position].pMark.toString() == "3") {
            pMark.setTextColor(Color.parseColor("#ff0000"))
        }
      //   pMark.visibility = View.GONE
        when(data[position].pMark.toString()) {
            "1" -> {
                pMark.setText("♠")

            }
            "2" -> {
                pMark.setText("♡")

            }
            "3" -> {
                pMark.setText("♦")

            }
            "4" -> {
                pMark.setText("♧")

            }
            else -> {  // "5" JOKERの時です 今回は JOKER無しなので使用していませんが
                pMark.setText("")

            }

        }


        // holder.itemView でルート要素のビュー の CardViewが取得できます
        val cardView : CardView = holder.itemView.findViewById(R.id.playerCardView);
        cardView.setOnClickListener {
            val pTag = it.findViewById<TextView>(R.id.pTag)
            Log.i("ok", pTag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}