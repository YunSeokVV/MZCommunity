package ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import data.model.LoginedUser
import ui.comment.BottomSheetFragment

abstract class BaseFragment<T : ViewBinding> : Fragment() {

    private var _binding : T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(inflater, container)

        return binding.root
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : T

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getUserBundle(arguments: Bundle): LoginedUser =
        arguments?.getSerializable("loginedUserProfile") as LoginedUser

    fun showComment(boardUID : String){
        var bundle = Bundle()
        bundle.putString("boardUID", boardUID)
        bundle.putString("collectionName", "dailyBoardComment")
        bundle.putString("nestedCommentName", "dailyBoardNestedComment")
        bundle.putString("commentName", "dailyBoardComment")
        bundle.putSerializable("loginedUserProfile", arguments?.let { getUserBundle(it) })
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

}