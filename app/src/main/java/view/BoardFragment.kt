package view

import adapter.DailyBoardAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentBoardBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import viewModel.BoardFramgnetViewModel

@AndroidEntryPoint
class BoardFragment : Fragment() {
    private val viewModel by viewModels<BoardFramgnetViewModel>()
    private lateinit var binding: FragmentBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater)

        binding.dailyBoards.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)


        // 일단 서버에서 데이터를 갖고 오세요
        viewModel.document.observe(requireActivity(), Observer {

            val dailyBoardAdapter = DailyBoardAdapter(
                it,
                viewModel.getUserUploadFilesUri(),
                object : DailyBoardAdapter.IncreaseLike {
                    override fun increaseLikse(likeNumber: Int) {
                        Logger.v("increaseLike called")
                    }

                },
                object : DailyBoardAdapter.DecreaseLike {
                    override fun decreaseLike(likeNumber: Int) {
                        Logger.v("decreaseLikse called")
                    }

                })
            binding.dailyBoards.adapter = dailyBoardAdapter

            dailyBoardAdapter.submitList(it)
            dailyBoardAdapter.notifyDataSetChanged()
        })

        return binding.root
    }

}