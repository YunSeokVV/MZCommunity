package view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mzcommunity.databinding.FragmentVersusBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.LoginedUser
import view.activity.PostingVersusActivity
import viewmodel.VersusFragmnetViewModel

@AndroidEntryPoint
class VersusFragment(private val loginedUserProfile : LoginedUser) : Fragment() {
    private lateinit var binding: FragmentVersusBinding
    private val viewModel by viewModels<VersusFragmnetViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVersusBinding.inflate(inflater)
        binding.posting.setOnClickListener {
            val intent = Intent(
                activity,
                PostingVersusActivity::class.java
            )
            startActivity(intent)
        }


        viewModel.versusBoard.observe(requireActivity(), Observer {
            binding.choice1Text.text = it.opinion1
            binding.choice2Text.text = it.opinion2

            Glide.with(this).load(it.writerUri)
                .into(binding.userProfileImg)

            binding.writeName.text = it.writerName
            binding.title.text = it.boardTitle
            binding.choosenChoice1.text = it.opinion1
            binding.choosenChoice1Percent.text = viewModel.opinion1Percent.toString() + "%"
            binding.choosenChoice1Count.text = it.opinion1Count.toString()

            binding.choosenChoice2.text = it.opinion2
            binding.choosenChoice2Percent.text = viewModel.opinion2Percent.toString() + "%"
            binding.choosenChoice2Count.text = it.opinion2Count.toString()

        })

        binding.choice1.setOnClickListener {
            vote(true)
        }

        binding.choice2.setOnClickListener {
            vote(false)
        }

        binding.comment.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(viewModel.boardUID, "versusBoardComment","versusBoardNestedComment", "versusBoardComment", loginedUserProfile)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        viewModel.voteComplte.observe(requireActivity(), Observer {
            Logger.v(it.toString())
            showNextVote()
        })

        return binding.root
    }

    fun vote(opinionOne: Boolean) {
        viewModel.voteOpinion(opinionOne)
        val visibleView = if (opinionOne) binding.choice1Checked else binding.choice2Checked
        visibleView.visibility = View.VISIBLE

        if (opinionOne)
            binding.choosenChoice1Count.text =
                viewModel.addVoteCount(binding.choosenChoice1Count.text.toString().toInt())
                    .toString()
        else
            binding.choosenChoice2Count.text =
                viewModel.addVoteCount(binding.choosenChoice2Count.text.toString().toInt())
                    .toString()

        binding.choosenChoice1.visibility = View.VISIBLE
        binding.choosenChoice1Percent.visibility = View.VISIBLE
        binding.choosenChoice1Count.visibility = View.VISIBLE
        binding.choosenChoice2.visibility = View.VISIBLE
        binding.choosenChoice2Percent.visibility = View.VISIBLE
        binding.choosenChoice2Count.visibility = View.VISIBLE
        binding.choice1Text.visibility = View.GONE
        binding.choice2Text.visibility = View.GONE
    }

    fun initalizeVoteView(){
        binding.choice1Checked.visibility = View.GONE
        binding.choice2Checked.visibility = View.GONE
        binding.choosenChoice1.visibility = View.GONE
        binding.choosenChoice1Percent.visibility = View.GONE
        binding.choosenChoice1Count.visibility = View.GONE
        binding.choosenChoice2.visibility = View.GONE
        binding.choosenChoice2Percent.visibility = View.GONE
        binding.choosenChoice2Count.visibility = View.GONE
        binding.choice1Text.visibility = View.VISIBLE
        binding.choice2Text.visibility = View.VISIBLE
    }


    fun showNextVote() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("투표 완료!").setMessage("확인을 눌러서 다음 질문으로 넘어가세요")
            .setNegativeButton("취소") { dialog, which ->

            }.setPositiveButton("확인") { dialog, which ->
            initalizeVoteView()
            viewModel.getRandomVersusBoard()
        }
        builder.show()
    }

}