package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.viva.play.base.BaseAdapter
import com.viva.play.databinding.ItemSearchHistoryBinding
import com.viva.play.db.entity.PoSearchHistoryEntity

/**
 * @author 李雄厚
 *
 *
 */
class HistoryAdapter(
    private val context: Context,
    data: MutableList<PoSearchHistoryEntity>
) : BaseAdapter<ItemSearchHistoryBinding, PoSearchHistoryEntity>(data) {

    var itemLongClick: ((data: PoSearchHistoryEntity) -> Unit)? = null
    var itemChildClick: ((data: PoSearchHistoryEntity, position: Int) -> Unit)? = null
    var mRemoveMode = false
    var mRemoveModeChanging = false

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemSearchHistoryBinding =
        ItemSearchHistoryBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun bind(
        binding: ItemSearchHistoryBinding,
        data: PoSearchHistoryEntity,
        position: Int
    ) {
        binding.tvKey.text = data.key
        if (!mRemoveModeChanging) {
            binding.ivRemove.isVisible = mRemoveMode
        } else {
            if (mRemoveMode) {
                val scaleAnimation = ScaleAnimation(
                    0F, 1F, 0F, 1F,
                    Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5F
                )
                scaleAnimation.duration = 300
                scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.ivRemove.isVisible = true
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        mRemoveModeChanging = false
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                binding.ivRemove.startAnimation(scaleAnimation)
            } else {
                val scaleAnimation = ScaleAnimation(
                    1F, 0F, 1F, 0F,
                    Animation.RELATIVE_TO_SELF, 0.5F,
                    Animation.RELATIVE_TO_SELF, 0.5F
                )
                scaleAnimation.duration = 300
                scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        mRemoveModeChanging = false
                        binding.ivRemove.isInvisible = true
                    }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                })
                binding.ivRemove.startAnimation(scaleAnimation)
            }
        }
        binding.tvKey.setOnLongClickListener {
            itemLongClick?.invoke(data)
            true
        }
        binding.ivRemove.setOnClickListener {
            itemChildClick?.invoke(data, position)
        }
    }
}