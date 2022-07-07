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
import java.util.function.Predicate

/**
 * コンストラクタの引数を増やしています.
 * コンストラクタの 第2引数以降を追加して データをフラグメントから受け取る. このクラスのプロパティとしてセットされます.
 *
 */
class PlayerCardListAdapter(
    private val data: List<PlayerListItem>, // 第一引数はRecyclerViewにバインドするデータです
    private val availableList: MutableList<String>,
    private val tableCardData: List<ListItem>
) : RecyclerView.Adapter<PlayerCardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false

   // コンストラクタの引数で渡ってきたものをフィールド値にセットします
    //  変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
   //  ここでは onCreate()の後に呼ばれる onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _availableList :MutableList<String>  // 宣言だけ
    private lateinit var _tableCardData :List<ListItem>  // 宣言だけ
    // コンストラクタの val data は　読み取り専用だから これも 違う変数名で新しく フィールドとして宣言します
    private lateinit var _deepDataList : List<PlayerListItem>  // 宣言だけ　　onCreateViewHolderの中で 代入をして初期化しています

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // ここで lateinit varフィールドへ 初期値をセットします
       _availableList = availableList // 引数で渡ってきた値をフィールドへ初期値として代入してる onCreate()をオーバーライドして そこで代入してもいい
        _tableCardData = tableCardData
        // _data = data として同じ参照を入れてはいけない!!、バインドが終わるまで dataも変わってしまってはいけないからです ディープコピーをすること！！！
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ 変数の型はList<PlayerListItem>型 中身はArrayList<PlayerListItem>実装クラス型
         _deepDataList = ArrayList<PlayerListItem>(data)

        return PlayerCardViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: PlayerCardViewHolder, position: Int) {
        holder.pId.text = data[position].pId.toString()  // data を使います
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
                // 卓上カードのアイテムListItemの属性を変更する placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                for (item in _tableCardData) {
                    if (item.tag.equals(txtViewPTag.text)) {
                        item.placed = true
                    }
                }
                // _tableCardData を ArrayList<ListItem>ダウンキャストが必要です ダウンキャストは　明示的に キャスト演算子を使ってキャストさせます
                // 注意点 List<ListItem> の ListItemデータクラスは自作のクラスなので、intentで送るためには
                // ListItemデータクラスは Serializableインタフェースを実装する必要があります
                intent.putExtra("tList", _tableCardData as ArrayList<ListItem>)  // putExtraのために キャスト
                // _deepDataListは　中身は ArrayListだけど 変数の型は Listだから、ダウンキャストしないと removeが使えません
                val pArrayLi : MutableList<PlayerListItem> = _deepDataList as MutableList<PlayerListItem> // ダウンキャストなので 明示的キャスト

                // java.util.ConcurrentModificationException を回避するために
                filter(pArrayLi, Predicate { item: PlayerListItem -> item.pTag.equals(txtViewPTag.text) })

                // 注意点  PlayerListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要がる
               intent.putExtra("pArrayLi", pArrayLi as ArrayList<PlayerListItem>) // putExtraは ArrayList型でないとだめ

                // トースト表示
                val toast: Toast = Toast.makeText(context, context.getString(R.string.putOn, txtViewPTag.text.toString()), Toast.LENGTH_LONG)
                toast.show()

                // 遷移する前に、ここで、コンピューター２つ分の処理もやってしまう。　同じように書く タイマーを使う


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

    fun <T> filter(list: MutableList<T>, predicate: Predicate<T>) {
        val itr = list.iterator()

        while (itr.hasNext())
        {
            val t = itr.next()
            if (predicate.test(t)) {
                itr.remove()
            }
        }
    }

}