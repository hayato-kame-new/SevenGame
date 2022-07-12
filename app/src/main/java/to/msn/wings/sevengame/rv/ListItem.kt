package to.msn.wings.sevengame.rv

import android.annotation.SuppressLint
import android.os.Parcelable
import java.io.Serializable
/**
 * data class です.卓上テーブル用.  自作したクラスを intentで送るためには Serializableインタフェースを実装しなくてはなりません.
 * RecyclerViewのための
 * import java.io.Serializable  をインポートして Serializableインタフェースを実装しないと intentで送れません
 */
data class ListItem(
    val id: Long,  //  識別のための
    val mark : Int,  //  1:スペード   2:ハート  3:ダイヤ 3   4:クローバー 5:ジョーカー  0  だとまだ作られてないカード
    val numberCenter : String,
    val markDown : Int,
    var placed : Boolean,  // 　true: 置いてある false:置いてない
    val tag : String,
) : Serializable  // 自作したクラスを intentで送るためには Serializableインタフェースを実装します