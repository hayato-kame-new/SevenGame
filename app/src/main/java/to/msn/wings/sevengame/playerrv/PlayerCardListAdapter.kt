package to.msn.wings.sevengame.playerrv

import android.content.Intent
import android.graphics.Color
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * コンストラクタの引数を増やしています.
 * コンストラクタの 第2引数以降を追加して データをフラグメントから受け取る. このクラスのプロパティとしてセットされます.
 *
 */
class PlayerCardListAdapter(
    private val data: List<PlayerListItem>, // 第一引数はRecyclerViewにバインドするデータです
    private val cardSet: Set<String>,
    private val tableCardData: List<ListItem>,
    private val comAList : List<PlayerListItem>,
    private val comBList : List<PlayerListItem>
) : RecyclerView.Adapter<PlayerCardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false

   // コンストラクタの引数で渡ってきたものをフィールド値にセットします
    //  変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
   //  ここでは onCreate()の後に呼ばれる onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _cardSet :Set<String>  // 宣言だけ
    private lateinit var _tableCardData :List<ListItem>  // 宣言だけ
    // コンストラクタの val data は　読み取り専用だから これも 違う変数名で新しく フィールドとして宣言します
    private lateinit var _deepDataList : List<PlayerListItem>  // 宣言だけ　　onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _comADeepList : List<PlayerListItem> // 宣言だけ
    private lateinit var _comBDeepList : List<PlayerListItem> // 宣言だけ

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // ここで lateinit varフィールドへ 初期値をセットします
        // 引数で渡ってきた値をフィールドへ初期値として代入してる onCreate()をオーバーライドして そこで代入してもいい
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！
        // _cardSet = cardSet
        _cardSet = HashSet<String>(cardSet)  // ディープコピーすること 同じ参照にしないこと
        _tableCardData = tableCardData
        // _data = data として同じ参照を入れてはいけない!!、バインドが終わるまで dataも変わってしまってはいけないからです ディープコピーをすること！！！
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ 変数の型はList<PlayerListItem>型 中身はArrayList<PlayerListItem>実装クラス型
         _deepDataList = ArrayList<PlayerListItem>(data) // ディープコピーすること 同じ参照にしないこと
         _comADeepList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと
         _comBDeepList = ArrayList<PlayerListItem>(comBList) // ディープコピーすること 同じ参照にしないこと
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
         //   Log.i("ok", txtViewPTag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)

            val context = holder.itemView.context // MainActivity が取得できてる

            if (_cardSet.contains(txtViewPTag.text.toString()) == true) {
                val intent = Intent(context, MainActivity::class.java)  // MainActivityから MainActivityへデータを送り 戻る

                //  含まれるタグが 8以上 12以下の時には +1の数のカード　   13の時は 1のカード を加える
                // 含まれるタグが 2以上 6以下の時には -1の数のカード  1の時は 13のカード を加えます
                val mark = (txtViewPTag.text.toString()).substring(0, 1)  // "S" とか
                val numInt = (txtViewPTag.text.toString()).substring(1).toInt()  // 1　とか
                val rangeMore: IntRange = 8..12
                val rangeLess: IntRange = 2..6
                var strNum = ""
                if (numInt in rangeMore) {
                    strNum = (numInt + 1).toString()  // String型の "9" "10 "11" "12" "13"
                } else if (numInt in rangeLess) {
                    strNum = (numInt - 1).toString()  // String型の "5" "4" "3" "2" "1"
                } else if (numInt == 13) {  // 13を出したら、1しか置けなくなるから
                    strNum = "1"
                } else if (numInt == 1) {  // 1を出したら 13しか置けなくなるから
                    strNum = "13"
                }
                val addStr = mark + strNum

                // addStr が "S1"  だったら、置けるリストの中に 同じマークで 8以上 13以下のタグがあれば除いてください
                // そして "S1"を 置けるリストに add してください Setでは指定された要素がセット内になかった場合に追加してくれます 重複はしない
                //  addStr が "S13" だったら、置けるリストの中に、もし スペードで １以上 ６以下のタグがあれば除いてください
                // そして "S13"を 置けるリストに add してください

                var muSet : MutableSet<String>  = _cardSet as MutableSet<String>

                muSet.add(addStr)  // 追加する
                // ここまでOK
                val ite = muSet.iterator()  // 元のコレクションmuSet を書き換えます エラーなしで
                while (ite.hasNext()){
                    val item = ite.next()
                    if (item.equals(txtViewPTag.text.toString())) {
                        ite.remove()  // MutableSetにしないと remove()が使えない　置いたからそれを削除
                    }

                }
             // ここまでの動きはOKです
                // 1 か 13　が出てきた時にチェックしてください！！

                if (strNum == "1") {  // まだ動き未確認です
                    while (ite.hasNext()){
                        val item = ite.next()
                        var m = item.substring(0 ,1)
                        var i = item.substring(1)
                        if (m.equals(mark)) {
                            if (i.toInt() >= 8 && i.toInt() <= 13) {
                                ite.remove()  // MutableSetにしないと remove()が使えない　置いたからそれを削除
                            }
                        }
                    }
                }
                if (strNum == "13") {  // まだ動き未確認です
                    while (ite.hasNext()){
                        val item = ite.next()
                        var m = item.substring(0, 1)
                        var i = item.substring(1)
                        if (m.equals(mark)) {
                            if (i.toInt() >= 1 && i.toInt() <= 6) {
                                ite.remove()  // MutableSetにしないと remove()が使えない　置いたからそれを削除
                            }
                        }
                    }
                }


                // 卓上カードのアイテムListItemの属性を変更する placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                for (item in _tableCardData) {
                    if (item.tag.equals(txtViewPTag.text)) {
                        item.placed = true
                    }
                }

                // _deepDataListは　中身は ArrayListだけど 変数の型は Listだから、ダウンキャストしないと removeが使えません
                val pArrayLi : MutableList<PlayerListItem> = _deepDataList as MutableList<PlayerListItem> // ダウンキャストなので 明示的キャスト

                // java.util.ConcurrentModificationException を回避するために forは使わないでください
                val iterator = pArrayLi.iterator()  // 元のコレクションを書き換えます エラーなしで
                while (iterator.hasNext()){
                    val item = iterator.next()
                    if (item.pTag.equals(txtViewPTag.text)) {
                        iterator.remove()
                    }
                }
                // トースト表示
                val toast: Toast = Toast.makeText(context, context.getString(R.string.putOn, txtViewPTag.text.toString()), Toast.LENGTH_LONG)
                toast.show()

                // 手持ちのカードが muSetに、存在したら、存在したものをリストで取得し、リストが要素がない空ならば、出せるカードがないのでパスをする
                // パスは ３回でき、4回目で 負け、負けになれば、手持ちカードを全て出し、ゲームを抜けること
                // パスをしたら、パスをしましたとトースト表示する パス(残り○回)とトーストに書く パス４回目で 負けましたと表示してあなたが負けたのでここで終了

                // もし、負けたら  ゲームはあなたの負けです でここで ダイアログを表示させて、 ダイアログ上 またゲームをするボタンだけ
                // を作っておく、またゲームをするボタンを押したら、intentに何もputせずに MainActivityへ遷移すると、初回になる




                // イテレータを使用して、元のpArrayLiに変更を加えています。それを intentで送ります
                // 注意点  PlayerListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要がる
                intent.putExtra("pArrayLi", pArrayLi as ArrayList<PlayerListItem>) // putExtraは ArrayList型でないとだめ
                // キャストが必要です Stringは Serializableインタフェースを実装してるので putExtraにそのままで渡せる
                intent.putExtra("cardSet", muSet as HashSet<String>)
                // _tableCardData を ArrayList<ListItem>ダウンキャストが必要です ダウンキャストは　明示的に キャスト演算子を使ってキャストさせます
                // 注意点 List<ListItem> の ListItemデータクラスは自作のクラスなので、intentで送るためには
                // ListItemデータクラスは Serializableインタフェースを実装する必要があります
                intent.putExtra("tList", _tableCardData as ArrayList<ListItem>)  // putExtraのために キャスト

                intent.putExtra("comAList", _comADeepList as ArrayList<ListItem>)
                intent.putExtra("comBList", _comBDeepList as ArrayList<ListItem>)
                // MainActivityへ遷移します
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