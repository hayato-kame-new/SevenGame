package to.msn.wings.sevengame.playerrv

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.R

class PlayerCardViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
    val pId = itemView.findViewById<TextView>(R.id.pId)
    val pNumber = itemView.findViewById<TextView>(R.id.pNumber)
    val pMark = itemView.findViewById<TextView>(R.id.pMark)
    val pTag = itemView.findViewById<TextView>(R.id.pTag)
}