package to.msn.wings.sevengame

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

/**
 * MainActivity上のフラグメントです
 * Androidでは、アクティビティとフラグメントのクラスでは、引数なしのコンストラクタを強く推奨していますので、コンストラクタは規定通りにすること
 */
class StartFragment : Fragment() {
     // publicにしておく _availableList は
    lateinit var _availableList : MutableList<String>
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var _game : Game
    // public
    lateinit var _tableCardData : List<ListItem>
    private lateinit var _playersCardData : ArrayList<PlayerListItem>
    // public
    lateinit var _playerList : MutableList<PlayerListItem>
    private lateinit var _comAList : MutableList<PlayerListItem>
    private lateinit var _comBList : MutableList<PlayerListItem>

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
            _availableList = mutableListOf<String>("S6", "S8", "H6", "H8",  "D6", "D8", "C6", "C8")  // lateinit varフィールドに 初期値を代入
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
            _availableList =  intent.getStringArrayListExtra("aList") as MutableList<String>
                // lateinit varフィールドに 初期値を代入する
            _tableCardData =  intent.getSerializableExtra("tList") as List<ListItem>
                // lateinit varフィールドに 初期値を代入する
            _playerList = intent.getStringArrayListExtra("pArrayLi") as MutableList<PlayerListItem>  // ArrayListがら変換

            // ここまで完成 _comAList _comBList もアダプターの引数に渡すこと！！同じように引き渡すそして adapterで書き換えたリストを
            // intent.get して lateinit varフィールドに 初期値を代入する 　同じように行う


//            _comAList =
//                _playersCardData.subList(_playersCardData.size / 3, _playersCardData.size * 2 / 3) // フィールドに初期値を代入
//            _comBList =
//                _playersCardData.subList(_playersCardData.size * 2 / 3, _playersCardData.size) // フィールドに初期値を代入



            /* 遷移してきたときここまで
            */
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
                adapter = PlayerCardListAdapter(_playerList, _availableList, _tableCardData)  // 第2引数以降に渡しています
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