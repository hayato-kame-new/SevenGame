package to.msn.wings.sevengame.rv

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.R

class CardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
   // val number = itemView.findViewById<TextView>(R.id.number)
    val mark = itemView.findViewById<TextView>(R.id.mark)
    val numberCenter = itemView.findViewById<TextView>(R.id.numberCenter)
  //  val numberDown = itemView.findViewById<TextView>(R.id.numberDown)
    val markDown = itemView.findViewById<TextView>(R.id.markDown)
    val placed = itemView.findViewById<TextView>(R.id.placed)
    val tag = itemView.findViewById<TextView>(R.id.tag)
}