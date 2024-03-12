package view

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mzcommunity.R
import com.example.mzcommunity.databinding.ActivityPostingBinding
import com.orhanobut.logger.Logger


class PostingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting)
        val binding = ActivityPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.exit.setOnClickListener {
            finish()
        }

        binding.dropDownMenu.setOnClickListener {
            binding.dropDownMenu.setBackgroundResource(R.drawable.baseline_arrow_drop_up_24)
            val popupMenu = PopupMenu(this, it)
            menuInflater.inflate(R.menu.post_board_context_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.dailyBoardPosting) {
                    binding.boardSort.text = getString(R.string.daily_board)
                } else if (menuItem.itemId == R.id.balanceBoardPosting) {
                    binding.boardSort.text = getString(R.string.balance_board)
                }

                binding.dropDownMenu.setBackgroundResource(R.drawable.baseline_arrow_drop_down_24)
                false
            }
            popupMenu.setOnDismissListener {
                binding.dropDownMenu.setBackgroundResource(R.drawable.baseline_arrow_drop_down_24)
            }
            popupMenu.show()
        }

    }


}