package view.fragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentVersusBinding
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import model.LoginedUser
import util.Util
import view.activity.PostingVersusActivity
import viewmodel.VersusFragmnetViewModel

@AndroidEntryPoint
class VersusFragment(private val loginedUserProfile: LoginedUser) : Fragment() {
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

        binding.addPost.setOnClickListener {
            val intent = Intent(
                activity,
                PostingVersusActivity::class.java
            )
            startActivity(intent)
        }


        binding.choice1.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    // 손을 누른 시점
                    MotionEvent.ACTION_DOWN -> {
                        binding.choice1.setBackgroundColor(Util.getResourceColor(requireActivity(), R.color.complementary_pink))
                    }
                    // 손을 뗀 시점
                    MotionEvent.ACTION_UP -> {
                        vote(true)
                        binding.choice1.setBackgroundColor(Util.getResourceColor(requireActivity(), R.color.pink))
                    }
                }
                return true
            }
        })


        binding.choice2.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    // 손을 누른 시점
                    MotionEvent.ACTION_DOWN -> {
                        binding.choice1.setBackgroundColor(Util.getResourceColor(requireActivity(), R.color.complementary_sky_blue))
                    }
                    // 손을 뗀 시점
                    MotionEvent.ACTION_UP -> {
                        vote(true)
                        binding.choice1.setBackgroundColor(Util.getResourceColor(requireActivity(), R.color.sky_blue))
                    }
                }
                return true
            }
        })

        viewModel.versusBoard.observe(requireActivity(), Observer {
            showAgenda(true)
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

        viewModel.errorValue.observe(requireActivity(), Observer {
            showAgenda(false)
        })

        binding.comment.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment(
                viewModel.boardUID,
                "versusBoardComment",
                "versusBoardNestedComment",
                "versusBoardComment",
                loginedUserProfile
            )
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        viewModel.voteComplte.observe(requireActivity(), Observer {
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

        showAfterVoteView(true)
    }

    private fun showNextVote() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("투표 완료!").setMessage("확인을 눌러서 다음 질문으로 넘어가세요")
            .setNegativeButton("취소") { dialog, which ->

            }.setPositiveButton("확인") { dialog, which ->
                viewModel.getRandomVersusBoard()
            }
        builder.show()
    }

    private fun showAgenda(isShowAgenda: Boolean) {
        if (isShowAgenda) {
            binding.choice1Text.visibility = View.VISIBLE
            binding.choice2Text.visibility = View.VISIBLE
            binding.userProfileImg.visibility = View.VISIBLE

            binding.writeName.visibility = View.VISIBLE
            binding.title.visibility = View.VISIBLE
            binding.choosenChoice1.visibility = View.VISIBLE
            binding.choosenChoice1Percent.visibility = View.VISIBLE
            binding.choosenChoice1Count.visibility = View.VISIBLE

            binding.choosenChoice2.visibility = View.VISIBLE
            binding.choosenChoice2Percent.visibility = View.VISIBLE
            binding.choosenChoice2Count.visibility = View.VISIBLE
            binding.nothing.visibility = View.GONE
            binding.addPost.visibility = View.GONE

            showAfterVoteView(false)
        } else {
            binding.choice1Text.visibility = View.GONE
            binding.choice2Text.visibility = View.GONE
            binding.choice1.visibility = View.GONE
            binding.choice2.visibility = View.GONE
            binding.posting.visibility = View.GONE
            binding.userProfileImg.visibility = View.GONE

            binding.writeName.visibility = View.GONE
            binding.title.visibility = View.GONE
            binding.choosenChoice1.visibility = View.GONE
            binding.choosenChoice1Percent.visibility = View.GONE
            binding.choosenChoice1Count.visibility = View.GONE

            binding.choosenChoice2.visibility = View.GONE
            binding.choosenChoice2Percent.visibility = View.GONE
            binding.choosenChoice2Count.visibility = View.GONE
            binding.nothing.visibility = View.VISIBLE
            binding.addPost.visibility = View.VISIBLE
        }
    }

    //투표결과 이후에 보여져야할 뷰들이다. 투표가 아직 이루어지지 않았다면 false로 처리해야 한다.
    private fun showAfterVoteView(showView: Boolean) {
        if (showView) {
            binding.choosenChoice1.visibility = View.VISIBLE
            binding.choosenChoice1Percent.visibility = View.VISIBLE
            binding.choosenChoice1Count.visibility = View.VISIBLE
            binding.choosenChoice2.visibility = View.VISIBLE
            binding.choosenChoice2Percent.visibility = View.VISIBLE
            binding.choosenChoice2Count.visibility = View.VISIBLE
            binding.choice1Text.visibility = View.GONE
            binding.choice2Text.visibility = View.GONE
        } else {
            binding.choosenChoice1.visibility = View.GONE
            binding.choosenChoice1Percent.visibility = View.GONE
            binding.choosenChoice1Count.visibility = View.GONE
            binding.choosenChoice2.visibility = View.GONE
            binding.choosenChoice2Percent.visibility = View.GONE
            binding.choosenChoice2Count.visibility = View.GONE
            binding.choice1Text.visibility = View.VISIBLE
            binding.choice2Text.visibility = View.VISIBLE
        }

    }

}