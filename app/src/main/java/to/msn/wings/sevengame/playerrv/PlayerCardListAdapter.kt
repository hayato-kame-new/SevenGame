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
import to.msn.wings.sevengame.Game
import to.msn.wings.sevengame.MainActivity
import to.msn.wings.sevengame.PossibleCard
import to.msn.wings.sevengame.R
import to.msn.wings.sevengame.rv.ListItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * コンストラクタの引数を増やしています.
 * コンストラクタの 第2引数以降を追加して データをフラグメントから受け取る. このクラスのプロパティとしてセットされます.
 * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)
 * コンストラクタの 型は、将来他の型でも受け取れるように、ポリモーフィズム で　インタフェース型にしてますが、
 * 中身のオブジェクトは MutableList型ではなく ArrayList型にすること もちろん、重複なしが欲しいなら HashSetでOKです <E>のクラスは、Serializableインタフェースを実装してるので putExtraにそのままで渡せる
 */
class PlayerCardListAdapter(
    private val data: List<PlayerListItem>, // 第一引数はRecyclerViewにバインドするデータです
    private val possibleCardSet: Set<PossibleCard>,   // 引数の型はインタフェース型でいい
    private val tableCardData: List<ListItem>,  // 引数の型はインタフェース型でいい ポリモーフィズム
    private val comAList : List<PlayerListItem>,
    private val comBList : List<PlayerListItem>,
    private val _playerPassCounter : Int,
    private val _comAPassCounter : Int,
    private val _comBPassCounter : Int
) : RecyclerView.Adapter<PlayerCardViewHolder>() {

    // フィールド
    private val _isLayoutXLarge = false

    // 遅延して コンストラクタの引数で渡ってきたものをフィールド値にセットします
    //  変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    //  ここでは onCreate()の後に呼ばれる onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _deepPossibleCardSet: HashSet<PossibleCard>  // フィールド宣言だけ 　フィールドの型は実装の型にすること
    private lateinit var _tableCardData: ArrayList<ListItem>  // フィールド宣言だけ フィールドの型は実装の型にすること
    // コンストラクタの val data は　読み取り専用だから これも 違う変数名で新しく フィールドとして宣言します
    private lateinit var _deepDataList: ArrayList<PlayerListItem>  // フィールド宣言だけ　　onCreateViewHolderの中で 代入をして初期化しています フィールドの型は実装の型にすること
    private lateinit var _deepComAList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること
    private lateinit var _deepComBList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // ここで lateinit varフィールドへ 初期値をセットします (引数で渡ってきた値を使って)
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ ディープコピーにしないとエラー _cardSet = cardSet としてはいけない バインドが終わるまで cardSetも変わってしまってはいけないからです
        _deepPossibleCardSet = HashSet<PossibleCard>(possibleCardSet)  // ディープコピーすること (同じ参照にしないこと)
        // 中身は同じ ArrayListですが、引数で渡ってきた方はインタフェースの型の変数に入れていますので、ダウンキャストするには明示的なキャストが必要です
        // _tableCardData　要素の属性だけを変更するだけだから ディープコピーしなくてもいい 同じ参照のままでいい
        _tableCardData = tableCardData as ArrayList<ListItem>
        // _data = data として同じ参照を入れてはいけない!!、バインドが終わるまで dataも変わってしまってはいけないからです ディープコピーをすること！！！
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ 変数の型はList<PlayerListItem>型 中身はArrayList<PlayerListItem>実装クラス型
        _deepDataList = ArrayList<PlayerListItem>(data) // ディープコピーすること 同じ参照にしないこと
        _deepComAList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと
        _deepComBList = ArrayList<PlayerListItem>(comBList) // ディープコピーすること 同じ参照にしないこと
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
//        val context = holder.itemView.context
//        val intent = Intent(context, MainActivity::class.java)


        cardView.setOnClickListener {
            val txtViewPTag = it.findViewById<TextView>(R.id.pTag)  // ラムダの中で、クリックしたビューのタグを取得する 変数名に気を付ける
            //   Log.i("ok", txtViewPTag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)


           val context = holder.itemView.context // MainActivity が取得できてる
         //   val intent = Intent(context, MainActivity::class.java)



            val txtP = it.findViewById<TextView>(R.id.pTag)

            // まず、属性を見て判断する
            var decision: Boolean = false
            for (item in _deepPossibleCardSet) {
             //   if (item.tag.equals(txtViewPTag.text.toString())) {
                if (item.tag.equals(txtP.text.toString()) && item.possible == true) {
                    decision = item.possible
                }
            }
            // 判断した結果によって分岐する
            if (decision == true) {
                // クリックしたものはクリックしたものは置けるカードでしたので 処理をして、遷移する
               val intent = Intent(context, MainActivity::class.java)  // MainActivityから MainActivityへデータを送り 戻る

                // まずは プレイヤーの持ち手リスト_deepDataList から、出したカードを取り除く
                // java.util.ConcurrentModificationException を回避するために forは使わないでください
                val iterator = _deepDataList.iterator()  // 元のコレクションを書き換えます エラーなしで
                while (iterator.hasNext()){
                    val item = iterator.next()
                  //  if (item.pTag.equals(txtViewPTag.text)) {
                    if (item.pTag.equals(txtP.text)) {     // 修正した
                        iterator.remove()
                    }
                }
                // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _tableCardData) {
                   // if (item.tag.equals(txtViewPTag.text)) {
                   if (item.tag.equals(txtP.text)) {    // 修正した
                        item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                    }
                }
                // さらに、_deepPossibleCardSet の　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
             //   var itemDistance = 0
                for (item in _deepPossibleCardSet) {
                    if (item.tag.equals(txtP.text.toString())) {
                        item.placed = true  // 置いた
                        item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                     //   itemDistance = item.distance  // 6だったら -1 になる
                    }
                }

                // さらに、次に出せるカードの属性を変更する
                val game = Game()
             //   val mark = (txtP.text.toString()).substring(0, 1)  // "S" とか
                val numInt = (txtP.text.toString()).substring(1).toInt()  // 8　とか 6 とか
                val rangeMore: IntRange = 8..13
                val rangeLess: IntRange = 1..6
                val distanceMAX = 6
                val distanceMIN = -6

                if (numInt in rangeMore) {  // +1づつ 直近のものから調べる
                    for ( n in 1..6) {
                        // メソッドでインスタンスを取得して属性をチェックする  getNPossibleCard N個先のカードを取得する

                        var card = game.getNPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる
                            break // 抜ける
                            // ここまでの動きは OKです！！
                        }
                        // ループで 直近で  card.placed == falseの物を見つけていきます
                        if (card != null && card.placed == true && n == distanceMAX) {  // 13まで調べたら
                            for ( num in 1..6) {  // 数字が 1から6までのカードを調べる
                                var card = game.getPossibleCard(
                                    _deepPossibleCardSet,
                                    txtP.text.toString(),
                                    num
                                ) // 1のカードを取得

                                if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                                    card.possible = true // 可能に trueを入れる
                                    break // 抜ける
                                }
                                // また、 +1づつ直近から調べていって  6までみて 6も trueなら何もせずに抜ける
                            }
                        }

                    }
                } else if (numInt in rangeLess) {  // 1..6
                  // 6 なら itemDistance -1
                //    for ( n in distanceMIN downTo itemDistance) { //  -6　~　-1 の間を -1から順に調べたい時 downTo と使うと -1 から始まり逆順に -2 -3 -4 -5 -6 となる
                    for ( n in -1 downTo -6) {
                        // n が -1 ならば -1 -2 -3 -4 -5 -6 までループさせる
                        var card = game.getNPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる
                            break // 抜ける
                            // ここまでの動きは OKです！！
                        }

                        if (card != null && card.placed == true && n == distanceMIN) {  // 1まで調べたら n == -6 の時
                            for ( num in 8 downTo 13) {  // 数字が 13から8までのカードを調べる downTo と使うと 13から始まり逆順に 12 11 10 9 8 とループする
                                var card = game.getPossibleCard(
                                    _deepPossibleCardSet,
                                    txtP.text.toString(),
                                    num
                                ) // 13 のカードを取得
                                if (card != null && card.placed == false) {  // もし、まだ置いてないカードが見つかった時点で
                                    card.possible = true  // 可能に trueを入れる
                                    break  // 抜ける
                                }
                                // また、 -1づつ直近から調べていって 8もplacedが trueなら、何もせずにループは終わり
                            }
                        }
                    }
                }
                // トースト表示
                val toast: Toast = Toast.makeText(context, context.getString(R.string.put_on, txtP.text.toString()), Toast.LENGTH_SHORT)
                toast.show()
                  // 注意点 putExtraは リストの時には ArrayList型でないとだめ
               // 注意点  PlayerListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要がる
                intent.putExtra("data", _deepDataList as ArrayList<PlayerListItem> )
                //  自作したクラス PossibleCardを intentで送るためには Serializableインタフェースを実装します
                intent.putExtra("set", _deepPossibleCardSet as HashSet<PossibleCard>)
                // 注意点 ListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要があります
                intent.putExtra("tableCardData", _tableCardData as ArrayList<ListItem>)
                intent.putExtra("comAList", _deepComAList as ArrayList<PlayerListItem>)
                intent.putExtra("comBList", _deepComBList as ArrayList<PlayerListItem>)
                intent.putExtra("pPassCount", _playerPassCounter)  // そのまま渡すだけ
                intent.putExtra("comAPassCount", _comAPassCounter)  // そのまま渡すだけ
                intent.putExtra("comBPassCount", _comBPassCounter)  // そのまま渡すだけ
                // MainActivityへ遷移します
               //it.context
              //  it.context.startActivity(intent)
                context.startActivity(intent)  // もともとMainActivityは戻るボタンでいつでももどるので終わらせることはありません
            } else {
                // クリックしたものは置けないカードだったので トースト表示だけ 遷移しません パスをしたいなら、ボタンを押せるようにしてるから
                val toast: Toast = Toast.makeText(context, context.getString(R.string.uncontained), Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        // リスナーここまで
    }

    override fun getItemCount(): Int {
        return data.size
    }

}