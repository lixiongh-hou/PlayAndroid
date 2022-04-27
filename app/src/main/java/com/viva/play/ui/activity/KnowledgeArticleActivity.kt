package com.viva.play.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import com.viva.play.R
import com.viva.play.adapter.vp2.FixedFragmentPagerAdapter
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityKnowledgeArticleBinding
import com.viva.play.service.entity.Tag
import com.viva.play.ui.fragment.KnowledgeArticleFragment
import com.viva.play.ui.model.KnowledgeModel
import com.viva.play.ui.vo.VoChapterEntity
import com.viva.play.utils.bind.binding
import com.viva.play.utils.createUrlByPath
import com.viva.play.utils.getThemeColor
import com.viva.play.utils.toError
import dagger.hilt.android.AndroidEntryPoint
import github.xuqk.kdtablayout.KDTabAdapter
import github.xuqk.kdtablayout.KDTabLayout
import github.xuqk.kdtablayout.widget.KDTab
import github.xuqk.kdtablayout.widget.KDTabIndicator
import github.xuqk.kdtablayout.widget.tab.KDColorMorphingTextTab

@AndroidEntryPoint
class KnowledgeArticleActivity : BaseActivity() {

    companion object {
        private const val SUPER_CHAPTER_ID = "superChapterId"
        private const val SUPER_CHAPTER_NAME = "superChapterName"
        private const val CHAPTER_ID = "chapterId"

        fun start(context: Context, superChapterId: Int, superChapterName: String, chapterId: Int) {
            val intent = Intent(context, KnowledgeArticleActivity::class.java)
            intent.putExtra(SUPER_CHAPTER_ID, superChapterId)
            intent.putExtra(SUPER_CHAPTER_NAME, superChapterName)
            intent.putExtra(CHAPTER_ID, chapterId)
            context.startActivity(intent)
        }

        fun start(context: Context, tag: Tag?) {
            if (tag == null) return
            val url = tag.url
            if (url.isEmpty()) return
            val uri = Uri.parse(url.createUrlByPath())
            try {
                var cid = uri.getQueryParameter("cid")
                if (cid.isNullOrEmpty()) {
                    val paths = uri.pathSegments
                    if (paths != null && paths.size >= 3) {
                        cid = paths[2]
                    }
                }
                var chapterId = 0
                if (cid != null) {
                    chapterId = cid.toInt()
                }
                if (chapterId > 0) {
                    start(context, 0, "", chapterId)
                }
            } catch (e: Exception) {

            }
        }
    }

    private val binding by binding<ActivityKnowledgeArticleBinding>()
    private val model by viewModels<KnowledgeModel>()

    private val superChapterId by lazy {
        intent.getIntExtra(SUPER_CHAPTER_ID, -1)
    }
    private val superChapterName by lazy {
        intent.getStringExtra(SUPER_CHAPTER_NAME) ?: ""
    }
    private val chapterId by lazy {
        intent.getIntExtra(CHAPTER_ID, -1)
    }

    private lateinit var mAdapter: FixedFragmentPagerAdapter

    override fun initView(savedInstanceState: Bundle?) {
        if (superChapterId <= 0 && chapterId <= 0) {
            binding.msv.toError()
        }
        binding.abc.titleTextView.text = superChapterName
        model.getKnowledgeList()
        model.knowledgeList.observe(this) {
            var superChapterBean: VoChapterEntity? = null
            for (bean in it) {
                if (bean.chapter.id == superChapterId) {
                    superChapterBean = bean
                    break
                }
            }
            var currPos = 0
            if (superChapterBean == null) {
                for (scb in it) {
                    val chapters = scb.children
                    for (i in chapters.indices) {
                        val cb = chapters[i]
                        if (cb.id == chapterId) {
                            superChapterBean = scb
                            currPos = i
                            break
                        }
                    }
                }
            } else {
                val chapters = superChapterBean.children
                for (i in chapters.indices) {
                    val cb = chapters[i]
                    if (cb.id == chapterId) {
                        currPos = i
                        break
                    }
                }
            }
            if (superChapterBean != null) {
                initVP(superChapterBean, currPos)
            } else {
                binding.msv.toError()
            }
        }
    }

    private fun initVP(superChapterBean: VoChapterEntity, currPos: Int) {
        binding.abc.titleTextView.text = superChapterBean.chapter.name
        binding.mi.tabMode = KDTabLayout.TAB_MODE_SCROLLABLE
        mAdapter = FixedFragmentPagerAdapter(
            this, superChapterBean.children.mapIndexed { index, entity ->
                KnowledgeArticleFragment.create(index, entity.id)
            }
        )
        binding.vp.adapter = mAdapter
        binding.mi.contentAdapter = object : KDTabAdapter() {
            val tabName = superChapterBean.children.map { it.name }
            override fun createTab(position: Int): KDTab {
                return KDColorMorphingTextTab(
                    this@KnowledgeArticleActivity,
                    tabName[position]
                ).apply {
                    horizontalPadding = 8F
                    selectedTextSize = 15F
                    normalTextSize = 15F
                    selectedTextColor =
                        this@KnowledgeArticleActivity.getThemeColor(R.attr.colorOnMainOrSurface)
                    normalTextColor =
                        this@KnowledgeArticleActivity.getThemeColor(R.attr.colorOnMainOrSurfaceAlpha)
                    setOnClickListener {
                        binding.vp.currentItem = position
                    }
                }
            }

            override fun createIndicator(): KDTabIndicator? {
                return null
            }

            override fun getTabCount(): Int {
                return tabName.size
            }
        }
        // 与ViewPager2联动
        binding.mi.setViewPager2(binding.vp)
        binding.vp.currentItem = currPos
    }
}