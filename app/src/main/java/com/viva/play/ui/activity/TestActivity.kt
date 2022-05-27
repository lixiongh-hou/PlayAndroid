package com.viva.play.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityLiBinding
import com.viva.play.utils.bind.binding
import com.viva.play.utils.bindDivider
import com.viva.play.views.draggable.QMUIContinuousNestedTopAreaBehavior
import kotlinx.android.synthetic.main.item_home_article.*

class TestActivity : BaseActivity() {

    private val binding by binding<ActivityLiBinding>()
//    private lateinit var adapter: Adapter

    override fun initView(savedInstanceState: Bundle?) {
//        binding.mCoordinatorLayout.setDraggableScrollBarEnabled(true)
//        binding.mRecyclerView.setBackgroundColor(Color.LTGRAY)
//        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
//        val topLp = CoordinatorLayout.LayoutParams(
//            matchParent, ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        topLp.behavior = QMUIContinuousNestedTopAreaBehavior(this)
//        binding.mCoordinatorLayout.setTopAreaView(binding.mNestedWebView, topLp)
        binding.mNestedWebView.x5WebViewExtension.setScrollBarDefaultDelayBeforeFade(3000)
        binding.mNestedWebView.loadUrl("https://www.jianshu.com/p/3a372af38103")

//        adapter = Adapter(
//            listOf(
//                "数据1",
//                "数据2",
//                "数据2",
//                "数据3",
//                "数据4",
//                "数据5",
//                "数据6",
//                "数据7",
//                "数据8",
//                "数据9",
//                "数据10",
//                "数据1",
//                "数据2",
//                "数据2",
//                "数据3",
//                "数据4",
//                "数据5",
//                "数据6",
//                "数据7",
//                "数据8",
//                "数据9",
//                "数据10",
//                "数据1",
//                "数据2",
//                "数据2",
//                "数据3",
//                "数据4",
//                "数据5",
//                "数据6",
//                "数据7",
//                "数据8",
//                "数据9",
//                "数据10",
//                "数据1",
//                "数据2",
//                "数据2",
//                "数据3",
//                "数据4",
//                "数据5",
//                "数据6",
//                "数据7",
//                "数据8",
//                "数据9",
//                "数据10",
//                "数据1",
//                "数据2",
//                "数据2",
//                "数据3",
//                "数据4",
//                "数据5",
//                "数据6",
//                "数据7",
//                "数据8",
//                "数据9",
//                "数据10",
//            )
//        )
//        binding.mRecyclerView.bindDivider()
//        binding.mRecyclerView.adapter = adapter
//        binding.mCoordinatorLayout.postDelayed({
//        }, 300)


    }

    override fun onDestroy() {
        super.onDestroy()
//        binding.mCoordinatorLayout.removeView(binding.mNestedWebView)
        binding.mNestedWebView.destroy()
    }


//    inner class Adapter(private val data: List<String>) :
//        RecyclerView.Adapter<Adapter.ViewHolder>() {
//
//        inner class ViewHolder(private val textView: View) :
//            RecyclerView.ViewHolder(textView) {
//            fun bind(str: String) {
//                (textView as AppCompatTextView).text = str
//            }
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            return ViewHolder(layoutInflater.inflate(R.layout.item_li, parent, false))
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.bind(data[position])
//        }
//
//        override fun getItemCount(): Int = data.size
//    }

}