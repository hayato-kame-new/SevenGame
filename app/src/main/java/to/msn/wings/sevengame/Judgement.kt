package to.msn.wings.sevengame

class Judgement( poList: ArrayList<PossibleCard> ){

    // フィールド
    val _game = Game()
    // ディープコピーすること   そしてこの値を最後メソッドで返す　
    val _deepPossibleCardList: ArrayList<PossibleCard> = _game.getSubList(poList) as ArrayList<PossibleCard>


//    val subSSmall = _game.getSubList(_deepPossibleCardList, 0, (_deepPossibleCardList.size / 8) - 1 ) as ArrayList<PossibleCard>
//    // 7のカードは含めないで作る "S8" から "S13"
//    val subSBig = _game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size / 8) + 1, (_deepPossibleCardList.size * 2 / 8) - 1 ) as ArrayList<PossibleCard>
//
//    // "H1" から "H6"
//    val subHSmall = _game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 2 / 8, (_deepPossibleCardList.size * 3 / 8) - 1 ) as ArrayList<PossibleCard>
//    // 7のカードは含めないで作る "H8" から "H13"
//    val subHBig = _game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 3 / 8) + 1, (_deepPossibleCardList.size * 4 / 8) - 1 ) as ArrayList<PossibleCard>
//
//    // "D1" から "D6"
//    val subDSmall = _game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 4 / 8, (_deepPossibleCardList.size * 5 / 8) - 1 ) as ArrayList<PossibleCard>
//    // 7のカードは含めないで作る "D8" から "D13"
//    val subDBig = _game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 5 / 8) + 1, (_deepPossibleCardList.size * 6 / 8) - 1 ) as ArrayList<PossibleCard>
//
//    // "C1" から "C2"
//    val subCSmall = _game.getSubList(_deepPossibleCardList, _deepPossibleCardList.size * 6 / 8, (_deepPossibleCardList.size * 7 / 8) - 1 ) as ArrayList<PossibleCard>
//    // 7のカードは含めないで作る "C8" から "C13"
//    val subCBig = _game.getSubList(_deepPossibleCardList, (_deepPossibleCardList.size * 7 / 8) + 1, _deepPossibleCardList.size - 1 ) as ArrayList<PossibleCard>
//    val _tagList: ArrayList<String> = arrayListOf("S", "H", "D", "C")


    /**
     * 小さいカード置いた時
     */
    fun methodSmall( pTagStr: String): ArrayList<PossibleCard>{
        val putCardNum = pTagStr.substring(1).toInt()  // 置いたカードの数字
        if (putCardNum == 1) {
            var count = 0
            for (num in 8..13) {
                var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                if (card != null && card.placed == true) {
                    count++
                }
            }
            if (count == 6) {
                // カウント6だったら、13まで置かれていて 1を置いてる つまり逆向きになってる
                //  1 2 3 4 5 6 と置けるようにする
                for (num in 1..6) {
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    if (card != null && card.placed == false) { // もし、5から１まで　リストに まだ置いてないカードが見つかった時点で
                        card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                        break // 抜ける
                    }
                }
            } else {
                // カウント6 でなければ、普通の向きです "1"を置いたから 次は  13から置くように変更する
                for (num in 13 downTo 8) {
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    // 余計な possibleをクリアする 一旦全部クリアする 8 ~ 13 の
                    if (card != null && card.placed == false && card.possible == true) {
                        card.possible = false //全部　置ける を 一旦 置けない でクリアしておく 全部だから breakはしない
                    }
                }
                // もう一度別のループで設定し直す
                for (num in 13 downTo 8) {
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    // 設定し直しする
                    if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                        card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
                        break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
                    }
                }
            }
        } else {
            for (num in 6 downTo 1) {
                var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                    card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                    break // 抜ける
                }
            }
        }
        return _deepPossibleCardList  // 最後書き換えたのを返す 呼び出し元で必要です
    }


    /**
     * 置いたカード大きい時
     */
    fun methodBig(pTagStr: String): ArrayList<PossibleCard> {
        val putCardNum = pTagStr.substring(1).toInt()
            if (putCardNum == 13) {
                var count = 0
                for (num in 1..6) {
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    if (card != null && card.placed == true) {
                        count++
                    }
                }
                if (count == 6) {
                    // カウントが 6ならば 1まで置かれていて 13を置いてる つまり逆向きになってる
                    //  13 12 11 10 9 8  と置けるようにする
                    for (num in 13 downTo 8) {
                        var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                        if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                            break // 抜ける
                        }
                    }
                } else {
                    // カウントが 6 でなければ、普通の向きです
                    // 今度は 1から置くように変更する
                    for (num in 1..6) {
                        var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                        // 余計な possibleをクリアする
                        if (card != null && card.placed == false && card.possible == true) {
                            card.possible = false //全部　置ける を 一旦 置けない でクリアしておく 全部だから breakはしない
                        }
                    }
                    // もう一度別のループで設定し直す
                    for (num in 1..6) {
                        var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                        // 設定し直しする
                        if (card != null && card.placed == false) { // もし、まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる 最初に見つかったやつだけ
                            break // 抜ける 最初に見つかったら trueにしたらすぐループを抜ける
                        }
                    }
                }
            } else {
                for (num in 8..13) {
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                        card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                        break // 抜ける
                    }
                }
            }
        return _deepPossibleCardList  // 最後書き換えたのを返す 呼び出し元で必要です
    }
}