package ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import data.model.LoginedUser
import ui.comment.BottomSheetFragment

open class BaseFragment : Fragment() {
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