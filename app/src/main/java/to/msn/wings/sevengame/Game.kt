package to.msn.wings.sevengame


import to.msn.wings.sevengame.playerrv.PlayerListItem
import to.msn.wings.sevengame.rv.ListItem
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * 今回はJOKERなしでゲームを作っていく.
 * アダプターのコンストラクタに渡すリストを、データベースなどから取得してきます.
 * 今回は、固定のリストなので、ここで作ってしまう.
 *
 */
class Game {
//  intent.putExtra する際に 第二引数が ArrayList<E> である必要があるために、なるべく ArrayList<E>を使うようにします (MutableListではなく)

    val _numberList: ArrayList<String> =
        arrayListOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")

    val _markList: ArrayList<Int> = arrayListOf(1, 2, 3, 4)   //  Int型  1:スペード    2: ハート    3: ダイヤ     4:クローバー

    val _tagList: ArrayList<String> = arrayListOf("S", "H", "D", "C")

    // LinkedHashMap　は　順序を記憶します どうやらputは使用せずにindexing operatorで追加するほうが良いようです。
    // 通常のgetも存在しますが、putと同様にindexing operatorの使用が推奨されています。
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
     * Javaで言う staticメソッド 静的メソッドのようなもの
     */
    companion object {
        fun staticMethodTest() {

        }
    }

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


//    fun getPossibleCardData(): HashSet<PossibleCard> {
//        // まず空の HashSetオブジェクトを生成して
//        val hashSet = hashSetOf<PossibleCard>()
//        var possibleCard: PossibleCard? = null
//        var str: String = ""
//
//        for (i in _markList.indices) { //  スペード ハート　ダイヤ　クローバーの順に作成する  indicesプロパティで 0..3 と同じになる  i in 0..3  と同じこと
//            for (j in 1..13) {  //   1　から 13
//                if (j == 7) {  // ７のカードだけは true:テーブルに置いてある　で作る
//                    str = _tagList.get(i) + (j).toString()
//                    possibleCard = PossibleCard(
//                        str,  // "S7"  "H7"  "D7"  "C7"
//                        _distanceList[j]!!,  //  0     7のカードを 起点   通常のgetも存在しますが、putと同様にindexing operatorの使用が推奨されています。
//                        true,  // もうテーブルには置いている
//                        false  // 次に置ける可能性のあるカードではありません (もうすでに置かれてるから)
//                    )
//                    hashSet.add(possibleCard)
//                } else if (j == 6) { //  6 のカード
//                    str = _tagList.get(i) + (j).toString()
//                    possibleCard = PossibleCard(
//                        str,  // "S6"  "H6"  "D6"  "C6"
//                        _distanceList[j]!!,  //   -1     7のカードを 起点
//                        false,  // まだテーブルには置いてない
//                        true  //  次に置ける可能性あり!!
//                    )
//                    hashSet.add(possibleCard)
//                } else if (j == 8) { //  8 のカード
//                    str = _tagList.get(i) + (j).toString()
//                    possibleCard = PossibleCard(
//                        str,   // "S8"  "H8"  "D8"  "C8"
//                        _distanceList[j]!!,  //   1      7のカードを起点
//                        false,  // まだテーブルには置いてない
//                        true  // 次に置ける可能性あり!!
//                    )
//                    hashSet.add(possibleCard)
//                } else if (j <= 5) {  // 1 2 3 4 5 のカード は false
//                //    var distance: Int = j - 7 //  距離は -6 -5 -4 -3 -2  (7のカードを起点 0)
//                    str = _tagList.get(i) + (j).toString()
//                    possibleCard = PossibleCard(
//                        str,
//                        _distanceList[j]!!,
//                    //    distance, //   -6 -5 -4 -3 -2
//                        false,  // まだテーブルには置いてない
//                        false  // 次に置ける可能性無し
//                    )
//                    hashSet.add(possibleCard)
//                } else if (j >= 9) { //  9 10 11 12 13 のカードは false  距離は  2 3 4 5 6 (7のカードを起点 0)
//                 //   var distance: Int = j - 7  // 距離は  2 3 4 5 6 (7のカードを起点 0)
//                    str = _tagList.get(i) + (j).toString()
//                    possibleCard = PossibleCard(
//                        str,
//                        _distanceList[j]!!,
//                  //      distance, //       2 3 4 5 6
//                        false,  // まだテーブルには置いてない
//                        false  // 次に置ける可能性無し
//                    )
//                    hashSet.add(possibleCard)
//                }
//            }
//        }
//        return hashSet
//    }


    /**
     * n だけ先のオブジェクト取得. n は負の数の時もある.
     * 置いたカードの数が 8以上だとnは正の数　  6以下だと nは負の数
     */
//    fun getNPossibleCard(set: Set<PossibleCard>, tag: String, n: Int): PossibleCard? {
//
//        var mark: String = tag.substring(0,1)  // "S"とか
//        var numInt: Int = tag.substring(1).toInt()  // 8以上だとnは正の数　  6以下だと nは負の数
//        var newTagstr: String = mark + (numInt + n).toString() // ８だと ９になり  6だと ５になる
//        var possibleCard: PossibleCard? = null
//        for (item in set) {
//            if(item.tag.equals(newTagstr)) {
//                possibleCard = item
//            }
//        }
//        return possibleCard
//    }


    /**
     * 引数のタグと同じマークで、引数num が指定の数 となるカードを取得する
     */
    fun getPossibleCard(set: Set<PossibleCard>, tag: String, num: Int): PossibleCard? {

        var mark: String = tag.substring(0,1)  // "S"とか

        var newTagstr: String = mark + num.toString()

        var possibleCard: PossibleCard? = null
        for (item in set) {
            if(item.tag.equals(newTagstr)) {
                possibleCard = item
            }
        }
        return possibleCard
    }

}