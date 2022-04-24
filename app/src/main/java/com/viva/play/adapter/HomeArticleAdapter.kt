package com.viva.play.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.databinding.ItemHomeArticleBinding
import com.viva.play.databinding.ListFooterBinding
import com.viva.play.db.entity.PoArticleEntity
import com.viva.play.db.entity.PoBannerEntity
import com.viva.play.utils.*
import com.viva.play.views.CollectView
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import com.youth.banner.loader.ImageLoaderInterface

/**
 * @author 李雄厚
 *
 *
 */
class HomeArticleAdapter(
    private val context: Context,
    private val retry: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_FOOTER = -1
        private const val TYPE_BANNER = 1

        /**
         * 加载失败
         */
        const val LOAD_FAILED = 10

        /**
         * 加载中
         */
        const val LOADING = 20
    }

    private var isShowFooter = true
    private var type = LOADING
    private val bannerList = mutableListOf<PoBannerEntity>()
    val data: ArrayList<PoArticleEntity> = ArrayList()

    var itemOnClick: ((PoArticleEntity, Int) -> Unit)? = null
    var itemLongClick: ((PoArticleEntity, Int) -> Boolean)? = null
    var collectClick: ((CollectView, PoArticleEntity) -> Unit)? = null

    var banner: Banner? = null

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_BANNER
            isShowFooter && position + 1 == itemCount -> TYPE_FOOTER
            else -> super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FOOTER -> FooterViewHolder(
                ListFooterBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
            TYPE_BANNER -> {
                initBanner(context)
                val binding = BannerViewHolder(banner!!)
                binding
            }
            else -> ViewHolder(
                ItemHomeArticleBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val data = this.data[position]
                holder.binding.apply {
                    this.data = data
                    cvCollect.onClick = {
                        collectClick?.invoke(it, data)
                    }
                }

                holder.binding.executePendingBindings()
                holder.itemView.setOnClickListener {
                    itemOnClick?.invoke(data, position)
                }
                holder.itemView.setOnLongClickListener {
                    itemLongClick?.invoke(data, position) ?: true
                }
            }
            is FooterViewHolder -> {
                if (isShowFooter) {
                    if (type == LOADING) {
                        holder.binding.footerMessage.text = "正在加载..."
                        holder.binding.footerProgress.isVisible = true
                    } else {
                        holder.binding.footerMessage.text = "加载失败点击重试~"
                        holder.binding.footerProgress.isVisible = false
                        holder.binding.root.setOnClickListener {
                            retry.invoke()
                        }
                    }
                }
            }
            is BannerViewHolder -> {
                val data = this.data[position]
                val bannerData = data.banner
                if (!bannerData.isNullOrEmpty()) {
                    if (!bannerList.containsAll(bannerData)) {
                        bannerList.addAll(bannerData)
                        holder.banner.setImages(bannerData)
                        holder.banner.setBannerTitles(bannerData.map { banner -> banner.title })
                        holder.banner.start()
                    }
                }
                holder.banner.setOnBannerListener {
                    holder.banner.isAutoPlay(false)
                    holder.banner.stopAutoPlay()
                }
            }
        }
    }

    override fun getItemCount(): Int =
        if (isShowFooter && data.size > 0) data.size + 1 else data.size

    inner class BannerViewHolder(val banner: Banner) :
        RecyclerView.ViewHolder(banner)

    inner class ViewHolder(val binding: ItemHomeArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class FooterViewHolder(val binding: ListFooterBinding) :
        RecyclerView.ViewHolder(binding.root)


    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(data: List<PoArticleEntity>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<PoArticleEntity>) {
        this.data.addAll(data)
        notifyItemRangeInserted(itemCount - 1, data.size)
    }

    fun setShowFooter(flag: Boolean) {
        this.isShowFooter = flag
        notifyItemChanged(itemCount - 1)
    }

    fun setFooterType(type: Int) {
        this.type = type
        notifyItemChanged(itemCount - 1)
    }


    private fun initBanner(context: Context) {
        banner = Banner(context).apply {
            val screenWidth: Int = screenWidth
            val screenHeight: Int = screenHeight
            val height = (screenWidth.coerceAtMost(screenHeight) * (9f / 16f)).toInt()
            layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
            setImageLoader(object : ImageLoaderInterface<ImageView> {
                override fun displayImage(
                    context: Context?,
                    data: Any?,
                    imageView: ImageView?
                ) {
                    imageView?.banner((data as PoBannerEntity).imagePath)
                }

                override fun createImageView(context: Context?): ImageView {
                    val imageView = ImageView(context)
                    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageView.layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    return imageView
                }

            })
            setIndicatorGravity(BannerConfig.CENTER)
            setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
            setBannerAnimation(Transformer.Default)
            startAutoPlay()
            setDelayTime(5000)
        }
    }
}