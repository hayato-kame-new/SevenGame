package to.msn.wings.sevengame


import to.msn.wings.sevengame.rv.ListItem

/**
 * 今回はJOKERなしでゲームを作っていく.
 * アダプターのコンストラクタに渡すリストを、データベースなどから取得してきます.
 * 今回は、固定のリストなので、ここで作ってしまう
 *
 */
class Game {

    val numberList : List<String> = arrayOf("A", "2", "3","4","5","6","7","8","9","10","J", "Q", "K").toList()
    val markList : List<Int> = arrayOf(1, 2, 3, 4).toList() // Int型  1:スペード    2: ハート    3: ダイヤ     4:クローバー 　5:ジョーカーとする  0:まだ作らない   1,2,3,4だけでつくる
    val tagList : List<String> = arrayOf("S", "H", "D", "C").toList()  // 後で判定するのにタグが必要

    /**
     * 最初に卓上に置かれているカードのリストを作成し返す
     * 最初に 7のカードだけが置かれている状態になるようにする
     */
    fun getStartTableCardData() : List<ListItem>{
        // まず可変リストを空で生成しておき、
        val muList  = mutableListOf<ListItem>()  // あとで toList()をつけて List<ListItem>にすること    var data : List<ListItem> = muList.toList()
        // まず、52枚のトランプカードを作る (ジョーカー以外) 管理ID 1 から 52のカードを作る
        var listItem: ListItem? = null
        var count: Long = 0
        var str: String = ""

        for (i in tagList.indices) { // indices プロパティによって IntRangeの 0..3 が取得できますfor (i in 0..3) と書いたのと同じになります
            for (j in 0..12) { // ジョーカー以外をまず作成する スペード ハート　ダイヤ　クローバーの順に作成する
                if ( j == 6) {  // ７だけをきちんと作る
                    str = tagList.get(i) + (j+1).toString()
                    listItem = ListItem(
                        ++count, // 管理IDは １からスタートして連番で振る Long値
                        numberList.get(j),
                        markList.get(i),
                        numberList.get(j),
                        numberList.get(j),
                        markList.get(i),
                        true,  // 置いてある
                        str
                    )
                } else {  // 7以外の時は 卓上には置いていないので、マークは 0
                    str = tagList.get(i) + (j+1).toString()  // これはどうしようか
                    listItem = ListItem(
                        ++count, // 管理IDは １からスタートして連番で振る
                        "",
                        0,  // マークは とりあえず 0
                        "",
                        "",
                        0, // マークは とりあえず 0
                        false,// 卓上に置いていない
                        str  // これも空文字に後で変更しておいた方がいいのかも、でも判定する時に必要かも？？？
                    )
                }
                muList.add(listItem) // 可変リストに加えていく
            }
        }
        // あと、ジョーカー1枚を追加する?  しかし、今回ははいらない
//        var joker = ListItem(53, "JOKER", 5, "JOKER", "JOKER" ,5)  // IDは 1からスタートだから 53
//        muList.add(joker)

        // 可変リストを  toList()により List<ListItem>型に変換しました  アダプターの引数に使うので 型を変換する必要がある
        var tableCardData : List<ListItem> = muList.toList()

//        val data : List<ListItem> = arrayOf(
//            ListItem(1, "A", 1, "A", "A", 1),
//            ListItem(2, "2", 1, "2", "2", 1),
//            ListItem(3, "3",  1, "3", "3", 1),
//        ).toList()
        return tableCardData
    }

}