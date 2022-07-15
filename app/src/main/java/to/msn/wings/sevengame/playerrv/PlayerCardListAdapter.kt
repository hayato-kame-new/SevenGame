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
import kotlin.properties.Delegates

/**
 * コンストラクタの引数を増やしています.
 * コンストラクタの 第2引数以降を追加して データをフラグメントから受け取る. このクラスのプロパティとしてセットされます.
 * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)
 * コンストラクタの 型は、将来他の型でも受け取れるように、ポリモーフィズム で　インタフェース型にしてますが、
 * 中身のオブジェクトは MutableList型ではなく ArrayList型にすること  <E>のクラスは、Serializableインタフェースを実装してることによって putExtraにそのままで渡せる
 */
class PlayerCardListAdapter(
    private val data: List<PlayerListItem>, // 第一引数はRecyclerViewにバインドするデータです
    private val possibleCardList: List<PossibleCard>,   // 引数の型はインタフェース型でいい
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
  //  private lateinit var _deepPossibleCardSet: HashSet<PossibleCard>  // フィールド宣言だけ 　フィールドの型は実装の型にすること
    private lateinit var _deepPossibleCardList: ArrayList<PossibleCard>  // フィールド宣言だけ 　フィールドの型は実装の型にすること
    private lateinit var _tableCardData: ArrayList<ListItem>  // フィールド宣言だけ フィールドの型は実装の型にすること
    // コンストラクタの val data は　読み取り専用だから これも 違う変数名で新しく フィールドとして宣言します
    private lateinit var _deepDataList: ArrayList<PlayerListItem>  // フィールド宣言だけ　　onCreateViewHolderの中で 代入をして初期化しています フィールドの型は実装の型にすること
    private lateinit var _deepComAList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること
    private lateinit var _deepComBList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // ここで lateinit varフィールドへ 初期値をセットします (引数で渡ってきた値を使って)
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ ディープコピーにしないとエラー _cardSet = cardSet としてはいけない バインドが終わるまで cardSetも変わってしまってはいけないからです
      //  _deepPossibleCardSet = HashSet<PossibleCard>(possibleCardSet)  // ディープコピーすること (同じ参照にしないこと)
        _deepPossibleCardList = ArrayList<PossibleCard>(possibleCardList)  // ディープコピーすること (同じ参照にしないこと)


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
            //   Log.i("ok", txtViewPTag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)
           val context = holder.itemView.context // MainActivity が取得できてる
            // 変数名内部クラスの外と衝突しないように気を付けること
            val txtP = it.findViewById<TextView>(R.id.pTag) // ラムダの中で、クリックしたビューのタグを取得する 外の変数名と同じにならないように気を付ける

            // まず、属性を見て判断する
            var decision: Boolean = false
            for (item in _deepPossibleCardList) {
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
                    if (item.pTag.equals(txtP.text)) {     // 修正した
                        iterator.remove()
                    }
                }
                // ここで、_deepDataList　の要素数が 0　になったら、
                // あなたの勝ちです！！ ゲーム終了 一番先に 手持ちが 0になった人の勝ちで ゲーム終章
                 // if else追加すること



                // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _tableCardData) {
                   // if (item.tag.equals(txtViewPTag.text)) {
                   if (item.tag.equals(txtP.text)) {    // 修正した
                        item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                    }
                }

                // さらに、_deepPossibleCardSet の　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _deepPossibleCardList) {
                    if (item.tag.equals(txtP.text.toString())) {
                        item.placed = true  // 置いた
                        item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                    }
                }

                // ここからまだできてない

                // さらに、次に出せるカードの属性を変更する _deepPossibleCardSet
                val game = Game()

                val pTagStr: String = txtP.text.toString()  // 置いたカードのタグの文字列 "S6" とか
                val putCardMark: String = pTagStr.substring(0, 1) // "S" とか
                val putCardNum: Int = pTagStr.substring(1).toInt()  // 6 とか


                // _deepPossibleCardSetの属性を変更させるので forでできるが、とりあえず _deepPossibleCardSetをサブリストにする
                // _deepPossibleCardListを 8分割する  元のコレクションに影響与えないで作っています
                // "S1" から "S6"
                val subSSmall = game.getSubList(_deepPossibleCardList, 0, (_deepPossibleCardList.size / 8) - 1 ) as ArrayList<PossibleCard>
                // 7のカードは含めないで作る "S8" から "S13"
                val subSBig = game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size / 8) + 1, (_deepPossibleCardList.size * 2 / 8) - 1 ) as ArrayList<PossibleCard>

                // "H1" から "H6"
                val subHSmall = game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 2 / 8, (_deepPossibleCardList.size * 3 / 8) - 1 ) as ArrayList<PossibleCard>
                // 7のカードは含めないで作る "H8" から "H13"
                val subHBig = game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 3 / 8) + 1, (_deepPossibleCardList.size * 4 / 8) - 1 ) as ArrayList<PossibleCard>

                // "D1" から "D6"
                val subDSmall = game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 4 / 8, (_deepPossibleCardList.size * 5 / 8) - 1 ) as ArrayList<PossibleCard>
                // 7のカードは含めないで作る "D8" から "D13"
                val subDBig = game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 5 / 8) + 1, (_deepPossibleCardList.size * 6 / 8) - 1 ) as ArrayList<PossibleCard>

                // "C1" から "C2"
                val subCSmall = game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 6 / 8, (_deepPossibleCardList.size * 7 / 8) - 1 ) as ArrayList<PossibleCard>
                // 7のカードは含めないで作る "C8" から "C13"
                val subCBig = game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 7 / 8) + 1, _deepPossibleCardList.size - 1 ) as ArrayList<PossibleCard>

               if (putCardMark.equals("S") && (putCardNum >= 1 && putCardNum <= 6)) {
                   for (num in 5 downTo 1) {
                        // メソッドでインスタンスを取得して属性をチェックする
                        var card =
                            game.getPossibleCard(subSSmall, pTagStr, num)  // サブリストから取得してくる
                        if (card != null && card.placed == false) { // もし、5から１まで　サブリストに まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                            break // 抜ける
                        }
                       // 1を置いた時に もし、普通の向きならばこれで判定できるので
                       if (card != null && card.placed == true) {   // 普通の向きで 1まで並べた時には   5 4 3 2 1 は既に placed == trueであるので これで順番は普通の向きだと断定できる
                           // 13から置くように変更する
                           for (num in 13 downTo 8) {
                               var card =
                                   game.getPossibleCard(subSBig, pTagStr, num)  // 13から順にサブリストから取得してくる
                               // 余計な possibleをクリアする
                                if (card != null && card.placed == false && card.possible == true) {
                                    card.possible = false //全部　置ける を 一旦 置けない でクリアしておく 全部だから breakはしない
                                }
                           }
                           // もう一度別のループで設定し直す
                           for ( num in 13 downTo 8) {
                               var card = game.getPossibleCard(subSBig, pTagStr, num)  // 13から順にサブリストから取得してくる
                               // 設定し直しする
                               if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                                   card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
                                   break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
                               }
                           }
                       }
                    }

               }






//  _deepPossibleCardList

//                if (numInt in rangeMore ) {
//                    for (n in 9..13) {
//                        // メソッドでインスタンスを取得して属性をチェックする
//                        var card =
//                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる
//                            break // 抜ける
//                        }
//                    }
//                } else if (numInt == 13) {
//                    // １つ目のループで 先にクリアしてから
//                    for ( num in 1..6) {  // 数字が 1から6までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // 先にクリア
//                        if (card != null && card.placed == false && card.possible == true) {
//                            card.possible = false // 1から6までのカード全部　置ける を 一旦 置けない でクリアしておく
//                        }
//                    }
//                    // もう一度別のループで設定し直す
//                    for ( num in 1..6) {  // 数字が 1から6までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // 設定し直しする
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//                } else if (numInt in rangeLess) {
//                    for (n in 5 downTo 1) {
//                        // メソッドでインスタンスを取得して属性をチェックする
//                        var card =
//                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
//                            break // 抜ける
//                        }
//                    }
//                } else if (numInt == 1) { // 今置いたカード numInt 1 の時には  上で 置いたになってます item.placed = true
//
//                    // １つ目のループで 先にクリアしてから
//                    for (num in 13 downTo 8) {  // 数字が 13から8までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // クリアします
//                        if (card != null && card.placed == false && card.possible == true) {
//                            card.possible = false // 13から8までのカード全部　置ける を 一旦 置けない でクリアしておく
//                        }
//                    }
//                    // もう一度別のループで設定し直す
//                    for (num in 13 downTo 8) {  // 数字が 13から8までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//
//                }     // 今置いたカードのマーク
//             //   val putMark = (txtP.text.toString()).substring(0, 1).toInt()  // "S"
//                // 今置いたカードの数
//                val putNumInt = (txtP.text.toString()).substring(1).toInt()  // 8　とか 6 とか
//
//                if (putNumInt == 1) {
//                    // １つ目のループで 先にクリアしてから
//                    for (num in 13 downTo 8) {  // 数字が 13から8までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // クリアします
//                        if (card != null && card.placed == false && card.possible == true) {
//                            card.possible = false // 13から8までのカード全部　置ける を 一旦 置けない でクリアしておく
//                        }
//                    }
//                    // もう一度別のループで設定し直す
//                    for (num in 13 downTo 8) {  // 数字が 13から8までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//                    // 通常 1を置いた時には 2..6　は　placed == trueになってるから 大丈夫 逆回転の時のためのです
//                    for (num in 2..6) {
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//                }
//
//
//                if (putNumInt == 13) {
//                    // １つ目のループで 先にクリアしてから
//                    for (num in 1..6) {  // 数字が 1から6までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // 先にクリア
//                        if (card != null && card.placed == false && card.possible == true) {
//                            card.possible = false // 1から6までのカード全部　置ける を 一旦 置けない でクリアしておく
//                        }
//                    }
//                    // もう一度別のループで設定し直す
//                    for (num in 1..6) {  // 数字が 1から6までのカードを調べる
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        // 設定し直しする
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//                    // 通常の向きならば 13を置いた時点では 8..12の placedはtrueになってるから大丈夫  逆回転の場合のことです
//                    for (num in 12 downTo 8) {
//                        var card = game.getPossibleCard(
//                            _deepPossibleCardSet,
//                            txtP.text.toString(),
//                            num
//                        )
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
//                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
//                        }
//                    }
//                }
//
//
//                if (putNumInt >= 2 && putNumInt <= 6) {
//                    for (num in 5 downTo 1) {
//                        // メソッドでインスタンスを取得して属性をチェックする
//                        var card =
//                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), num)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
//                            break // 抜ける
//                        }
//                    }
////                    for (num in 3..6) {  // 通常の向きならば
////                        // メソッドでインスタンスを取得して属性をチェックする
////                        var card =
////                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), num)
////                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
////                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
////                            break // 抜ける
////                        }
////                    }
//                }
//
//
//
//                if (putNumInt >= 8 && putNumInt <= 12) {
//                    for (n in (putNumInt + 1)..13) {
//                        // メソッドでインスタンスを取得して属性をチェックする
//                        var card =
//                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
//                            break // 抜ける
//                        }
//                    }
//                    //
//                    for (n in 1 downTo 6) {
//                        var card =
//                            game.getPossibleCard(_deepPossibleCardSet, txtP.text.toString(), n)
//                        // 逆むきなら全て 1 - 6は　placed true
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
//                            break // 抜ける
//                        }
//                }






                    // トースト表示
                val toast: Toast = Toast.makeText(context, context.getString(R.string.put_on, txtP.text.toString()), Toast.LENGTH_SHORT)
                toast.show()
                  // 注意点 putExtraは リストの時には ArrayList型でないとだめ
               // 注意点  PlayerListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要がる
                intent.putExtra("data", _deepDataList as ArrayList<PlayerListItem> )
                //  自作したクラス PossibleCardを intentで送るためには Serializableインタフェースを実装します
                intent.putExtra("poList", _deepPossibleCardList as ArrayList<PossibleCard>)
                // 注意点 ListItemデータクラスは自作のクラスなので、intentで送るためには Serializableインタフェースを実装する必要があります
                intent.putExtra("tableCardData", _tableCardData as ArrayList<ListItem>)
                intent.putExtra("comAList", _deepComAList as ArrayList<PlayerListItem>)
                intent.putExtra("comBList", _deepComBList as ArrayList<PlayerListItem>)
                intent.putExtra("pPassCount", _playerPassCounter)  // そのまま渡すだけ
                intent.putExtra("comAPassCount", _comAPassCounter)  // そのまま渡すだけ
                intent.putExtra("comBPassCount", _comBPassCounter)  // そのまま渡すだけ
                // MainActivityへ遷移します
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