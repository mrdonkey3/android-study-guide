package com.dk.android_art.activities

import android.animation.*
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.dk.android_art.R
import com.dk.android_art.animation.propertyanimator.ImageViewWrapper
import com.dk.android_art.animation.viewanimation.Rotate3dAnimation
import kotlinx.android.synthetic.main.activity_animation.*

class AnimationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        setListener()
    }

    private fun setListener() {
        iv_view_animation.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.view_animation)
            iv_view_animation.startAnimation(animation)
            iv_view_animation.postDelayed({
                iv_view_animation.startAnimation(AlphaAnimation(0f, 1f).apply {
                    duration = 500
                })//java代码实现动
            }, 500)
            iv_view_animation.postDelayed({
                iv_view_animation.startAnimation(
                    Rotate3dAnimation(
                        0f,
                        360f,
                        20f,
                        20f,
                        100f,
                        false
                    ).apply { duration = 1000 })
            }, 1000)
        }
        iv_frame_animator.setOnClickListener {
            println("--->iv_frame_animator")
            iv_frame_animator.background =
                ContextCompat.getDrawable(this, R.drawable.frame_animation)
            (iv_frame_animator.background as AnimationDrawable).start()
        }
        iv_property_animator.setOnClickListener {
            println("--->iv_property_animator")
            val propertyAnimatorSet =
                AnimatorInflater.loadAnimator(this, R.animator.property_animator) as AnimatorSet
            propertyAnimatorSet.setTarget(iv_property_animator)
            propertyAnimatorSet.start()
            iv_property_animator.postDelayed({
                //使用包装类类来实现属性动画
                val imageViewWrapper = ImageViewWrapper(iv_property_animator)
                val objectAnimator = ObjectAnimator.ofInt(imageViewWrapper, "width", 100)
                    .setDuration(500)
                objectAnimator.repeatCount = 0
                objectAnimator.start()

            }, 1000)

            //使用valueAnimator
            val start = iv_property_animator.width
            val end = 500

            iv_property_animator.postDelayed({
                val valueAnimator = ValueAnimator.ofInt(1, 100)
                valueAnimator.interpolator = AccelerateDecelerateInterpolator()//加减速插值器,两头慢中间快
                val intEvaluator = IntEvaluator()//估值器，根据百分比来计算最终改变的属性值
                valueAnimator.addUpdateListener {
                    //1...100
                    println("--->${it.animatedValue}")
                    //当前值
                    val currentValue = it.animatedValue.toString().toInt()
                    //当前百分比
                    val currentFraction = it.animatedFraction
                    //利用估计值算出此次的属性值
                    val evaluateValue = intEvaluator.evaluate(
                        currentFraction,
                        start,
                        end
                    )
                    iv_property_animator.layoutParams.width = evaluateValue
                    iv_property_animator.requestLayout()
                }
                valueAnimator.start()
            }, 2000)
        }
    }
}