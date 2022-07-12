package to.msn.wings.sevengame.playerrv

import java.io.Serializable
/**
 * データクラスです.自作したクラスを intentで送るためには Serializableインタフェースを実装しなくてはなりません.
 * RecyclerViewのためのクラス.
 * import java.io.Serializable  をインポートします.
 */
data class PlayerListItem(
    var pId: Long,  //  識別のための
    val pNumber : String,
    val pMark : Int,
    var pTag : String
) : Serializable  // intentで送るためには Serializableを実装すべき
