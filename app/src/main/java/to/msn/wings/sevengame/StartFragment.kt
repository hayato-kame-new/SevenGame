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
     // publicにしておく 次に置ける候補のカードを要素としている
    lateinit var _possibleCardList: ArrayList<PossibleCard>  // indexでアクセスするには リストにすべき Setは順番を持たないためできない
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
    private lateinit var _a : TextView
    private lateinit var _b : TextView
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
        _a = view.findViewById<TextView>(R.id.a)
        _a.setBackgroundColor(activity?.resources?.getColor(R.color.comAColor)!!)
        _aTxt = view.findViewById<TextView>(R.id.aTxt)

        // コンピュータB の表示
        _b = view.findViewById<TextView>(R.id.b)
        _b.setBackgroundColor(activity?.resources?.getColor(R.color.comBColor)!!)
        _bTxt = view.findViewById<TextView>(R.id.bTxt)

        val intent = activity?.intent
        val extras = intent?.extras

        _game = Game()

        if (extras == null) {
            /* 初回
            */
            // lateinit varフィールドに 初期値を代入
            _possibleCardList = _game.getPossibleCardData() // lateinit varフィールドに 初期値を代入
            _tableCardData = _game.getStartTableCardData() // lateinit varフィールドに 初期値を代入
            _playersCardData = _game.getPlayersCardData() // lateinit varフィールドに 初期値を代入
            _playerPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comAPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comBPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入


            _aTxt.text = " パス 残り " + _comAPassCounter.toString() + "回"  // 最初 3
            _bTxt.text = " パス 残り " + _comBPassCounter.toString() + "回"  // 最初 3

            // lateinit varフィールドに 初期値を代入してる
            //     エラーなしで サブリスト取得するには、指定された範囲の間に存在する元のリストの要素をサブリストに追加することです。
            // getSubListメソッドの中で MutableListオブジェクトを新しく作って返している MutableListにしないとできない
            _playerList = _game.getSubList(_playersCardData, 0, (_playersCardData.size / 3) - 1 ) as ArrayList<PlayerListItem>
            _comAList = _game.getSubList(_playersCardData, _playersCardData.size / 3 , (_playersCardData.size * 2 / 3) - 1) as ArrayList<PlayerListItem>
            _comBList = _game.getSubList(_playersCardData, _playersCardData.size * 2 / 3, _playersCardData.size - 1) as ArrayList<PlayerListItem>

            sort(_playerList)  // 管理ID順　ソートずみのリスト　をアダプターの引数に渡す  初回表示

            /* 初回ここまで
            */
        } else {
            /* 遷移してきたとき
            */
            // lateinit varフィールドに 初期値を代入するが、操作してから代入したい時は  ここでは直接入れないで 一旦違う変数に入れておく 下の３つ
            val poList = intent.getSerializableExtra("poList") as ArrayList<PossibleCard>

            val comAList = intent.getStringArrayListExtra("comAList") as ArrayList<PlayerListItem>
            val comBList = intent.getStringArrayListExtra("comBList") as ArrayList<PlayerListItem>

            //  lateinit varフィールドに 初期値を代入する
            _playerList = intent.getSerializableExtra("data") as ArrayList<PlayerListItem>
            _tableCardData = intent.getSerializableExtra("tableCardData") as ArrayList<ListItem>
            _playerPassCounter = intent.getIntExtra("pPassCount", 0)
            _comAPassCounter = intent.getIntExtra("comAPassCount", 0)
            _comBPassCounter = intent.getIntExtra("comBPassCount", 0)

            // コンピュータの表示
            comDisplay(_aTxt, _comAPassCounter)
            comDisplay(_bTxt, _comBPassCounter)

            // 一旦ローカル変数で取得した3つを  ディープコピーしておく (新たに 別のオブジェクト) ディープコピーしたオブジェクトも ローカル変数にしておく
            val deepComAList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと
            val deepComBList = ArrayList<PlayerListItem>(comBList) // ディープコピーすること 同じ参照にしないこと
            var deepPoList = ArrayList<PossibleCard>(poList)  // ディープコピーすること (同じ参照にしないこと)

            // comAの手
            // サブリストを取得する そのリストは後で インデックスで要素を取得したいので、Listにすべきです (Setは順番を持たないからです Setにはしません)
            // 置くことのできそうなpossibleカードを探してリストにする 複数見つかる時もあるし、空のリストを返す時もある
            val subListComA = _game.getSubList(poList, comAList)  // 戻り値リストにします(Setだめです) 引数はintentから取得したもの

            if (subListComA.size != 0) {  // Aは　出せるので出す
                // まずは プレイヤーの持ち手リストから、置いたカードを取り除く
                var putCard = removeComPutCard(subListComA, deepComAList)  // 戻り値置いたカード
                // ここで、 comAの手持ちリストが最初に 0になったら、comAの勝ちですとする ゲーム終了
                //  一番先に 手持ちが 0になった人の勝ちで ゲーム終了
                // if else追加すること

                // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _tableCardData) {
                    if (item.tag.equals(putCard?.tag)) {
                        item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                    }
                }
                // さらに、　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in deepPoList) {
                    if (item.tag.equals(putCard?.tag)) {
                        item.placed = true  // 置いた
                        item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                    }
                }
                val pTagStr: String = putCard!!.tag  // 置いたカードのタグの文字列 "S6" とか
                val putNum: Int = pTagStr.substring(1).toInt()
                val game =  Game()
                // さらに、次に出せるカードの属性を変更する
                val judge = Judgement(deepPoList)
                if (putNum in 1..6) {
                    val list = judge.methodSmall(pTagStr)  // 属性を書き換えた リストを返すので、
                    deepPoList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                } else if (putNum in 8..13) {
                    val list = judge.methodBig(pTagStr)  // 属性を書き換えた リストを返すので、
                    deepPoList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                }
                // comA トースト表示
                val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_put_on, putCard!!.tag), Toast.LENGTH_SHORT)
                toast.show()
            } else {  // Aは　出せない パスをします
                val intent = Intent(activity, MainActivity::class.java) // もうパスができない場合にダイアログ出して遷移する
                // もし コンピュータA  0  　   コンピュターB -1なら
                 if (_comAPassCounter == 0 && _comBPassCounter == -1) { // ゲーム終了
                    // あなたの勝ちですダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                    AlertDialog.Builder(activity) // FragmentではActivityを取得して生成
                        .setTitle("あなたの勝ちです")
                        .setMessage("ゲーム再開する")
                        .setPositiveButton("OK", { dialog, which ->
                            activity?.startActivity(intent)  // ここで遷移する  もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
                        })
                        .show()
                    // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと

                 } else if (_comAPassCounter == 0 && _comBPassCounter != -1) { // もうパスできないので comAの負け
                     _comAPassCounter--  //  -1 にする コンピューターA はゲームオーバー
                     if (_comAPassCounter == -1) {
                         _aTxt.text = " 負けました "
                         _aTxt.setTextColor(activity?.resources?.getColor(R.color.white)!!)
                         _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.black)!!)
                     }
                    // comAの負けです トースト出す  comA手持ちを全て出す  comBとあなたでゲームは続く
                    val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_lose), Toast.LENGTH_SHORT)
                    toast.show()
                    // comA負けたので手持ちを全て出す
                     // クリアする前にディープコピーしておく 全く別のオブジェクトを生成
                     // 修正した
                     val subDeepComAList = _game.getSubList(deepComAList)
                    // deepComAListを変更空にする リスト内の全要素を削除
                    deepComAList.clear()  // 最後にこの空にしたリスト をさらに _comAListにディープコピーをして lateinit varフィールドへ初期値として代入しています
                    //  comA手持ちを全て出す　卓上に並べる  とりあえず、卓上を placed trueの属性に変えればいい
                     // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                     for (item in _tableCardData) {
                         for (pItem in subDeepComAList!!) {
                             if (item.tag.equals(pItem.pTag)) {
                                 item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                             }
                         }
                     }
                    // 　placed   possibleだけ を属性を書き換える
                     // 出したカード の属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                     for (item in deepPoList) {
                         for (pItem in subDeepComAList!!) {
                             if (item.tag.equals(pItem.pTag)) {
                                 item.placed = true  // 置いた
                                 item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                             }
                         }
                     }
                } else {
                    // まだパスできるから まだゲームは続けられる　 3人とも続いてる
                    _comAPassCounter--  //  comAはパスしたから パスカウンターだけをマイナスするだけ
                     val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_pass), Toast.LENGTH_SHORT)
                     toast.show()
                    _aTxt.text = " パス 残り " + _comAPassCounter.toString() + "回"

                    if (_comAPassCounter == 0) {
                        _aTxt.text = " パス 残りなし"
                        _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
                    }
                }
            }
                ////////　ここまでcomA
                ////////　ここからcomB
            val subListComB = _game.getSubList(poList, comBList)  // リストにします インデックスで要素を取得したいなら、Listにすべきです Setは順番を持たないからです
            if (subListComB.size != 0) {  // Bは　出せるので出す
                // まずは プレイヤーの持ち手リスト から、置いたカードを取り除く  戻り値置いたカード
                var putCard: PossibleCard? = null  // 出すカード
                putCard = removeComPutCard(subListComB, deepComBList)
                // ここで、 comBの手持ちリストが最初に 0になったら、comAの勝ちですとする ゲーム終了
                //  一番先に 手持ちが 0になった人の勝ちで ゲーム終了
                // if else追加すること


                // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in _tableCardData) {
                    if (item.tag.equals(putCard?.tag)) {
                        item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                    }
                }
                // さらに、deepPossibleCardSet の　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                for (item in deepPoList) {
                    if (item.tag.equals(putCard?.tag)) {
                        item.placed = true  // 置いた
                        item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                    }
                }
                val pTagStr: String = putCard!!.tag  // 置いたカードのタグの文字列 "S6" とか
                val putNum: Int = pTagStr.substring(1).toInt()
                val game =  Game()
                // さらに、次に出せるカードの属性を変更する
                val judge = Judgement(deepPoList)
                if (putNum in 1..6) {
                    val list = judge.methodSmall(pTagStr)  // 属性を書き換えた リストを返すので、
                    deepPoList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                } else if (putNum in 8..13) {
                    val list = judge.methodBig(pTagStr)  // 属性を書き換えた リストを返すので、
                    deepPoList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                }
                // comB トースト表示
                val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comB_put_on, putCard!!.tag), Toast.LENGTH_SHORT)
                toast.show()
            } else {  // Bは　出せない パスをします！！
                val intent = Intent(activity, MainActivity::class.java)
                if (_comBPassCounter == 0 && _comAPassCounter == -1) { // ゲーム終了
                    // あなたの勝ちですダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                    AlertDialog.Builder(activity) // FragmentではActivityを取得して生成
                        .setTitle("あなたの勝ちです")
                        .setMessage("ゲーム再開する")
                        .setPositiveButton("OK", { dialog, which ->
                            activity?.startActivity(intent)  // ここで遷移する  もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
                        })
                        .show()
                    // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
                } else if (_comBPassCounter == 0 && _comAPassCounter != -1) {  // もうパスはできないので comBの負けです
                    _comBPassCounter--  // -1 にしておく コンピューターB はゲームオーバー
                    if (_comBPassCounter == -1) {
                        _bTxt.text = " 負けました "
                        _bTxt.setTextColor(activity?.resources?.getColor(R.color.white)!!)
                        _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.black)!!)
                    }
                    // comBの負けです トースト出す  comB手持ちを全て出す  comAとあなたでゲームは続く
                    val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comB_lose), Toast.LENGTH_SHORT)
                    toast.show()
                    // 修正した
                    val subDeepComBList = _game.getSubList(deepComBList)  // 手持ちリストをクリアする前にディープコピーをしておく(全く別のオブジェクト生成)
                    deepComBList.clear()  // 最後にこの空にしたリスト をさらに _comBListにディープコピーをして lateinit varフィールドへ初期値として代入しています
                    for (item in _tableCardData) {
                        for (pItem in subDeepComBList!!) {
                            if (item.tag.equals(pItem.pTag)) {
                                item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                            }
                        }
                    }
                    // 　placed   possibleだけ を属性を書き換える
                    // 出したカード の属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                    for (item in deepPoList) {
                        for (pItem in subDeepComBList!!) {
                            if (item.tag.equals(pItem.pTag)) {
                                item.placed = true  // 置いた
                                item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                            }
                        }
                    }
                } else {
                    // まだパスできるから まだゲームは続けられる　 3人とも続いてる
                    _comBPassCounter--  //  comBはパスしたから パスカウンターだけをマイナスするだけ
                    val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_pass), Toast.LENGTH_SHORT)
                    toast.show()
                    _bTxt.text = " パス 残り " + _comBPassCounter.toString() + "回"

                    if (_comBPassCounter == 0) {
                        _bTxt.text = " パス 残りなし"
                        _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
                    }
                }
            }
            ////////　ここまでcomB

            // ここで lateinit varフィールドに 初期値を代入する
            _possibleCardList = ArrayList<PossibleCard>(deepPoList)  // ディープコピーすること (同じ参照にしないこと)
            // ここで lateinit varフィールドに 初期値を代入する
              _comAList = ArrayList<PlayerListItem>(deepComAList) // ディープコピーすること 同じ参照にしないこと
            // ここで lateinit varフィールドに 初期値を代入する
             _comBList = ArrayList<PlayerListItem>(deepComBList) // ディープコピーすること 同じ参照にしないこと
            /* 遷移してきたときここまで
            */
        }

        // プレイヤーのパスボタン取得 この位置に書いてください 動かさないで
        view.findViewById<Button>(R.id.passBtn).also {
            _passBtn = it!!
            _passBtn.text = "パス 残り " + _playerPassCounter.toString() + "回"  // 最初 3
            if (_playerPassCounter == 0) {
                _passBtn.text = "ゲームに負ける"
                _passBtn.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }
        }

        // プレイヤーのパスボタンのクリックリスナー
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
                intent.putExtra("poList", _possibleCardList as ArrayList<PossibleCard>)
                intent.putExtra( "tableCardData" ,_tableCardData as ArrayList<ListItem>)
                 intent.putExtra( "comAList", _comAList as ArrayList<PlayerListItem>)
                 intent.putExtra( "comBList", _comBList as ArrayList<PlayerListItem>)
                intent.putExtra("pPassCount", _playerPassCounter)
                intent.putExtra("comAPassCount", _comAPassCounter)
                intent.putExtra("comBPassCount", _comBPassCounter)
                activity?.startActivity(intent)  // もともとMainActivityは戻るボタンでいつでももどるので終わらせることはありません
            }
        }

        // アダプタープロパティへセットする
        activity?.let {
            view.findViewById<RecyclerView>(R.id.rv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager = GridLayoutManager(activity, 13)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                adapter = CardListAdapter(_tableCardData)
            }
        }

        // アダプタープロパティへセットする
        activity?.let {
            view.findViewById<RecyclerView>(R.id.playerrv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager =
                    GridLayoutManager(activity, 16)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                // アダプターのクラスにデータを渡したいときには、このようにコンストラクタの実引数に渡すことで可能になります 第一引数は、RecycleViewで使うものです
                adapter = PlayerCardListAdapter(_playerList, _possibleCardList, _tableCardData, _comAList , _comBList, _playerPassCounter ,_comAPassCounter, _comBPassCounter)  // 第2引数以降に渡しています
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
     * コンピューターの持ち手リストから 出したカードを取り除く.
     * java.util.ConcurrentModificationException を回避するために forは使わないでください.
     *  subListComA _deepComAList などを 実引数にとる.
     */
    fun removeComPutCard( sublist: ArrayList<PossibleCard>, comList: ArrayList<PlayerListItem>): PossibleCard? {
        var putCard: PossibleCard? = null  // 出すカード
        var randomIndex = 0
        // nextInt() は 0 から引数に指定した値未満の整数を返します
        randomIndex = Random.nextInt(sublist.size)  // 3つ 出せるのがあったら 0 1 2　とかどれかが返ります　
        putCard = sublist.get(randomIndex)
        //  プレイヤーの持ち手リスト から、出したカードを取り除く
        // java.util.ConcurrentModificationException を回避するために forは使わないでください
        val iterator = comList.iterator()  // 元のコレクションを書き換えます エラーなしで
        while (iterator.hasNext()){
            val item = iterator.next()
            if (item.pTag.equals(putCard.tag)) {
                iterator.remove()
            }
        }
        return putCard
    }

    /**
     * コンピューターのパスの残りの表示や負けた時の表示.
     */
    fun comDisplay(view: TextView, count: Int) {
        when(count) {
            0 -> {
                view.text = " パス 残りなし"
                view.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }
            -1 -> {
                view.text = " 負けました "
                view.setTextColor(activity?.resources?.getColor(R.color.white)!!)
                view.setBackgroundColor(activity?.resources?.getColor(R.color.black)!!)
            }
            else -> {
                view.text = " パス 残り " + count.toString() + "回"
            }
        }
    }

}

