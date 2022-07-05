package to.msn.wings.sevengame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class StartFragment : Fragment() {

    var placeableList = mutableListOf<String>("♠6", "♠8", "♥6", "♥8",  "♦6", "♦8", "♣6", "♣8")

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        return view  // フラグメントでは最後必ず viewを返す
    }


}