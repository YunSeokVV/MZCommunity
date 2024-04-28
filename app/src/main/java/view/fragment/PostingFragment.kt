package view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mzcommunity.R
import view.activity.ChooseMediaActivity
import view.activity.MainActivity

class PostingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 게시글 작성이 완료된 시점에 일상게시글 화면을 보여준다
        val rootActivity = activity as MainActivity
        rootActivity.showBoardFragmnet()

        val intent = Intent(
            activity,
            ChooseMediaActivity::class.java
        )
        startActivity(intent)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posting, container, false)
    }


}