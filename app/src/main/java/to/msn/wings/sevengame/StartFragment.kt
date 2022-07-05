package to.msn.wings.sevengame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.rv.CardListAdapter
import to.msn.wings.sevengame.rv.ListItem


class StartFragment : Fragment() {

    var placeableList = mutableListOf<String>("♠6", "♠8", "♥6", "♥8",  "♦6", "♦8", "♣6", "♣8")
    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    private lateinit var game : Game
    private lateinit var tableCardData : List<ListItem>

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)

        game = Game()
        tableCardData = game.getStartTableCardData() // 卓上

        activity?.let {
            view.findViewById<RecyclerView>(R.id.rv).apply {
                //  this.setHasFixedSize(true)  // あらかじめ固定サイズの場合にパフォーマンス向上
                layoutManager = GridLayoutManager(activity, 13)  // ここはフラグメントなので thisじゃなくて activityプロパティ
               // adapter = CardListAdapter(tableCardData)
                adapter = CardListAdapter(tableCardData, placeableList)
            }

        }



        return view  // フラグメントでは最後必ず viewを返す
    }


}