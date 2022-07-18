package to.msn.wings.sevengame.rv

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.MainActivity
import to.msn.wings.sevengame.R
import to.msn.wings.sevengame.StartFragment

/**
 * 卓上テーブル用
 */
class CardListAdapter(private val _data: List<ListItem>) : RecyclerView.Adapter<CardViewHolder>() {

    // 画面サイズ判定フラグ インスタンスフィールド アクティビティ(もしくはフラグメント)では onViewStateRestoredコールバックメソッドをオーバーライドする
    // onCreateViewHolderコールバックメソッド内で、所属するactivityは何か調べてから activityからフラグメントマネージャーを取得しfindFragmentById メソッドを使って
    //  .is_7Inch()  .is_10Inch() インスタンスメソッドで調べて この フィールドへ 代入します そして
    private var _isLayoutLarge7Inch : Boolean = false  // アダプターのクラスでは 初期値を falseにしておく

    private var _isLayoutXLarge10Inch : Boolean = false  // アダプターのクラスでは 初期値を falseにしておく

    /**
     * ビューホルダーを生成
     * ここでリスナーをつけることもできるが、今回は onBindViewHolderでリスナーをつける
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        // MainActivity の上のフラグメントしか、このアダプターは使ってないから、分岐処理は無いが、
        // AppCompatActivity は、MainActivityの時もあるし、そうでない時もあるから
        val context: Context = parent.context  // 大切 parentから contextプロパティを取得する
        val appCompatActivity: AppCompatActivity = context as AppCompatActivity  // 大切 contextプロパティを ダウンキャストする
        // ダウンキャストは 明示的に as演算子(キャスト演算子) で キャストをしてください

        var mainActivity: MainActivity? = null
        // もう一つのActivityがあればここに書く 例の為に書いてるだけ
        //   var monthCalendarActivity : MonthCalendarActivity? = null

        if (appCompatActivity.javaClass == MainActivity::class.java) {
            mainActivity = appCompatActivity as MainActivity  // ダウンキャストは 明示的に as演算子(キャスト演算子) で キャストをしてください
        }
//        else if (appCompatActivity.javaClass == MonthCalendarActivity::class.java) {  // 例のために書いてるだけ
//            monthCalendarActivity = appCompatActivity as MonthCalendarActivity
//        }

        var fmanager: FragmentManager? = null    //  import androidx.fragment.app.FragmentManager    androidx の方です

        if (mainActivity != null) {
            fmanager = (mainActivity as FragmentActivity).supportFragmentManager  // 明示的に ダウンキャストする必要がある
            // 取得した フラグメントマネージャーオブジェクトから
            // MainActivityに所属している フラグメントが取得できます findFragmentById メソッドを使って
            // MainActivityの上には StartFragmentが 所属しています    import android.R を書くとエラーになるので注意.
            val startFragment = fmanager.findFragmentById(R.id.startFragment) as StartFragment
            // startFragmentオブジェクトから、インスタンスメソッドの呼び出しをして、このクラスのフィールドに代入する
            // ここで フィールドへ代入しておき、後で onBindViewHoldeコールバックメソッド内で、使用します
            _isLayoutLarge7Inch = startFragment.is_7Inch()
            _isLayoutXLarge10Inch = startFragment.is_10Inch()

        }
//        else if (monthCalendarActivity != null) {  // 例のために書いてあるだけ
//            fmanager = (monthCalendarActivity as FragmentActivity).supportFragmentManager
//            // MonthCalendarActivityの 上には MonthCalendarFragment が乗っているので
//            val monthCalendarFragment = fmanager.findFragmentById(R.id.monthCalendarFragment) as MonthCalendarFragment?
//            _isLayoutXLarge = monthCalendarFragment!!.is_isLayoutXLarge()
//        }

        return CardViewHolder( cardView)
    }

    /**
     * ビューホルダーを生成した後によばれます.ビューにデータを割り当て、リスト項目を生成.data.sizeの数だけ実行されます
     *
     */
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        holder.mark.text = _data[position].mark.toString()
        holder.numberCenter.text = _data[position].numberCenter
        holder.markDown.text = _data[position].markDown.toString()
        holder.placed.text = _data[position].placed.toString()
        holder.tag.text = _data[position].tag

        // holder.itemView でルート要素のビューのCardView が取得できる
        val cardView : CardView = holder.itemView.findViewById(R.id.cardView);
        // 配下のウィジェットを取得　　holder.itemView はルートの要素の CardView そこからfindViewByIdを使うと取得できる
        val mark: TextView = holder.itemView.findViewById(R.id.mark)
        val numberCenter: TextView = holder.itemView.findViewById(R.id.numberCenter)
        val markDown: TextView = holder.itemView.findViewById(R.id.markDown)
        val placed: TextView = holder.itemView.findViewById(R.id.placed)
        val tag: TextView = holder.itemView.findViewById(R.id.tag)

        // バインドするときに もし、markが 1 4 なら 黒にする 2 3 なら 赤にする
        if (_data[position].mark.toString() == "1" || _data[position].mark.toString() == "4") {
            mark.setTextColor(Color.parseColor("#FF000000"))
            markDown.setTextColor(Color.parseColor("#FF000000"))
        } else if (_data[position].mark.toString() == "2" || _data[position].mark.toString() == "3") {
            mark.setTextColor(Color.parseColor("#ff0000"))
            markDown.setTextColor(Color.parseColor("#ff0000"))
        }
        // 非表示だけして、値は利用できるようにすること 置くときに値を変更する
        placed.visibility = View.GONE
        tag.visibility = View.GONE

        when(_data[position].mark.toString()) {
            "1" -> {
                mark.setText("♠")
                markDown.setText("♠")
            }
            "2" -> {
                mark.setText("♡")
                markDown.setText("♡")
            }
            "3" -> {
                mark.setText("♦")
                markDown.setText("♦")
            }
            "4" -> {
                mark.setText("♧")
                markDown.setText("♧")
            }
//            else -> {  // "5" JOKERの時です 今回は JOKER無しなので使用していませんが
//                mark.setText("")
//                markDown.setText("")
//            }
        }
        if (_data[position].placed == false) {
            mark.visibility = View.GONE
            markDown.visibility = View.GONE
            numberCenter.visibility = View.GONE
            // cardView.setCardBackgroundColor(null) // nullにはしない方がいい
            cardView.setCardBackgroundColor(Color.parseColor("#006c3a"))  // 色を重ねがけする方がいい
        }

        /**
         * リスナーをつけたいならば layoutで、rvを上に被せてると押せる でも、ここでは押せなくていいのでコメントアウトする
         */
//        cardView.setOnClickListener {   // it　は CardViewです
//            val tag = it.findViewById<TextView>(R.id.tag)
//            Log.i("ok", tag.text.toString() + "です" + it.toString() + "です クラスは" + it.javaClass)
//        }

        // 画面サイズによって、属性を変更します
        if (_isLayoutLarge7Inch) {  //  trueの時です
            holder.mark.setTextSize(22F)
            holder.numberCenter.setTextSize(29F)
            holder.markDown.setTextSize(22F)
        }
        if (_isLayoutXLarge10Inch) { //  trueの時です
            holder.mark.setTextSize(24F)
            holder.numberCenter.setTextSize(31F)
            holder.markDown.setTextSize(24F)
        }

    }

    /**
     * データのバインドはこの回数実行されます
     */
    override fun getItemCount(): Int {
        return _data.size
    }
}