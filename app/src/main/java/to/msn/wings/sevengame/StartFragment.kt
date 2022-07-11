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
     // publicにしておく _cardSet は 重複しないSetにする
    lateinit var _cardSet : HashSet<String>
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var _game : Game
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

        val intent = activity?.intent
        val extras = intent?.extras

        _game = Game()

        if (extras == null) {
            /* 初回
            */
            // lateinit varフィールドに 初期値を代入
            _cardSet = hashSetOf<String>("S6", "S8", "H6", "H8",  "D6", "D8", "C6", "C8")  // lateinit varフィールドに 初期値を代入
            _tableCardData = _game.getStartTableCardData() // lateinit varフィールドに 初期値を代入
            _playersCardData = _game.getPlayersCardData() // lateinit varフィールドに 初期値を代入
            _playerPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comAPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
            _comBPassCounter = 3  // by Delegates.notNull<Int>()フィールドに 初期値を代入
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
                val cardSet = intent.getSerializableExtra("cardSet") as HashSet<String>
                val comAList = intent.getStringArrayListExtra("comAList") as ArrayList<PlayerListItem>
               _playerList = intent.getSerializableExtra("data") as ArrayList<PlayerListItem>
         //   _cardSet =  intent.getSerializableExtra("cardSet") as HashSet<String>
            _tableCardData = intent.getSerializableExtra("tableCardData") as ArrayList<ListItem>

        //    _comAList = intent.getStringArrayListExtra("comAList") as ArrayList<PlayerListItem>
            _comBList = intent.getStringArrayListExtra("comBList") as ArrayList<PlayerListItem>
            _playerPassCounter = intent.getIntExtra("pPassCount", 0)
            _comAPassCounter = intent.getIntExtra("comAPassCount", 0)
            _comBPassCounter = intent.getIntExtra("comBPassCount", 0)

            // アダプターと同じ処理を繰り返し書くので、同じメソッドを使いまわせるように Gameクラスにメソッドを定義して使うようにします。Javaでいうstaticなメソッドを作る
            // クラス名.メソッド名で呼び出しできるようにします kotlinではstaticメソッドはありません。ただしCompanion Objectsという仕組みを使えば実現できます
                // まずAから
            val subSet = getSubSet(cardSet, comAList)
            var putStr = ""
            var randomIndex = 0
            var counter = 0
            /////////////////// ここで
            val _deepComAList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと
            val _deepCardSet = HashSet<String>(cardSet)  // ディープコピーすること (同じ参照にしないこと)
            ////////　ここからcomA
            if (subSet.size != 0) {  // Aは　出せるので出す
                randomIndex = Random.nextInt(subSet.size)  //2だったとすると
                for (item in subSet) {
                    if ( randomIndex == counter) {
                        putStr = item
                        break
                    }
                    counter++
                }
                // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ ディープコピーにしないとエラー _cardSet = cardSet としてはいけない バインドが終わるまで cardSetも変わってしまってはいけないからです
          //    val _deepCardSet = HashSet<String>(cardSet)  // ディープコピーすること (同じ参照にしないこと)

                val mark = putStr.substring(0, 1)  // "D" とか
                val numInt = putStr.substring(1).toInt()  // 5　とか
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
                _deepCardSet.add(addStr)  // 追加する

                // java.util.ConcurrentModificationException を回避するために forは使わないでください
                val ite = _deepCardSet.iterator()  // _deepCardSet を書き換えます イテレータを使えばエラーなしでできる
                while (ite.hasNext()){
                    val item = ite.next()
                    if (item.equals(putStr)) {
                        ite.remove()
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
                                ite.remove()
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
                                ite.remove()
                            }
                        }
                    }
                }
                // ここで lateinit varフィールドに 初期値を代入する
               // _cardSet = HashSet<String>(_deepCardSet)  // ディープコピーすること (同じ参照にしないこと)
                // _deepCardSetここまで


             //   val _deepComAList = ArrayList<PlayerListItem>(comAList) // ディープコピーすること 同じ参照にしないこと

                // comA手持ちりすとから putStr と　同じタグのを除いてください
                // java.util.ConcurrentModificationException を回避するために forは使わないでください
                val iterator = _deepComAList.iterator()  // 元のコレクションを書き換えます エラーなしで
                while (iterator.hasNext()){
                    val item = iterator.next()
                    if (item.pTag.equals(putStr)) {
                        iterator.remove()
                    }
                }
                // ここで lateinit varフィールドに 初期値を代入する
              //  _comAList = ArrayList<PlayerListItem>(_deepComAList) // ディープコピーすること 同じ参照にしないこと


                // 卓上リストを　　trueにして表示されるようにしてください
                // 卓上カードのアイテムListItemの属性を変更する placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                for (item in _tableCardData) {  // 要素の属性を操作してるだけだから、元のコレクションがそのまま使える
                    if (item.tag.equals(putStr)) {
                        item.placed = true
                    }
                }
                // トースト表示
                val toast: Toast = Toast.makeText(activity, activity?.getString(R.string.comA_putOn, putStr), Toast.LENGTH_SHORT)
                toast.show()

            } else {   // Aは 出せるもの要素がない パスをします！！
                // パスする パスカウンターを操作 もし、パスが限度を越したら負け、持ち札を全ておく、また _cardSetを操作する トースト表示
                // パスができるなら、パスカウンターだけ操作
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




            ////////　ここまでcomB
            // ここで lateinit varフィールドに 初期値を代入する
            _cardSet = HashSet<String>(_deepCardSet)  // ディープコピーすること (同じ参照にしないこと)
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
                intent.putExtra( "data" ,_playerList)
                intent.putExtra("cardSet", _cardSet)

                intent.putExtra( "tableCardData" ,_tableCardData)
                 intent.putExtra( "comAList", _comAList)
                 intent.putExtra( "comBList", _comBList )
                intent.putExtra("pPassCount", _playerPassCounter)
                intent.putExtra("comAPassCount", _comAPassCounter)
                intent.putExtra("comBPassCount", _comBPassCounter)
                activity?.startActivity(intent)  // もともとMainActivityは戻るボタンでいつでももどるので終わらせることはありません
            }
        }

        view.findViewById<TextView>(R.id.aTxt).also {
            _aTxt = it!!
            _aTxt.text = "コンピューターAは パス 残り " + _comAPassCounter.toString() + "回"  // 最初 3
            _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.comAColor)!!)
            if (_comAPassCounter == 0) {
                _aTxt.text = "コンピューターA パス 残りなし"
                _aTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
            }
        }

        view.findViewById<TextView>(R.id.bTxt).also {
            _bTxt = it!!
            _bTxt.text = "コンピューターBは パス 残り " + _comBPassCounter.toString() + "回"  // 最初 3
            _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.comBColor)!!)
            if (_comBPassCounter == 0) {
                _bTxt.text = "コンピューターB パス 残りなし"
                _bTxt.setBackgroundColor(activity?.resources?.getColor(R.color.danger)!!)
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
                adapter = PlayerCardListAdapter(_playerList, _cardSet, _tableCardData, _comAList , _comBList, _playerPassCounter ,_comAPassCounter, _comBPassCounter)  // 第2引数以降に渡しています
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

    fun <T> getSubSet(set: Set<T>, list: List<PlayerListItem>): HashSet<String> {
        val subSet: HashSet<String> = HashSet()  // HashSet
        for (s in set) {
            for (item in list) {
                if (s!!.equals(item.pTag)) {
                    subSet.add(s.toString())
                }
            }
        }
        return subSet
    }

}