package to.msn.wings.sevengame

class Judgement( poList: ArrayList<PossibleCard> ){

    // フィールド
    val _game = Game()
    // ディープコピーすること   そしてこの値を最後メソッドで返す　
    val _deepPossibleCardList: ArrayList<PossibleCard> = _game.getSubList(poList) as ArrayList<PossibleCard>
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
        } else { // 置いたカード１以外の時 2 ~ 6の時

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
                // カウント6 でなければ、普通の向きです
                // 置いたカードが 2〜６の時には　正常の向きの時はこれでいいのです!!
                for (num in 6 downTo 1) { // 6 5 4 3 2 1
                    var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                    if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                        card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                        break // 抜ける
                    }
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
            } else {  // 13以外で 8 ~ 12のカードを出した時
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
                    // 置いたカードが 8 ~ 12のカードで、
                    // 正常の向きの時はこれでいいのです
                    for (num in 8..13) {  // 8,9,10,11,12,13
                        var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                        if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                            card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                            break // 抜ける
                        }
                    }
                }
            }
        return _deepPossibleCardList  // 最後書き換えたのを返す 呼び出し元で必要です
    }
}