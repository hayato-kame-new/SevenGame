package to.msn.wings.sevengame

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.playerrv.PlayerCardListAdapter
import to.msn.wings.sevengame.playerrv.PlayerListItem
import to.msn.wings.sevengame.rv.CardListAdapter
import to.msn.wings.sevengame.rv.ListItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.properties.Delegates
import kotlin.random.Random

/**
 * MainActivity上のフラグメントです
 * Androidでは、アクティビティとフラグメントのクラスでは、引数なしのコンストラクタを強く推奨していますので、コンストラクタは規定通りにすること
 * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)
 */
class StartFragment : Fragment() {
     // publicにしておく _cardSet は 重複しないSetにする 次に置ける候補のカードを要素としている
   // lateinit var _cardSet: HashSet<String>
     lateinit var _possibleCardSet: HashSet<PossibleCard>
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var _game: Game
    // public
    lateinit var _tableCardData : ArrayList<ListItem>
    private lateinit var _playersCardData : ArrayList<PlayerListItem>
    // public 基本MutableListを使うが ここでは putExtraをする時に、ArrayList　に明示的にキャストしたりしないといけなくなるから ArrayListを使う
    lateinit var _playerList : ArrayList<PlayerListItem>
    lateinit var _comAList : ArrayList<PlayerListItem>
    lateinit var _comBList : ArrayList<PlayerListItem>
    // public  lateinit var は　Intには使えない    by Delegates.notNull<Int>() を使う
    var _playerPassCounter by Delegates.notNull<Int>()
    var _comAPassCounter by Delegates.notNull<Int>()
    var _comBPassCounter by Delegates.notNull<Int>()
    // private
    private lateinit var _passBtn : Button
    private lateinit var _aTxt : TextView
    private lateinit var _bTxt : TextView

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        // コンピュータA の表示
        _aTxt = view.findViewById<TextView>(R.id.aTxt)
        _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.comAColor)!!)
        // コンピュータB の表示
        _bTxt = view.findViewById<TextView>(R.id.bTxt)
        _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.comBColor)!!)

        val intent = activity?.intent
        val extras = intent?.extras

        _game = Game()

        if (extras == null) {
            /* 初回
            */
            // lateinit varフィールドに 初期値を代入
            _possibleCardSet = _game.getPossibleCardData() // lateinit varフィールドに 初期値を代入
            _tableCardData = _game.getStartTableCardData() // lateinit varフィールドに 初期値を代入
            _playersCardData = _game.getPlayersCardData() // lateinit varフィールドに 初期値を代入
            _playerPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comAPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comBPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _aTxt.text = "コンピューターAは パス 残り " + _comAPassCounter.toString() + "回"  // 最初 3
            _bTxt.text = "コンピューターBは パス 残り " + _comBPassCounter.toString() + "回"  // 最初 3
            // lateinit varフィールドに 初期値を代入してる
            //      単純な解決策は、指定された範囲の間に存在する元のリストの要素をサブリストに追加することです。
            // getSubListメソッドの中で MutableListオブジェクトを新しく作って返している MutableListにしないとできない
            _playerList = getSubList(_playersCardData, 0, (_playersCardData.size / 3) - 1 ) as ArrayList<PlayerListItem>
            _comAList = getSubList(_playersCardData, _playersCardData.size / 3 , (_playersCardData.size * 2 / 3) - 1) as ArrayList<PlayerListItem>
            _comBList = getSubList(_playersCardData, _playersCardData.size * 2 / 3, _playersCardData.size - 1) as ArrayList<PlayerListItem>

            sort(_playerList)  // 管理ID順　ソートずみのリスト　をアダプターの引数に渡す  初回表示


            /* 初回ここまで
            */
        } else {
            /* 遷移してきたとき
            */
            // lateinit varフィールドに 初期値を代入する  直接入れないで一旦違う変数に入れておいた方がいいかも??
                val deepPossibleCardSet = intent.getSerializableExtra("set") as HashSet<PossibleCard>
                val comAList = intent.getStringArrayListExtra("comAList") as ArrayList<PlayerListItem>
               _playerList = intent.getSerializableExtra("data") as ArrayList<PlayerListItem>
        
            _tableCardData = intent.getSerializableExtra("tableCardData") as ArrayList<ListItem>

        //    _comAList = intent.getStringArrayListExtra("comAList") as ArrayList<PlayerListItem>
            _comBList = intent.getStringArrayListExtra("comBList") as ArrayList<PlayerListItem>
            _playerPassCounter = intent.getIntExtra("pPassCount", 0)
            _comAPassCounter = intent.getIntExtra("comAPassCount", 0)
            _comBPassCounter = intent.getIntExtra("comBPassCount", 0)

            _aTxt.text = "コンピューターAは パス 残り " + _comAPassCounter.toString() + "回"  // 最初 3
            _bTxt.text = "コンピューターBは パス 残り " + _comBPassCounter.toString() + "回"  // 最初 3
            if (_comAPassCounter == 0) {
                _aTxt.text = "コンピューターA パス 残りなし"
                _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }
            if (_comBPassCounter == 0) {
                _bTxt.text = "コンピューターB パス 残りなし"
                _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }

            // ここから見直し _deepPossibleCardSet  _deepComAList  ディープコピー
            // _deepPossibleCardSet　ディープコピーしたもの (新たに 別のオブジェクト) ディープコピーにしないとエラー
            val _deepComAList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと
            val _deepPossibleCardSet = HashSet<PossibleCard>(deepPossibleCardSet)  // ディープコピーすること (同じ参照にしないこと)
            // アダプターと同じ処理を繰り返し書くので、同じメソッドを使いまわせるように Gameクラスにメソッドを定義して使うようにします。Javaでいうstaticなメソッドを作る
            // クラス名.メソッド名で呼び出しできるようにします kotlinではstaticメソッドはありません。ただしCompanion Objectsという仕組みを使えば実現できます
            // まずAから
            // インデックスで要素を取得したいなら、Listにすべきです Setは順番を持たないからです
            val subListComA = getSubList(deepPossibleCardSet, comAList)  // リストにします
            if (subListComA.size != 0) {  // Aは　出せるので出す

                var putCard: PossibleCard? = null  // 出すカード
                var randomIndex = 0
                // nextInt() は 0 から引数に指定した値未満の整数を返します
                randomIndex = Random.nextInt(subListComA.size)  // 3つ 出せるのがあったら 0 1 2　とかどれかが返ります　
                putCard = subListComA.get(randomIndex)
                // まずは プレイヤーの持ち手リスト_deepComAList から、出したカードを取り除く
                // java.util.ConcurrentModificationException を回避するために forは使わないでください
                val iterator = _deepComAList.iterator()  // 元のコレクションを書き換えます エラーなしで
                while (iterator.hasNext()){
                    val item = iterator.next()
                    if (item.pTag.equals(putCard.tag)) {
                        iterator.remove()
                    }
                }
                // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _tableCardData) {

                    if (item.tag.equals(putCard.tag)) {
                        item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                    }
                }
                // さらに、_deepPossibleCardSet の　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                var itemDistance = 0
                for (item in _deepPossibleCardSet) {
                    if (item.tag.equals(putCard.tag)) {
                        item.placed = true  // 置いた
                        item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                        itemDistance = item.distance  // 6だったら -1 になる
                    }
                }
                changeSet(_deepPossibleCardSet, putCard)
 //////////
//                // さらに、次に出せるカードの属性を変更する
//                val mark = putCard.tag.substring(0, 1)  // "S" とか
//                val numInt = (putCard.tag).substring(1).toInt()  // 8　とか 6 とか
//                val rangeMore: IntRange = 8..13
//                val rangeLess: IntRange = 1..6
//                val distanceMAX = 6
//                val distanceMIN = -6
//                if (numInt in rangeMore) {  // +1づつ 直近のものから調べる
//
//                    for ( n in 1..6) {
//                        // メソッドでインスタンスを取得して属性をチェックする  getNPossibleCard N個先のカードを取得する
//                        var card = _game.getNPossibleCard(_deepPossibleCardSet, putCard.tag, n)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる
//                            break // 抜ける
//                            // ここまでの動きは OKです！！
//                        }
//                        // ループで 直近で  card.placed == falseの物を見つけていきます
//                        if (card != null && card.placed == true && n == distanceMAX) {  // 13まで調べたら
//                            for ( num in 1..6) {  // 数字が 1から6までのカードを調べる
//                                var card = _game.getPossibleCard(
//                                    _deepPossibleCardSet,
//                                    putCard.tag,
//                                    num
//                                ) // 1のカードを取得
//
//                                if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                                    card.possible = true // 可能に trueを入れる
//                                    break // 抜ける
//                                }
//                                // また、 +1づつ直近から調べていって  6までみて 6も trueなら何もせずに抜ける
//                            }
//                        }
//
//                    }
//                } else if (numInt in rangeLess) {  // 1..6
//                    // 6 なら itemDistance -1
//                    //    for ( n in distanceMIN downTo itemDistance) { //  -6　~　-1 の間を -1から順に調べたい時 downTo と使うと -1 から始まり逆順に -2 -3 -4 -5 -6 となる
//                    for ( n in -1 downTo -6) {
//                        // n が -1 ならば -1 -2 -3 -4 -5 -6 までループさせる
//                        var card = _game.getNPossibleCard(_deepPossibleCardSet, putCard.tag, n)
//                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
//                            card.possible = true // 可能に trueを入れる
//                            break // 抜ける
//                            // ここまでの動きは OKです！！
//                        }
//
//                        if (card != null && card.placed == true && n == distanceMIN) {  // 1まで調べたら n == -6 の時
//                            for ( num in 8 downTo 13) {  // 数字が 13から8までのカードを調べる downTo と使うと 13から始まり逆順に 12 11 10 9 8 とループする
//                                var card = _game.getPossibleCard(
//                                    _deepPossibleCardSet,
//                                    putCard.tag,
//                                    num
//                                ) // 13 のカードを取得
//                                if (card != null && card.placed == false) {  // もし、まだ置いてないカードが見つかった時点で
//                                    card.possible = true  // 可能に trueを入れる
//                                    break  // 抜ける
//                                }
//                                // また、 -1づつ直近から調べていって 8もplacedが trueなら、何もせずにループは終わり
//                            }
//                        }
//                    }
//                }

    //////
                // comA トースト表示
                val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_put_on, putCard.tag), Toast.LENGTH_SHORT)
                toast.show()

            } else {  // Aは　出せない パスをします！！
                // パスする パスカウンターを操作 もし、パスが限度を越したら負け、持ち札を全ておく フィールドの操作をする 卓上の属性変更
                // パスができるなら、パスカウンターだけ操作して、トースト表示　　次にBの作業になる

                // もうパスできない
                val intent = Intent(activity, MainActivity::class.java)
                 if (_comAPassCounter == 0 && _comBPassCounter == 0) { // 終了
                    // あなたの勝ちですダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                    AlertDialog.Builder(activity) // FragmentではActivityを取得して生成
                        .setTitle("あなたの勝ちです")
                        .setMessage("ゲーム再開する")
                        .setPositiveButton("OK", { dialog, which ->
                            activity?.startActivity(intent)  // ここで遷移する
                        })
                        .show()
                    // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと

                } else if (_comAPassCounter == 0 && _comBPassCounter != 0) {
                    // comAの負けです トースト出す  comA手持ちを全て出す  comBとあなたでゲームは続く
                    val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_lose), Toast.LENGTH_SHORT)
                    toast.show()
                    // comA負けたので手持ちを全て出す _deepComAListを変更空にする　_deepCardSetも変更すること 卓上にも並べること
                    val sub = arrayListOf(_deepComAList)  // クリアする前にディープコピーしておく 全く別のオブジェクトを生成
                    // _deepComAListを変更空にする リスト内の全要素を削除
                    _deepComAList.clear()
                    // 卓上に並べる


                    // _deepCardSetも変更する


                } else {
                    // まだゲームは続けられる　 3人とも続いてる
                    _comAPassCounter--
                    _aTxt.text = "コンピューターAは パス 残り " + _comAPassCounter.toString() + "回"
                    _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.comAColor)!!)
                    if (_comAPassCounter == 0) {
                        _aTxt.text = "コンピューターA パス 残りなし"
                        _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
                    }
                    // comAはパスしたから パスカウンターだけをマイナスするだけ
                }
            }
                ////////　ここまでcomA
                ////////　ここからcomB

            // インデックスで要素を取得したいなら、Listにすべきです Setは順番を持たないからです
//            val subListComB = getSubList(deepPossibleCardSet, comBList)  // リストにします
//            if (subListComB.size != 0) {  // Bは　出せるので出す
//
//            }









            ////////　ここまでcomB
            // ここで lateinit varフィールドに 初期値を代入する
            _possibleCardSet = HashSet<PossibleCard>(_deepPossibleCardSet)  // ディープコピーすること (同じ参照にしないこと)
            // ここで lateinit varフィールドに 初期値を代入する
              _comAList = ArrayList<PlayerListItem>(_deepComAList) // ディープコピーすること 同じ参照にしないこと
            // ここで lateinit varフィールドに 初期値を代入する
            // _comBList = ArrayList<PlayerListItem>(_deepComBList) // ディープコピーすること 同じ参照にしないこと

            /* 遷移してきたときここまで
            */
        }




        view.findViewById<Button>(R.id.passBtn).also {
            _passBtn = it!!
            _passBtn.text = "パス 残り " + _playerPassCounter.toString() + "回"  // 最初 3
            if (_playerPassCounter == 0) {
                _passBtn.text = "ゲームに負ける"
                _passBtn.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }
        }


        _passBtn.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            if (_playerPassCounter == 0) {
                // あなたの負けです ダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                AlertDialog.Builder(activity) // FragmentではActivityを取得して生成
                    .setTitle("あなたの負けです")
                    .setMessage("ゲーム再開する")
                    .setPositiveButton("OK", { dialog, which ->
                        activity?.startActivity(intent)
                    })
                    .show()
                // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
               // activity?.startActivity(intent)
            } else {
                // まだゲームは続けられる
                _playerPassCounter--

                _passBtn.text = "パス 残り " + _playerPassCounter.toString() + "回"
                if (_playerPassCounter == 0) {
                    _passBtn.text = "ゲームに負ける"
                    _passBtn.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
                }
                // あなたがパスしたから 8つ intent.putExtraして、またMainActivity elseブロックへ戻ってきます
                intent.putExtra( "data" ,_playerList as ArrayList<PlayerListItem>)
                intent.putExtra("set", _possibleCardSet as HashSet<PossibleCard>)

                intent.putExtra( "tableCardData" ,_tableCardData as ArrayList<ListItem>)
                 intent.putExtra( "comAList", _comAList as ArrayList<PlayerListItem>)
                 intent.putExtra( "comBList", _comBList as ArrayList<PlayerListItem>)
                intent.putExtra("pPassCount", _playerPassCounter)
                intent.putExtra("comAPassCount", _comAPassCounter)
                intent.putExtra("comBPassCount", _comBPassCounter)
                activity?.startActivity(intent)  // もともとMainActivityは戻るボタンでいつでももどるので終わらせることはありません
            }
        }


        activity?.let {
            view.findViewById<RecyclerView>(R.id.rv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager = GridLayoutManager(activity, 13)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                adapter = CardListAdapter(_tableCardData)
            }
        }

        activity?.let {
            view.findViewById<RecyclerView>(R.id.playerrv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager =
                    GridLayoutManager(activity, 16)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                // アダプターのクラスにデータを渡したいときには、このようにコンストラクタの実引数に渡すことで可能になります 第一引数は、RecycleViewで使うものです
                adapter = PlayerCardListAdapter(_playerList, _possibleCardSet, _tableCardData, _comAList , _comBList, _playerPassCounter ,_comAPassCounter, _comBPassCounter)  // 第2引数以降に渡しています
            }
        }
        return view  // フラグメントでは最後必ず viewを返す
    }




    /**
     * 管理ID順に並べる.インスタンスメソッド
     */
    private fun sort(list: MutableList<PlayerListItem>) {
        for (i in 0 until list.size) {
            for (j in i + 1 until  list.size) {
                if ( list.get(i)!!.pId.toInt() >  list.get(j)!!.pId.toInt()) {
                    val t: PlayerListItem = list.get(i)
                    list.set(i, list.get(j))
                    list.set(j, t)
                }
            }
        }
    }

    /**
     * プレイヤーのカードを人数分で分ける.新しくオブジェクトを作り直して ディープコピーをする MutableListじゃないとだめ
     */
        fun <T> getSubList(list: List<T>, start: Int, end: Int): List<T>? {
        val subList: MutableList<T> = ArrayList()  // MutableList
        for (i in start..end) {
            subList.add(list[i])
        }
        return subList
        }


    /**
     * オーバーロード(多重定義)
     * インデックで要素を取得したいなら、Listにすべきです Setは順番を持たないからです
     * 戻り値 ArrayList<PossibleCard>型です
     */
    fun getSubList(set: HashSet<PossibleCard>, list: ArrayList<PlayerListItem>): ArrayList<PossibleCard> {
        val arrayList: ArrayList<PossibleCard> = ArrayList()
        for (possibleCard in set) {
            for (item in list) {
                if (possibleCard.tag.equals(item.pTag) && possibleCard.possible == true) {
                    arrayList.add(possibleCard)
                }
            }
        }
        return arrayList
    }


    /**
     * コンピュータの手の動き カードのセットの属性の変更
     * _deepPossibleCardSetが実引数
     */
    fun changeSet(set: HashSet<PossibleCard>, putCard: PossibleCard?) {
        val mark = putCard?.tag?.substring(0, 1)  // "S" とか
        val numInt = (putCard?.tag)?.substring(1)?.toInt()  // 8　とか 6 とか
        val rangeMore: IntRange = 8..13
        val rangeLess: IntRange = 1..6
        val distanceMAX = 6
        val distanceMIN = -6
        if (numInt in rangeMore) {  // +1づつ 直近のものから調べる
            for (n in 1..6) {
                // メソッドでインスタンスを取得して属性をチェックする  getNPossibleCard N個先のカードを取得する
                var card = _game.getNPossibleCard(set, putCard!!.tag, n)
                if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                    card.possible = true // 可能に trueを入れる
                    break // 抜ける
                    // ここまでの動きは OKです！！
                }
                // ループで 直近で  card.placed == falseの物を見つけていきます
                if (card != null && card.placed == true && n == distanceMAX) {  // 13まで調べたら
                    for (num in 1..6) {  // 数字が 1から6までのカードを調べる
                        var card = _game.getPossibleCard(
                            set, // _deepPossibleCardSetが実引数
                            putCard!!.tag,
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
            for (n in -1 downTo -6) {
                // n が -1 ならば -1 -2 -3 -4 -5 -6 までループさせる
                var card = _game.getNPossibleCard(set, putCard!!.tag, n)
                if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                    card.possible = true // 可能に trueを入れる
                    break // 抜ける
                }
                if (card != null && card.placed == true && n == distanceMIN) {  // 1まで調べたら n == -6 の時
                    for (num in 8 downTo 13) {  // 数字が 13から8までのカードを調べる downTo と使うと 13から始まり逆順に 12 11 10 9 8 とループする
                        var card = _game.getPossibleCard(
                            set,  // _deepPossibleCardSetが実引数
                            putCard.tag,
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
    }


}