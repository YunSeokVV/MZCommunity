package ui.board.versusboard

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.FragmentVersusBinding
import dagger.hilt.android.AndroidEntryPoint
import data.model.LoginedUser
import ui.base.BaseFragment
import ui.posting.versusboard.PostingVersusActivity
import ui.comment.BottomSheetFragment

@AndroidEntryPoint
class VersusFragment() : BaseFragment() {
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
                        binding.choice1.setBackgroundColor(requireActivity().getColor(R.color.complementary_pink))
                    }
                    // 손을 뗀 시점
                    MotionEvent.ACTION_UP -> {
                        binding.choice1.setBackgroundColor(requireActivity().getColor(R.color.pink))
                        vote(true)
                        setPercent()
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
                        binding.choice2.setBackgroundColor(requireActivity().getColor(R.color.complementary_sky_blue))
                    }
                    // 손을 뗀 시점
                    MotionEvent.ACTION_UP -> {
                        binding.choice2.setBackgroundColor(requireActivity().getColor(R.color.sky_blue))
                        vote(false)
                        setPercent()
                    }
                }
                return true
            }
        })

        viewModel.versusBoard.observe(requireActivity(), Observer {
            showAgenda(true)
            binding.choice1Text.text = it.opinion1
            binding.choice2Text.text = it.opinion2

            Glide.with(this).load(Uri.parse(it.writerUri))
                .into(binding.userProfileImg)

            binding.writeName.text = it.writerName
            binding.title.text = it.boardTitle
            binding.choosenChoice1.text = it.opinion1
            binding.choosenChoice1Count.text = it.opinion1Count.toString()
            binding.choosenChoice2Count.text = it.opinion2Count.toString()
            binding.choosenChoice2.text = it.opinion2
        })

        viewModel.errorValue.observe(requireActivity(), Observer {
            showAgenda(false)
        })

        binding.comment.setOnClickListener {

            showComment(viewModel.boardUID)
        }

        viewModel.voteComplte.observe(requireActivity(), Observer {
            showNextVote()
        })

        return binding.root
    }

    fun vote(isFirstOpinion: Boolean) {
        viewModel.voteOpinion(isFirstOpinion)
        val visibleView = if (isFirstOpinion) binding.choice1Checked else binding.choice2Checked
        visibleView.visibility = View.VISIBLE

        if (isFirstOpinion)
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
            binding.choice1.setBackgroundColor(requireActivity().getColor(R.color.pink))
            binding.choice2.setBackgroundColor(requireActivity().getColor(R.color.sky_blue))
            binding.writeNameLayout.setBackgroundColor(requireActivity().getColor(R.color.white))
            binding.titleLayout.setBackgroundColor(requireActivity().getColor(R.color.white))
            binding.writeName.setTextColor(requireActivity().getColor(R.color.black))
            binding.title.setTextColor(requireActivity().getColor(R.color.black))
            binding.choice1Text.visibility = View.VISIBLE
            binding.comment.visibility = View.VISIBLE
            binding.posting.visibility = View.VISIBLE
            binding.choice2Text.visibility = View.VISIBLE
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
            binding.choice1Checked.visibility = View.GONE
            binding.choice2Checked.visibility = View.GONE
        }

    }

    private fun setPercent() {
        val firstPercent: Int = viewModel.calculatePercent(
            binding.choosenChoice1Count.text.toString().toInt(),
            binding.choosenChoice2Count.text.toString().toInt()
        )
        val secondPercent: Int = viewModel.calculatePercent(
            binding.choosenChoice2Count.text.toString().toInt(),
            binding.choosenChoice1Count.text.toString().toInt()
        )
        binding.choosenChoice1Percent.text = "$firstPercent%"
        binding.choosenChoice2Percent.text = "$secondPercent%"
    }
}