package to.msn.wings.sevengame


import to.msn.wings.sevengame.playerrv.PlayerListItem
import to.msn.wings.sevengame.rv.ListItem
import java.util.*
import kotlin.collections.ArrayList

/**
 * 今回はJOKERなしでゲームを作っていく.
 * アダプターのコンストラクタに渡すリストを、データベースなどから取得してきます.
 * 今回は、固定のリストなので、ここで作ってしまう.
 *
 */
class Game {
//  intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)

    // val _numberList : List<String> = arrayOf("A", "2", "3","4","5","6","7","8","9","10","J", "Q", "K").toList()
    val _numberList: ArrayList<String> =
        arrayListOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    //  Int型  1:スペード    2: ハート    3: ダイヤ     4:クローバー
    //   val _markList : List<Int> = arrayOf(1, 2, 3, 4).toList()
    val _markList: ArrayList<Int> = arrayListOf(1, 2, 3, 4)

    //    val _tagList : List<String> = arrayOf("S", "H", "D", "C").toList()
    val _tagList: ArrayList<String> = arrayListOf("S", "H", "D", "C")

    /**
     * 最初に卓上に置かれているカードのリストを作成し返す.
     * 最初に 7のカードだけが置かれている状態になるようにする.
     * 戻り値の型は ArrayList にすること
     */
    fun getStartTableCardData(): ArrayList<ListItem> {
        // まずリストを空で生成しておき、
        //    val muList  = mutableListOf<ListItem>()  // あとで toList()をつけて List<ListItem>にすること    var data : List<ListItem> = muList.toList()
        val arrayList = arrayListOf<ListItem>()

        // まず、52枚のトランプカードを作る (ジョーカー以外) 管理ID 1 から 52のカードを作る
        var listItem: ListItem? = null
        var count: Long = 0
        var str: String = ""

        for (i in _tagList.indices) { // indices プロパティによって IntRangeの 0..3 が取得できますfor (i in 0..3) と書いたのと同じになります
            for (j in 1..13) { // ジョーカー以外をまず作成する スペード ハート　ダイヤ　クローバーの順に作成する
                if (j == 7) {  // ７だけをきちんと作る
                    str = _tagList.get(i) + (j).toString()
                    listItem = ListItem(
                        ++count, // 管理IDは １からスタートして連番で振る Long値
                        _markList.get(i),
                        _numberList.get(j - 1),
                        _markList.get(i),
                        true,  // 置いてある
                        str
                    )
                } else {  // 7以外の時は 卓上には置いていないので、マークは 0
                    str = _tagList.get(i) + (j).toString()  // これはどうしようか
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
     */
    fun getPlayersCardData(): ArrayList<PlayerListItem> {
        // まずリストを空で生成しておき、
        //    val muList  = mutableListOf<PlayerListItem>()  //  後で変換する var data : List<PlayerListItem> = muList.toList()
        val arrayList = arrayListOf<PlayerListItem>()

        // まず、52枚のトランプカードを作る (ジョーカー以外) ID 1 から 52のカードを作る
        var playerListItem: PlayerListItem? = null
        var count: Long = 0
        for (i in _markList.indices) { //  スペード ハート　ダイヤ　クローバーの順に作成する  indicesプロパティで 0..3 と同じになる  i in 0..3  と同じこと
            for (j in 1..13) {
                if (j != 7) {  //  7のカードは作らない
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
        //   var joker = PlayerListItem(49, "JOKER", 5)  // 管理IDは 1からスタートだから 7のカードは作らないから 49
        //    arrayList.add(joker)

        // 注意 ディープコピーをしたものを 返すようにすること
        Collections.shuffle(arrayList)  // シャッフルする
        // ディープコピー 新たに 別のオブジェクトを生成しています
        val deepArrayList: ArrayList<PlayerListItem> = ArrayList<PlayerListItem>(arrayList)

        // リターンするのは 新しく生成したリストです
        return deepArrayList
    }

    fun getPossibleCardData(): HashSet<PossibleCard> {
        // まず空の HashSetオブジェクトを生成して
        val hashSet = hashSetOf<PossibleCard>()
        var possibleCard: PossibleCard? = null
        var str: String = ""

        for (i in _markList.indices) { //  スペード ハート　ダイヤ　クローバーの順に作成する  indicesプロパティで 0..3 と同じになる  i in 0..3  と同じこと
            for (j in 1..13) {  //   1　から 13
                if (j == 7) {  // ７のカードだけは true:テーブルに置いてある　で作る
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,  // "S7"  "H7"  "D7"  "C7"
                        0,  //       7のカードを 起点
                        true,  // もうテーブルには置いている
                        false  // 次に置ける可能性のあるカードではありません (もうすでに置かれてるから)
                    )
                    hashSet.add(possibleCard)
                } else if (j == 6) { //  6 のカード
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,  // "S6"  "H6"  "D6"  "C6"
                        -1,  //        7のカードを 起点
                        false,  // まだテーブルには置いてない
                        true  //  次に置ける可能性あり!!
                    )
                    hashSet.add(possibleCard)
                } else if (j == 8) { //  8 のカード
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,   // "S8"  "H8"  "D8"  "C8"
                        1,  //      7のカードを起点
                        false,  // まだテーブルには置いてない
                        true  // 次に置ける可能性あり!!
                    )
                    hashSet.add(possibleCard)
                } else if (j <= 5) {  // 1 2 3 4 5 のカード は false
                    var distance: Int = j - 7 //  距離は -6 -5 -4 -3 -2  (7のカードを起点 0)
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,
                        distance, //   -6 -5 -4 -3 -2
                        false,  // まだテーブルには置いてない
                        false  // 次に置ける可能性無し
                    )
                    hashSet.add(possibleCard)
                } else if (j >= 9) { //  9 10 11 12 13 のカードは false  距離は  2 3 4 5 6 (7のカードを起点 0)
                    var distance: Int = j - 7  // 距離は  2 3 4 5 6 (7のカードを起点 0)
                    str = _tagList.get(i) + (j).toString()
                    possibleCard = PossibleCard(
                        str,
                        distance, //       2 3 4 5 6
                        false,  // まだテーブルには置いてない
                        false  // 次に置ける可能性無し
                    )
                    hashSet.add(possibleCard)
                }
            }

        }
        return hashSet
    }
}