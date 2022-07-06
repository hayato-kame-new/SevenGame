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

/**
 * コンストラクタの引数を増やしています
 */
class CardListAdapter(private val data: List<ListItem>, placeableList : MutableList<String>) : RecyclerView.Adapter<CardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false
    // 引数のままでも プロパティとして使えるけど
    private val _placeableList : MutableList<String> = placeableList  // コンストラクタの引数で渡ってきたものをフィールド値にセットします

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
       // holder.number.text = data[position].number
        holder.mark.text = data[position].mark.toString()
        holder.numberCenter.text = data[position].numberCenter
      //  holder.numberDown.text = data[position].numberDown
        holder.markDown.text = data[position].markDown.toString()
        holder.placed.text = data[position].placed.toString()
        holder.tag.text = data[position].tag

        // holder.itemView でルート要素のビューのCardView が取得できる
        val cardView : CardView = holder.itemView.findViewById(R.id.cardView);
        // holder.itemView はルートの要素の CardView そこからfindViewByIdを使って配下のウィジェットを取得
       // val number: TextView = holder.itemView.findViewById(R.id.number)
        val mark: TextView = holder.itemView.findViewById(R.id.mark)
        val numberCenter: TextView = holder.itemView.findViewById(R.id.numberCenter)
       // val numberDown: TextView = holder.itemView.findViewById(R.id.numberDown)
        val markDown: TextView = holder.itemView.findViewById(R.id.markDown)
        val placed: TextView = holder.itemView.findViewById(R.id.placed)
        val tag: TextView = holder.itemView.findViewById(R.id.tag)

        // バインドするときに もし、markが 1 4 なら 黒にする 2 3 なら 赤にする
        if (data[position].mark.toString() == "1" || data[position].mark.toString() == "4") {
            mark.setTextColor(Color.parseColor("#FF000000"))
            markDown.setTextColor(Color.parseColor("#FF000000"))
        } else if (data[position].mark.toString() == "2" || data[position].mark.toString() == "3") {
            mark.setTextColor(Color.parseColor("#ff0000"))
            markDown.setTextColor(Color.parseColor("#ff0000"))
        }
        // 非表示だけして、値は利用できるようにすること 置くときに値を変更する
        placed.visibility = View.GONE
        tag.visibility = View.GONE
        // バインドするときに data[position].mark.toString() によって 分岐させる
        // "0" は置かれていないので "" 空文字にする
        when(data[position].mark.toString()) {
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
         * rvを上に被せてるとにしてると押せる
         */
        cardView.setOnClickListener {   // it　は CardViewです
            val tag = it.findViewById<TextView>(R.id.tag)
            Log.i("ok", tag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)

        }

    }

    /**
     * データのバインドはこの回数実行されます
     */
    override fun getItemCount(): Int {
        return data.size
    }
}