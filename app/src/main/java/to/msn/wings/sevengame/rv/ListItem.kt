package to.msn.wings.sevengame.rv

/**
 * data class です
 * RecyclerViewのための
 */
data class ListItem(
    val id: Long,  //  識別のための
    val mark : Int,  //  1:スペード   2:ハート  3:ダイヤ 3   4:クローバー 5:ジョーカー  0  だとまだ作られてないカード
    val numberCenter : String,
    val markDown : Int,
    val placed : Boolean,  // 　true: 置いてある false:置いてない
    val tag : String
)
