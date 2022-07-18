package to.msn.wings.sevengame

/**
 * possible属性を操作し変更をして 次に置くことができるカードを判断するためのクラス.
 */
class Judgement( poList: ArrayList<PossibleCard> ){

    // フィールド
    val _game = Game()
    // getSubListを使ってディープコピーしています.
    val _deepPossibleCardList: ArrayList<PossibleCard> = _game.getSubList(poList) as ArrayList<PossibleCard>

    /**
     * 小さいカード(1..6)を置いた時.
     * 戻り値は forで属性を書き換えた後の _deepPossibleCardList です
     * ただ属性を書き換えてるだけなので forを使って大丈夫です (イテレータを使わないでやってもエラーにならない)
     */
    fun methodSmall( pTagStr: String): ArrayList<PossibleCard> {
         val putCardNum = pTagStr.substring(1).toInt()  // 置いたカードの数字
         // 1 のカードが置かれた時に、どちらの方向の場合に置かれたのか によって操作が異なります.分岐しています.
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
             for (num in 1..6) {
                 var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                 if (card != null && card.placed == true) {
                     count++
                 }
             }
             if (count == 6) {
                 // カウント6だったら、6 5 4 3 2 1 が　全部 trueになったから  例えば 4置いた時に カウント 6 だったら 今度 13からの向きになります
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
             } else if (count <= 5) {

                 var countBig = 0
                 for (num in 8..13) {
                     var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                     if (card != null && card.placed == true) {
                         countBig++
                     }
                 }
                 if (countBig == 6) {
                     // 逆向きなんです
                     for (num in 1..6) { // 1 2 3 4 5 6 と置かれるべきなのです
                         var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                         if (card != null && card.placed == false) { // もし、1..6まで　リストに まだ置いてないカードが見つかった時点で
                             card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                             break // 抜ける
                         }
                     }
                 } else {  // 正常向き
                     for (num in 6 downTo 1) { // 6 5 4 3 2 1 と置かれるべきなのです
                         var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                         if (card != null && card.placed == false) { // もし、6..1まで　リストに まだ置いてないカードが見つかった時点で
                             card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                             break // 抜ける
                         }
                     }
                 }
             }
         }
         return _deepPossibleCardList  // 最後書き換えたのを返す 呼び出し元で必要です
     }

    /**
     * 大きいカード(8..13)を置いた時
     * 戻り値は forで属性を書き換えた後の _deepPossibleCardList です
     * ただ属性を書き換えてるだけなので forを使って大丈夫です (イテレータを使わないでやってもエラーにならない)
     */
    fun methodBig(pTagStr: String): ArrayList<PossibleCard> {
             val putCardNum = pTagStr.substring(1).toInt()
             //  13 のカードが置かれた時に、どちらの方向の場合に置かれたのか によって操作が異なります.分岐しています.
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
                 for (num in 8..13) {
                     var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                     if (card != null && card.placed == true) {
                         count++
                     }
                 }

                 if (count == 6) {
                     // カウント6だったら、8 9 10 11 12 13   が　全部 trueになったから  例えば 9置いた時に カウント 6 だったら 今度 1からの向きになります
                     //  1 2 3 4 5 6 と置けるようにする
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

                 } else if (count <= 5) {
                     // ここ付け足しました
                     var countSmall = 0
                     for (num in 1..6) {
                         var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                         if (card != null && card.placed == true) {
                             countSmall++
                         }
                     }
                     if (countSmall == 6) {
                         // 逆向きなんです 13 12 11 10 9 8 と置かれるべきなのです
                         for (num in 13 downTo 8) {
                             var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                             if (card != null && card.placed == false) { // もし、13から8まで　リストに まだ置いてないカードが見つかった時点で
                                 card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                                 break // 抜ける
                             }
                         }

                     } else {
                         // まだ正常の向き
                         for (num in 8..13) {  // 8,9,10,11,12,13 と置かれるべきなのです
                             var card = _game.getPossibleCard(_deepPossibleCardList, pTagStr, num)
                             if (card != null && card.placed == false) { // もし、8 ~ 13まで　リストに まだ置いてないカードが見つかった時点で
                                 card.possible = true // 可能に trueを入れる 見つかった時点ですぐbreak
                                 break // 抜ける
                             }
                         }
                     }
                 }

             }
             return _deepPossibleCardList  // 最後書き換えたのを返す 呼び出し元で必要です
         }

     }