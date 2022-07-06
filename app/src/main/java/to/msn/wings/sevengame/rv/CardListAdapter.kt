package to.msn.wings.sevengame.rv

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.R


class CardListAdapter(private val _data: List<ListItem>) : RecyclerView.Adapter<CardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false

    /**
     * ビューホルダーを生成
     * ここでリスナーをつけることもできるが、onBindViewHolderでリスナーをつけた方がいい場合もある
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return CardViewHolder( cardView)
    }

    /**
     * ビューホルダーを生成した後によばれます.ビューにデータを割り当て、リスト項目を生成.data.sizeの数だけ実行されます
     *
     */
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        holder.mark.text = _data[position].mark.toString()
        holder.numberCenter.text = _data[position].numberCenter
        holder.markDown.text = _data[position].markDown.toString()
        holder.placed.text = _data[position].placed.toString()
        holder.tag.text = _data[position].tag

        // holder.itemView でルート要素のビューのCardView が取得できる
        val cardView : CardView = holder.itemView.findViewById(R.id.cardView);
        // 配下のウィジェットを取得　　holder.itemView はルートの要素の CardView そこからfindViewByIdを使うと取得できる
        val mark: TextView = holder.itemView.findViewById(R.id.mark)
        val numberCenter: TextView = holder.itemView.findViewById(R.id.numberCenter)
        val markDown: TextView = holder.itemView.findViewById(R.id.markDown)
        val placed: TextView = holder.itemView.findViewById(R.id.placed)
        val tag: TextView = holder.itemView.findViewById(R.id.tag)

        // バインドするときに もし、markが 1 4 なら 黒にする 2 3 なら 赤にする
        if (_data[position].mark.toString() == "1" || _data[position].mark.toString() == "4") {
            mark.setTextColor(Color.parseColor("#FF000000"))
            markDown.setTextColor(Color.parseColor("#FF000000"))
        } else if (_data[position].mark.toString() == "2" || _data[position].mark.toString() == "3") {
            mark.setTextColor(Color.parseColor("#ff0000"))
            markDown.setTextColor(Color.parseColor("#ff0000"))
        }
        // 非表示だけして、値は利用できるようにすること 置くときに値を変更する
        placed.visibility = View.GONE
        tag.visibility = View.GONE
        // バインドするときに data[position].mark.toString() によって 分岐させる
        // "0" は置かれていないので "" 空文字にする
        when(_data[position].mark.toString()) {
            "0" -> {  // おかれてないところ
                mark.visibility = View.GONE  // "0"になってるから 非表示にしておく "0"の値は判断するときに使うので非表示だけする おくときに値を変更する
                markDown.visibility = View.GONE
                // cardView.setCardBackgroundColor(null) // nullにはしない方がいい
                cardView.setCardBackgroundColor(Color.parseColor("#006c3a"))  // 色を重ねがけする方がいい
            }
            "1" -> {
                mark.setText("♠")
                markDown.setText("♠")
            }
            "2" -> {
                mark.setText("♡")
                markDown.setText("♡")
            }
            "3" -> {
                mark.setText("♦")
                markDown.setText("♦")
            }
            "4" -> {
                mark.setText("♧")
                markDown.setText("♧")
            }
            else -> {  // "5" JOKERの時です 今回は JOKER無しなので使用していませんが
                mark.setText("")
                markDown.setText("")
            }
        }

        /**
         * rvを上に被せてると押せる でも、ここでは押せなくてもいい
         */
//        cardView.setOnClickListener {   // it　は CardViewです
//            val tag = it.findViewById<TextView>(R.id.tag)
//            Log.i("ok", tag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)
//        }

    }

    /**
     * データのバインドはこの回数実行されます
     */
    override fun getItemCount(): Int {
        return _data.size
    }
}