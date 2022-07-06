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
     // publicにしておく _placeableList は
    lateinit var _availableList : MutableList<String>
  // var _placeableList = mutableListOf<String>("S6", "S8", "H6", "H8",  "D6", "D8", "C6", "C8")
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var _game : Game
    private lateinit var _tableCardData : List<ListItem>
    private lateinit var _playersCardData : ArrayList<PlayerListItem>
    private lateinit var _playerList : MutableList<PlayerListItem>
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

        if (extras == null) {
            /* 初回
            */
            _availableList = mutableListOf<String>("S6", "S8", "H6", "H8",  "D6", "D8", "C6", "C8")
            _game = Game()
            _tableCardData = _game.getStartTableCardData() // 卓上カード 13 * 4 = 52枚
            _playersCardData = _game.getPlayersCardData()  // シャッフル済み 3人のプレイヤーのカード 12 * 4 = 48枚
            // ３当分する
            _playerList = _playersCardData.subList(0, (_playersCardData.size / 3))
            _comAList =
                _playersCardData.subList(_playersCardData.size / 3, _playersCardData.size * 2 / 3)
            _comBList =
                _playersCardData.subList(_playersCardData.size * 2 / 3, _playersCardData.size)
            // プレイする人の分は、ソートして表示するので 管理ID順に並べる
            sort(_playerList)  // ソートずみのリストをアダプターの引数に渡す
            /* 初回ここまで
            */
        } else {
            /* 遷移してきたとき
            */
            _availableList =  intent.getStringArrayListExtra("li") as MutableList<String>
            val l = _availableList
            val s = l
            /* 遷移してきたときここまで
            */
        }

        activity?.let {
            view.findViewById<RecyclerView>(R.id.rv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager = GridLayoutManager(activity, 13)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                 adapter = CardListAdapter(_tableCardData)
                // アダプターのクラスにデータを渡したいときには、このようにコンストラクタの実引数に渡すことで可能になります
               // adapter = CardListAdapter(_tableCardData, _placeableList)
            }
        }

        activity?.let {
            view.findViewById<RecyclerView>(R.id.playerrv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager =
                    GridLayoutManager(activity, 16)  // ここはフラグメントなので thisじゃなくて activityプロパティ
                // アダプターのクラスにデータを渡したいときには、このようにコンストラクタの実引数に渡すことで可能になります
                adapter = PlayerCardListAdapter(_playerList, _availableList)  // 第2引数を作って渡しています
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