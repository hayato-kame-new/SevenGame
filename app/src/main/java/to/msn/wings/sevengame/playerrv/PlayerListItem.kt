package to.msn.wings.sevengame.playerrv

/**
 * データクラスです、もし、自作したクラスを アクティビティ間で移動させるためには Serializableインタフェースを実装します.
 * Serializableを実装すべきです.
 * import java.io.Serializable  をインポートします.
 */
data class PlayerListItem(
    var pId: Long,  //  識別のための
    val pNumber : String,
    val pMark : Int,
    var pTag : String
)
