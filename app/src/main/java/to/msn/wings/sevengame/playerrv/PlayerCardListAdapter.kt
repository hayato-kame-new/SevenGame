package to.msn.wings.sevengame.playerrv

//  import android.R を書くとエラーになるので注意.
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import to.msn.wings.sevengame.*
import to.msn.wings.sevengame.rv.ListItem
import kotlin.collections.ArrayList


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

    // 画面サイズ判定フラグ インスタンスフィールド アクティビティ(もしくはフラグメント)では onViewStateRestoredコールバックメソッドをオーバーライドする
    // onCreateViewHolderコールバックメソッド内で、所属するactivityは何か調べてから activityからフラグメントマネージャーを取得しfindFragmentById メソッドを使って
    //  .is_7Inch()  .is_10Inch() インスタンスメソッドで調べて この フィールドへ 代入します そして
    private var _isLayoutLarge7Inch : Boolean = false  // アダプターのクラスでは 初期値を falseにしておく

    private var _isLayoutXLarge10Inch : Boolean = false  // アダプターのクラスでは 初期値を falseにしておく

    // 遅延して コンストラクタの引数で渡ってきたものをフィールド値にセットします
    //  変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    //  ここでは onCreate()の後に呼ばれる onCreateViewHolderの中で 代入をして初期化しています
    private lateinit var _deepPossibleCardList: ArrayList<PossibleCard>  // フィールド宣言だけ 　フィールドの型は実装の型にすること
    private lateinit var _tableCardData: ArrayList<ListItem>  // フィールド宣言だけ フィールドの型は実装の型にすること
    // コンストラクタの val data は　読み取り専用だから これも 違う変数名で新しく フィールドとして宣言します
    private lateinit var _deepDataList: ArrayList<PlayerListItem>  // フィールド宣言だけ　　onCreateViewHolderの中で 代入をして初期化しています フィールドの型は実装の型にすること
    private lateinit var _deepComAList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること
    private lateinit var _deepComBList: ArrayList<PlayerListItem> // フィールド宣言だけ フィールドの型は実装の型にすること

    /**
     * ビューホルダーを生成.
     * import android.R を書くとエラーになるので注意.
     * 引数の parent から contextプロパティが取得できます
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerCardViewHolder {

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


        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        // ここで lateinit varフィールドへ 初期値をセットします (引数で渡ってきた値を使って)
        // ディープコピー 新たに 別のオブジェクトを生成しています 注意！！！ ディープコピーにしないとエラー _cardSet = cardSet としてはいけない バインドが終わるまで cardSetも変わってしまってはいけないからです
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

    /**
     * ビューにデータを割り当て、リスト項目を生成.
     * データのバインドは getItemCount()の戻り値の回数だけ 実行されます (data.size の数だけ).
     * バインドする際に、リスナーをつけておきます。
     * バインドする際に、ここで属性を変更できる.画面サイズによって属性を動的に変更もできます.
     */
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
                // あなたの勝ちです！！ ゲーム終了 一番先に 手持ちが 0になった人の勝ちで ゲーム終了
                if (_deepDataList.size == 0) {
                    // あなたの勝ちです！！ ダイアログ表示出す  ここでダイアログを表示して、もう一度ゲームをするだけを作る
                    AlertDialog.Builder(context) // FragmentではActivityを取得して生成  Adapter onBindViewHolder では holder.itemView.context
                        .setTitle("あなたの勝ちです")
                        .setMessage("ゲーム再開する")
                        .setPositiveButton("OK", { dialog, which ->
                            context.startActivity(intent)
                        })
                        //   .show()
                        .setCancelable(false).show().setCanceledOnTouchOutside(false)  //  .setCancelable(false).show().setCanceledOnTouchOutside(false) の順番
                    // もう一度ゲームをするを押したら、 intent を発行して、extras を nullにしておけば、また、　最初から始まる　つまり何も putExtraしないこと
                // setCancelable を false にすると、戻るボタンを押してもダイアログが閉じなくなります。
                // そして Dialog クラスの setCanceledOnTouchOutside メソッドを false にすると、ダイアログの外側をタップしてもダイアログが閉じなくなります
                } else {

                    // そして、 卓上の_tableCardDataのアイテムListItemの属性を変更すること ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                    for (item in _tableCardData) {
                        if (item.tag.equals(txtP.text)) {
                            item.placed = true  // placedプロパティを falseの時には View.GONEにしてるから trueにすれば非表示ではなくなります
                        }
                    }

                    // さらに、　出したカードの属性を変更する ただの属性の書き換えなので、イテレータはなくても大丈夫 forが使える
                    for (item in _deepPossibleCardList) {
                        if (item.tag.equals(txtP.text.toString())) {
                            item.placed = true  // 置いた
                            item.possible = false // もう卓上に置いたから、 次に置けるカードでは無くなったので falseにする
                        }
                    }
                    // 次に出せるカードの変更
                    val pTagStr: String = txtP.text.toString()  // 置いたカードのタグの文字列 "S6" とか
                    val putNum: Int = pTagStr.substring(1).toInt()
                    val game =  Game()
                    // さらに、次に出せるカードの属性を変更する
                    val judge = Judgement(_deepPossibleCardList)
                    if (putNum in 1..6) {
                        val list = judge.methodSmall(pTagStr)  // 属性を書き換えた リストを返すので、
                        _deepPossibleCardList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                    } else if (putNum in 8..13) {
                        val list = judge.methodBig(pTagStr)  // 属性を書き換えた リストを返すので、
                        _deepPossibleCardList = game.getSubList(list) as ArrayList<PossibleCard>  // ディープコピー
                    }

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
                }
            } else {
                // クリックしたカードは置けないカードだったので
                val toast: Toast = Toast.makeText(context, context.getString(R.string.uncontained), Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        // リスナーここまで

        // 画面サイズによって、属性を変更します
        if (_isLayoutLarge7Inch) {  //  trueの時です
            holder.pNumber.setTextSize(26F)
            holder.pMark.setTextSize(24F)
        }
        if (_isLayoutXLarge10Inch) { //  trueの時です
            holder.pNumber.setTextSize(28F)
            holder.pMark.setTextSize(26F)
        }

    }

    /**
     * データのバインドはこの回数実行されます
     */
    override fun getItemCount(): Int {
        return data.size
    }

}