package ui.posting.dailyboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentPostingBinding
import ui.base.BaseFragment
import ui.main.MainActivity

class PostingFragment : BaseFragment<FragmentPostingBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 게시글 작성이 완료된 시점에 일상게시글 화면을 보여준다
        val rootActivity = activity as MainActivity
        rootActivity.showBoardFragmnet()

        val intent = Intent(
            activity,
            ChooseMediaActivity::class.java
        )
        startActivity(intent)
    }
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPostingBinding = FragmentPostingBinding.inflate(inflater)


}