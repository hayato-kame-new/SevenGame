package to.msn.wings.sevengame

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.core.content.ContextCompat

/**
 * MainActivity上のフラグメントです
 * Androidでは、アクティビティとフラグメントのクラスでは、引数なしのコンストラクタを強く推奨していますので、コンストラクタは規定通りにすること
 */
class StartFragment : Fragment() {
     // publicにしておく _cardSet は 重複しないSetにする
    lateinit var _cardSet : HashSet<String>
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var _game : Game
    // public
    lateinit var _tableCardData : List<ListItem>
    private lateinit var _playersCardData : ArrayList<PlayerListItem>
    // public
    lateinit var _playerList : MutableList<PlayerListItem>
    lateinit var _comAList : MutableList<PlayerListItem>
    lateinit var _comBList : MutableList<PlayerListItem>
    // private
    private lateinit var _passBtn : Button

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    @SuppressLint("ResourceAsColor")
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
            _cardSet = hashSetOf<String>("S6", "S8", "H6", "H8",  "D6", "D8", "C6", "C8")  // lateinit varフィールドに 初期値を代入
            _tableCardData = _game.getStartTableCardData() // lateinit varフィールドに 初期値を代入
            _playersCardData = _game.getPlayersCardData() // lateinit varフィールドに 初期値を代入

            // ３当分する
            _playerList = _playersCardData.subList(0, (_playersCardData.size / 3)) // lateinit varフィールドに 初期値を代入
            _comAList =
                _playersCardData.subList(_playersCardData.size / 3, _playersCardData.size * 2 / 3) // lateinit varフィールドに 初期値を代入
            _comBList =
                _playersCardData.subList(_playersCardData.size * 2 / 3, _playersCardData.size) // lateinit varフィールドに 初期値を代入

            sort(_playerList)  // 管理ID順　ソートずみのリスト　をアダプターの引数に渡す  初回表示
            /* 初回ここまで
            */
        } else {
            /* 遷移してきたとき
            */
                // lateinit varフィールドに 初期値を代入する
            _cardSet =  intent.getSerializableExtra("cardSet") as HashSet<String>
                // lateinit varフィールドに 初期値を代入する
            _tableCardData =  intent.getSerializableExtra("tList") as List<ListItem>
                // lateinit varフィールドに 初期値を代入する
            _playerList = intent.getStringArrayListExtra("pArrayLi") as MutableList<PlayerListItem>  // ArrayListがら変換

            _comAList = intent.getStringArrayListExtra("comAList") as MutableList<PlayerListItem>
            _comBList = intent.getStringArrayListExtra("comBList") as MutableList<PlayerListItem>
            // ここまで完成 _comAList _comBList もアダプターの引数に渡すこと！！同じように引き渡す 引き渡して戻すだけ
            // intent.get して lateinit varフィールドに 初期値を代入する 　同じように行う

            // 遷移してきた時に _comAList _cardSet  比べて置けるものが
            // 存在していたら、その中から、ランダムに選んで起きます。置けなかったら、パスします
            // 同じように _comBList  もします
            // アダプターと同じ処理を繰り返し書くので、同じメソッドを使いまわせるように Gameクラスにメソッドを定義して使うようにします。Javaでいうstaticなメソッドを作る
            // クラス名.メソッド名で呼び出しできるようにします kotlinではstaticメソッドはありません。ただしCompanion Objectsという仕組みを使えば実現できます

            // ここから、comAの動作 comBの動作を書きます。メソッド化して ２回呼ぶように書きます この先プレイヤーが増えても対応できるようにする




            /* 遷移してきたときここまで
            */
        }

        // パスボタンを押すと、intentを発行して、_cardSet _tableCardData _playerList _comAList _comBList のデータを送って、このMain Activityへ戻るようにします。
        // すると　intentにExtraがついてるので elseのブロックへ行きます、つまり、プレイヤーはスキップして、 comA comBの実行になります
        // パスのカウントするフィールが必要になってきます パスをカウントします ３人分別々の変数が必要
        var playerPassCounter = 3
//        _passBtn = view.findViewById<Button>(R.id.passBtn)!!
//        _passBtn.text = "パス 残り " + playerPassCounter.toString() + "回"

        view.findViewById<Button>(R.id.passBtn).also {
            _passBtn = it!!
            _passBtn.text = "パス 残り " + playerPassCounter.toString() + "回"  // 最初 3
        }

        _passBtn.setOnClickListener {
            if (playerPassCounter == 0) {
                // あなたの負けです ダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
            }
            playerPassCounter--
            _passBtn.text = "パス 残り " + playerPassCounter.toString() + "回"
            if (playerPassCounter == 0) {
                _passBtn.text = "ゲームに負ける"
                _passBtn.setBackgroundColor(activity?.resources?.getColor(R.color.lose_btn_color)!!);
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
                adapter = PlayerCardListAdapter(_playerList, _cardSet, _tableCardData, _comAList , _comBList)  // 第2引数以降に渡しています
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



}