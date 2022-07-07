package to.msn.wings.sevengame.playerrv

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.MainActivity
import to.msn.wings.sevengame.R
import to.msn.wings.sevengame.rv.ListItem
import java.util.ArrayList

/**
 * コンストラクタの引数を増やしています.
 * コンストラクタの 第2引数で、データをフラグメントから受け取ることができます このクラスのプロパティとしてセットされます.
 * Kotlin「クラスのコンストラクタ引数」に使われるアンダースコア
 */
class PlayerCardListAdapter(
    private val data: List<PlayerListItem>,
    private val availableList: MutableList<String>,
    private val tableCardData: List<ListItem>,
  //  private val playerList: MutableList<String>,
) : RecyclerView.Adapter<PlayerCardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false

   // コンストラクタの引数で渡ってきたものをフィールド値にセットします
    //  変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
   //  ここでは onCreate()の後に呼ばれる onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _availableList :MutableList<String>
    private lateinit var _tableCardData :List<ListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // フィールドへセット
       _availableList = availableList // 引数で渡ってきた値をフィールドへ初期値として代入してる onCreate()をオーバーライドして そこで代入してもいい
        _tableCardData = tableCardData
        return PlayerCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: PlayerCardViewHolder, position: Int) {
        holder.pId.text = data[position].pId.toString()
        holder.pNumber.text = data[position].pNumber
        holder.pMark.text = data[position].pMark.toString()
        holder.pTag.text = data[position].pTag

        val pId: TextView = holder.itemView.findViewById(R.id.pId)
        val pMark: TextView = holder.itemView.findViewById(R.id.pMark)
        val pNumber: TextView = holder.itemView.findViewById(R.id.pNumber)
        val pTag: TextView = holder.itemView.findViewById(R.id.pTag)

        // バインドするときに もし、markが 1 4 なら 黒にする 2 3 なら 赤にする
        if (data[position].pMark.toString() == "1" || data[position].pMark.toString() == "4") {
            pMark.setTextColor(Color.parseColor("#FF000000"))
        } else if (data[position].pMark.toString() == "2" || data[position].pMark.toString() == "3") {
            pMark.setTextColor(Color.parseColor("#ff0000"))
        }
        pId.visibility = View.GONE
        pTag.visibility = View.GONE
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
        val cardView : CardView = holder.itemView.findViewById(R.id.playerCardView)
        // ラムダ式の中は、innerのついた内部クラスなので、外側のクラスのメンバにアクセスできる (innerがついればアクセスできるから)
        cardView.setOnClickListener {
            val txtViewPTag = it.findViewById<TextView>(R.id.pTag)  // ラムダの中で、クリックしたビューのタグを取得する 変数名に気を付ける
            Log.i("ok", txtViewPTag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)
            Log.i("ok", _availableList.contains(txtViewPTag.text.toString()).toString())
            val context = holder.itemView.context // MainActivity が取得できてる
            // 置けるカードならば遷移します リストの中身との比較で判断する
            if (_availableList.contains(txtViewPTag.text.toString()) == true) {
                // 含まれていたら  _availableListの中から、削除する また,プレイヤーリストからも除く 卓上には表示させる
                val intent = Intent(context, MainActivity::class.java)  // MainActivityから MainActivityへデータを送り 戻る
                _availableList.remove(txtViewPTag.text.toString())
                // キャストが必要です
                intent.putStringArrayListExtra("aList", _availableList as ArrayList<String>)



                for ( item in _tableCardData) { // 卓上カードのアイテムの属性を変更する
                    if (item.tag.equals(txtViewPTag.text)) {
                        item.placed = true
                    }
                }
                for ( item in _tableCardData) { // 卓上カードのアイテムの属性を変更する
                    Log.i("t" , item.placed.toString() + item.tag.toString())
                }

              //  intent.putStringArrayListExtra("tList", _tableCardData as ArrayList<String>)
                intent.putExtra("tList", _tableCardData as ArrayList<ListItem>)
               //  intent.putExtra("pTag", pTag.text.toString() )
                val toast: Toast = Toast.makeText(context, context.getString(R.string.putOn, txtViewPTag.text.toString()), Toast.LENGTH_LONG)
                toast.show()
                context.startActivity(intent)  // もともとMainActivityは戻るボタンでいつでももどるので終わらせることはありません
            } else {
                // 置けないカードだったら、トースト表示だけ
                val toast: Toast = Toast.makeText(context, context.getString(R.string.uncontained), Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}