package com.viva.play.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import com.viva.play.App
import com.viva.play.R
import com.viva.play.base.BaseActivity
import com.viva.play.databinding.ActivityAuthBinding
import com.viva.play.service.EventBus
import com.viva.play.ui.event.LoginEvent
import com.viva.play.ui.model.AuthModel
import com.viva.play.utils.SoftInputHelper
import com.viva.play.utils.ToastUtil.toast
import com.viva.play.utils.bind.binding
import com.viva.play.utils.bindColor
import com.viva.play.utils.postValue
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author 李雄厚
 *
 *
 */
@AndroidEntryPoint
class AuthActivity : BaseActivity(), OnFocusChangeListener {

    private val binding by binding<ActivityAuthBinding>()
    private val model by viewModels<AuthModel>()
    private val mViewColorNormal by bindColor(R.attr.colorIconMain)
    private val mViewColorFocus by bindColor(R.attr.colorIconThird)

    private var isRunning = false
    private lateinit var mSet1: AnimatorSet
    private lateinit var mSet2: AnimatorSet
    private lateinit var mSoftInputHelper: SoftInputHelper

    override fun initView(savedInstanceState: Bundle?) {
        binding.model = model
        mSoftInputHelper = SoftInputHelper.attach(this).moveBy(binding.rlInput)
        mSoftInputHelper.moveWith(
            binding.svLogin,
            binding.loginAccountEdt,
            binding.loginPasswordEdt,
        )
        binding.loginAccountEdt.onFocusChangeListener = this
        binding.loginPasswordEdt.onFocusChangeListener = this
        binding.loginPasswordEdt.addTextChangedListener {
            binding.loginPasswordDelete.isInvisible = it?.toString().isNullOrBlank()
        }

        binding.svLogin.setOnClickListener {
            model.login()
        }
    }


    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            when (v?.id) {
                R.id.loginAccountEdt -> {
                    binding.apply {
                        loginAccountIv.setColorFilter(mViewColorNormal)
                        view1.setBackgroundColor(mViewColorNormal)
                        loginPasswordIv.setColorFilter(mViewColorFocus)
                        view2.setBackgroundColor(mViewColorFocus)

                        bz1.setImageResource(R.drawable.bz0)
                        bz2.setImageResource(R.drawable.bz6)
                    }

                }
                R.id.loginPasswordEdt -> {
                    binding.apply {
                        loginPasswordDelete.isInvisible =
                            loginPasswordEdt.text.isNullOrBlank()
                        loginAccountIv.setColorFilter(mViewColorFocus)
                        view1.setBackgroundColor(mViewColorFocus)
                        loginPasswordIv.setColorFilter(mViewColorNormal)
                        view2.setBackgroundColor(mViewColorNormal)

                        bz1.setImageResource(R.drawable.bz3)
                        bz2.setImageResource(R.drawable.bz7)
                    }

                }

            }
        } else {
            binding.apply {
                if (v?.id == R.id.loginPasswordEdt) {
                    loginPasswordDelete.isInvisible = true
                }
                loginAccountIv.setColorFilter(mViewColorFocus)
                loginPasswordIv.setColorFilter(mViewColorFocus)
                view1.setBackgroundColor(mViewColorFocus)
                view2.setBackgroundColor(mViewColorFocus)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        model.login.observe(this) {
            postValue(EventBus.LOGIN, LoginEvent(it.nickname, it.rank, it.coinCount))
            App.notification()
            finish()
        }
        model.error.observe(this) {
            it.message.toast()
        }

    }


    override fun onStart() {
        isRunning = true
        mSet1 = startCircleAnim(binding.ivCircle1)
        mSet2 = startCircleAnim(binding.ivCircle2)
        super.onStart()
    }

    override fun onStop() {
        isRunning = false
        stopCircleAnim()
        super.onStop()
    }

    private fun stopCircleAnim() {
        mSet1.cancel()
        mSet2.cancel()
    }

    private fun startCircleAnim(target: View): AnimatorSet {
        val xy = calculateRandomXY()
        val set = createTranslationAnimator(target, xy[0], xy[1])
        set.doOnEnd {
            if (isRunning) {
                startCircleAnim(target)
            }
        }
        set.start()
        return set
    }

    private val mMaxMoveDuration = 10000L
    private val mMaxMoveDistanceX = 200
    private val mMaxMoveDistanceY = 20
    private val mRandom = Random()
    private fun calculateRandomXY(): FloatArray {
        val x = mRandom.nextInt(mMaxMoveDistanceX) - mMaxMoveDistanceX * 0.5F
        val y = mRandom.nextInt(mMaxMoveDistanceY) - mMaxMoveDistanceY * 0.5f
        return floatArrayOf(x, y)
    }

    private fun createTranslationAnimator(target: View, toX: Float, toY: Float): AnimatorSet {
        val fromX = target.translationX
        val fromY = target.translationY
        val duration = calculateDuration(fromX, fromY, toX, toY)
        val animatorX = ObjectAnimator.ofFloat(target, "translationX", fromX, toX)
        animatorX.duration = duration
        val animatorY = ObjectAnimator.ofFloat(target, "translationY", fromY, toY)
        animatorY.duration = duration
        val set = AnimatorSet()
        set.playTogether(animatorX, animatorY)
        return set
    }

    private fun calculateDuration(x1: Float, y1: Float, x2: Float, y2: Float): Long {
        val distance =
            abs(sqrt(abs(x1 - x2).toDouble().pow(2.0) + abs(y1 - y2).toDouble().pow(2.0)))
                .toFloat()
        val maxDistance = abs(
            sqrt(
                mMaxMoveDistanceX.toDouble().pow(2.0) + mMaxMoveDistanceY.toDouble().pow(2.0)
            )
        ).toFloat()
        return (mMaxMoveDuration * (distance / maxDistance)).toLong()
    }


}