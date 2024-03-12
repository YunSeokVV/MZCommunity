package view

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mzcommunity.R

class PostingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fileType: Array<String> = arrayOf("동영상", "사진")
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("업로드 형식 선택")
            .setItems(fileType, DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    // 동영상 선택
                    0 -> {


                    }

                    // 사진 선택
                    1 -> {
                        val intent = Intent(
                            activity,
                            ChooseMediaActivity::class.java
                        )
                        startActivity(intent)
                    }
                }

            })
        builder.show()


//        // 기존 postingActivity로 이동
//        val intent = Intent(activity, PostingActivity::class.java)
//        startActivity(intent)
//        (activity as MainActivity?)?.showBoardFragmnet()


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posting, container, false)
    }


}