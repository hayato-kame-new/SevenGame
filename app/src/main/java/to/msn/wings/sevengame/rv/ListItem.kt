package to.msn.wings.sevengame.rv

/**
 * data class です
 * RecyclerViewのための
 */
data class ListItem(
    val id: Long,  //  識別のための
    val number : String,  // 1は A と書く  11は J   12 は Q   13は K   JOKER もある  ただし今回はJOKER無し
    val mark : Int,  //  1:スペード   2:ハート  3:ダイヤ 3   4:クローバー 5:ジョーカー  0  だとまだ作られてないカード
    val numberCenter : String,  // numberと同じ
    val numberDown : String,  // numberと同じ
    val markDown : Int,  // markとおなじ
    val placed : Boolean,  // 　true: 置いてある false:置いてない
    val tag : String
)
