package to.msn.wings.sevengame


data class PossibleCard(
    var tag: String,   // "S1" など
    var distance: Int,   // 7 を　0 として  8  9  10 を +1 +2 +3    6 5 4 を -1 -2 -3 という絶対値で表す
    var placed: Boolean,  // テーブルに置いてあるのかどうか true: 置いてある   false: 置いてない
    var possible: Boolean  // 置ける可能性あるかどうか true: ある   false: ない
    ) {

}