package to.msn.wings.sevengame


import to.msn.wings.sevengame.playerrv.PlayerListItem
import to.msn.wings.sevengame.rv.ListItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * 今回はJOKERなしでゲームを作っていく.
 * プレイヤー(あなた)と コンピュータA  コンピュータB の 3人でゲームする
 * 本来は アダプターのコンストラクタに渡すリストを、データベースなどから取得してきます.
 * 今回は、固定のリストなので、ここで作ってしまう.
 * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、intentで遷移する時に渡すならば、ArrayList<E>を使う(MutableListではなく)
 */
class Game {

    val _numberList: ArrayList<String> =
        arrayListOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    val _markList: ArrayList<Int> = arrayListOf(1, 2, 3, 4) // Int型  1:スペード  2: ハート  3: ダイヤ  4:クローバー もしジョーカー有りをするなら 5:JOKER とする 今回はジョーカー無し

    val _tagList: ArrayList<String> = arrayListOf("S", "H", "D", "C")

    // LinkedHashMap　は　順序を記憶します どうやらputは使用せずにindexing operatorで追加するほうが良いようです。
    // 通常のgetも存在しますが、putと同様にindexing operatorの使用が推奨されています。
    // 今後、JOKERありの時に使うかもしれないため、PossibleCardオブジェクトの distanceフィールドを 作るため 使用するリスト
    val _distanceList: LinkedHashMap<Int, Int> = linkedMapOf(
        1 to -6,
        2 to -5,
        3 to -4,
        4 to -3,
        5 to -2,
        6 to -1,
        7 to 0,  //  7を起点として、7からの 距離
        8 to 1,
        9 to 2,
        10 to 3,
        11 to 4,
        12 to 5,
        13 to 6
    )

    /**
     * 最初に卓上に置かれているカードのリストを作成し返す.
     * 最初に 7のカードだけが置かれている状態になるようにする. placedフィールドを ７だけ true とする
     * 戻り値の型は ArrayList にすること
     * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)
     */
    fun getStartTableCardData(): ArrayList<ListItem> {
        // まずリストを空で生成しておき、
        val arrayList = arrayListOf<ListItem>()

        // まず、52枚のトランプカードを作る (ジョーカー以外) 管理ID 1 から 52のカードを作る
        var listItem: ListItem? = null
        var count: Long = 0
        var str: String = ""

        for (i in _tagList.indices) { // indices プロパティによって IntRangeの 0..3 が取得できますfor (i in 0..3) と書いたのと同じになります
            for (j in 1..13) { // ジョーカー以外をまず作成する スペード ハート　ダイヤ　クローバーの順に作成する
                if (j == 7) {  // ７のカード
                    str = _tagList.get(i) + (j).toString()
                    listItem = ListItem(
                        ++count, // 管理IDは １からスタートして連番で振る Long値
                        _markList.get(i),
                        _numberList.get(j - 1),
                        _markList.get(i),
                        true,  // 7だけは 最初から置いてある
                        str
                    )
                } else {  // 7以外 の時は 最初は卓上には置いていない
                    str = _tagList.get(i) + (j).toString()
                    listItem = ListItem(
                        ++count, // 管理IDは １からスタートして連番で振る
                        _markList.get(i),
                        _numberList.get(j - 1),
                        _markList.get(i),
                        false,// 卓上に置いていない
                        str
                    )
                }
                arrayList.add(listItem) // 可変リストに加えていく
            }
        }
        return arrayList
    }

    /**
     * ３人でするゲームのトランプ.
     * "7"のカードは 最初から卓上に並べてある設定にしてるから 作らない.
     * Collections.shuffleして変更を加えたら、それを元にして新しいオブジェクトを生成し、そのオブジェクトを返します.全く別のオブジェクトにする
     * 戻り値の型は ArrayList にすること
     * intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)
     */
    fun getPlayersCardData(): ArrayList<PlayerListItem> {
        // まずリストを空で生成しておき、
        val arrayList = arrayListOf<PlayerListItem>()

        // まず、52枚のトランプカードを作る (ジョーカー以外) ID 1 から 52のカードを作る
        var playerListItem: PlayerListItem? = null
        var count: Long = 0
        for (i in _markList.indices) { //  スペード ハート　ダイヤ　クローバーの順に作成する  indicesプロパティで 0..3 と同じになる  i in 0..3  と同じこと
            for (j in 1..13) {
                if (j != 7) {  //  7 のカードは作りません!!  7 以外を作る
                    playerListItem = PlayerListItem(
                        ++count, // 管理IDは １からスタートして連番で振る
                        _numberList.get(j - 1), // String型
                        _markList.get(i),  // Int型
                        _tagList.get(i) + j
                    )
                    arrayList.add(playerListItem) // 可変リストに加えていく
                }
            }
        }
        // もし、ジョーカー1枚を追加するならここで作成するが 今回は作らない
        //   var joker = PlayerListItem(49, "JOKER", 5, "JOKER")  // 管理IDは 1からスタートだから 7のカードは作らないから 49
        //    arrayList.add(joker)

        // 注意 ディープコピーをしたものを 返すようにすること
        Collections.shuffle(arrayList)  // シャッフルする
        // ディープコピー 新たに 別のオブジェクトを生成しています
        val deepArrayList: ArrayList<PlayerListItem> = ArrayList<PlayerListItem>(arrayList)

        // リターンするのは 新しく生成したリストです
        return deepArrayList
    }

    /**
     * 初期値としてリストを作成する.次に置ける可能性のあるカードは placed属性が false かつ possible属性が trueのカードになります.
     */
    fun getPossibleCardData(): ArrayList<PossibleCard> {
        // まず空の ArrayListオブジェクトを生成して
        val arrayList = arrayListOf<PossibleCard>()
        var possibleCard: PossibleCard? = null
        var str: String = ""

        for (i in _markList.indices) { //  スペード ハート　ダイヤ　クローバーの順に作成する  indicesプロパティで 0..3 と同じになる  i in 0..3  と同じこと
            for (j in 1..13) {  //   1　から 13
                if (j == 7) {  // ７のカードだけは true:テーブルに置いてある　で作る
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,  // "S7"  "H7"  "D7"  "C7"
                        _distanceList[j]!!,  //  0     7のカードを 起点   通常のgetも存在しますが、putと同様にindexing operatorの使用が推奨されています。
                        true,  // もうテーブルには置いている
                        false  // 次に置ける可能性のあるカードではありません (もうすでに置かれてるから)
                    )
                    arrayList.add(possibleCard)
                } else if (j == 6) { //  6 のカード
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,  // "S6"  "H6"  "D6"  "C6"
                        _distanceList[j]!!,  //   -1     7のカードを 起点
                        false,  // まだテーブルには置いてない
                        true  //  次に置ける可能性あり!!
                    )
                    arrayList.add(possibleCard)
                } else if (j == 8) { //  8 のカード
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,   // "S8"  "H8"  "D8"  "C8"
                        _distanceList[j]!!,  //   1      7のカードを起点
                        false,  // まだテーブルには置いてない
                        true  // 次に置ける可能性あり!!
                    )
                    arrayList.add(possibleCard)
                } else if (j <= 5) {  // 1 2 3 4 5 のカード は false
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,
                        _distanceList[j]!!,
                        false,  // まだテーブルには置いてない
                        false  // 次に置ける可能性無し
                    )
                    arrayList.add(possibleCard)
                } else if (j >= 9) { //  9 10 11 12 13 のカードは false
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,
                        _distanceList[j]!!,
                        false,  // まだテーブルには置いてない
                        false  // 次に置ける可能性無し
                    )
                    arrayList.add(possibleCard)
                }
            }
        }
        return arrayList
    }

    /**
     * オーバーロード(多重定義)
     * インデックで要素を取得したいなら、戻り値は Listにすべきです Setは順番を持たないからです
     * ArrayList<PossibleCard>　の中から 同じタグで かつ possible属性が true の PossibleCardオブジェクトを取得
     * 置くことのできそうなカードを探してリストにする 複数見つかる時もあるし、空のリストを返す時もある
     * 戻り値 ArrayList<PossibleCard>型です
     */
    fun getSubList(poList: ArrayList<PossibleCard>, comList: ArrayList<PlayerListItem>): ArrayList<PossibleCard> {
        val arrayList: ArrayList<PossibleCard> = ArrayList()
        for (possibleCard in poList) {
            for (item in comList) {
                if (possibleCard.tag.equals(item.pTag) && possibleCard.possible == true) {
                    arrayList.add(possibleCard)
                }
            }
        }
        return arrayList
    }

    /**
     * 多重定義(オーバーロード) シグネチャが異なれば同名のメソッドで、異なる内容の処理が書ける.
     * リストをディープコピーする.
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
     * リストをディープコピーする.新しい別のオブジェクトを生成する(全く同じ内容にする).
     * 多重定義(オーバーロード) シグネチャが異なれば同名のメソッドで、異なる内容の処理が書ける.
     * 新しくオブジェクトを作り直して ディープコピーをする MutableListじゃないとだめ
     */
    fun <T> getSubList(list: List<T>): List<T>? {
        val subList: MutableList<T> = ArrayList()  // MutableList
        for (i in list.indices) { // indicesプロパティで   インデックスの範囲が得られる  0..6　など IntRange
            subList.add(list[i])
        }
        return subList
    }

    /**
     * 引数のタグと同じマークで、引数num が指定の数 となるカードを取得する.
     */
    fun getPossibleCard(list: ArrayList<PossibleCard>, tag: String, num: Int): PossibleCard? {
        var mark: String = tag.substring(0,1)  // "S"とか
        var newTagstr: String = mark + num.toString()

        var possibleCard: PossibleCard? = null
        for (item in list) {
            if(item.tag.equals(newTagstr)) {
                possibleCard = item
            }
        }
        return possibleCard
    }

}