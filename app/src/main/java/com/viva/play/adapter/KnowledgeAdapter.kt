package com.viva.play.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.flexbox.FlexboxLayout
import com.viva.play.R
import com.viva.play.base.BaseAdapter
import com.viva.play.base.BaseViewHolder
import com.viva.play.databinding.ItemKnowledgeBinding
import com.viva.play.db.entity.PoChapterChildrenEntity
import com.viva.play.ui.vo.VoChapterEntity
import com.viva.play.utils.formatHtml
import java.util.*

/**
 * @author 李雄厚
 *
 *
 */
class KnowledgeAdapter(private val context: Context, data: MutableList<VoChapterEntity>) :
    BaseAdapter<ItemKnowledgeBinding, VoChapterEntity>(data) {

    private var mInflater: LayoutInflater? = null
    private val mFlexItemTextViewCaches: Queue<AppCompatTextView> = LinkedList()

    var itemClickListener: ((PoChapterChildrenEntity, VoChapterEntity, Int) -> Unit)? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemKnowledgeBinding =
        ItemKnowledgeBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun bind(binding: ItemKnowledgeBinding, data: VoChapterEntity, position: Int) {
        binding.tvName.text = data.chapter.name
        data.children.forEachIndexed { index, children ->
            val child = createOrGetCacheFlexItemTextView(binding.fbl)
            child.text = children.name.formatHtml()
            child.setOnClickListener {
                itemClickListener?.invoke(children, data, index)
            }
            binding.fbl.addView(child)
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder<ItemKnowledgeBinding>) {
        super.onViewRecycled(holder)
        for (i in 0 until holder.binding.fbl.childCount) {
            mFlexItemTextViewCaches.offer(holder.binding.fbl.getChildAt(i) as AppCompatTextView)
        }
        holder.binding.fbl.removeAllViews()
    }

    private fun createOrGetCacheFlexItemTextView(fbl: FlexboxLayout): AppCompatTextView {
        val tv = mFlexItemTextViewCaches.poll()
        if (tv != null) {
            return tv
        }
        return createFlexItemTextView(fbl)
    }

    private fun createFlexItemTextView(fbl: FlexboxLayout): AppCompatTextView {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(fbl.context)
        }
        return mInflater?.inflate(R.layout.item_knowledge_child, fbl, false) as AppCompatTextView

    }
}