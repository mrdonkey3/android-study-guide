package com.dk.android_art.customview.displayView


import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.dk.android_art.R
import com.dk.common.utils.UiCompatUtils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_disply_view.view.*
import org.jetbrains.annotations.NotNull
import kotlin.math.pow

/**
 * 展示view
 */
class DisplayView(ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs),
    View.OnClickListener {

    companion object {
        private const val BOTTOM = ConstraintSet.BOTTOM
        private const val TOP = ConstraintSet.TOP
        private const val START = ConstraintSet.START
        private const val END = ConstraintSet.END
        private const val VISIBLE = View.VISIBLE
        private const val INVISIBLE = View.INVISIBLE
        private const val TAG = "DisplayView"
        private const val DEBUG = true
    }


    //data
    private var entities = emptyArray<Entity>()//数据集
    private var lastIspList = emptyArray<Int>()//运营商列表存放code
    private var lastStatus: Int = STATUS.UNSYNC.status//上一次状态
    private var useFixedStatusPos: Boolean = true//是否使用固定的状态灯
    private var usePaAnimation: Boolean = true//是否使用功放动画

    //ids
    private val rootId = R.id.cst_root
    private val oneId = R.id.civ_one
    private val twoId = R.id.civ_two
    private val threeId = R.id.civ_three

    //widget
    private lateinit var civOne: CircleImageView
    private lateinit var civTwo: CircleImageView
    private lateinit var civThree: CircleImageView
    private lateinit var civOneStatus: CircleImageView
    private lateinit var civTwoStatus: CircleImageView
    private lateinit var civThreeStatus: CircleImageView

    //size
    private val dp24 = UiCompatUtils.dp2px(context, 24f)

    private val baseSize = dp24 //基准大小
    private val baseRatio = 0.8//基准比例

    init {
        //加载布局
        LayoutInflater.from(ctx).inflate(R.layout.layout_disply_view, this)
        if (DEBUG) this.setOnClickListener(this)
    }


    /**
     * 重新刷新布局
     * [ispList] 运营商列表 携带每一个运营商的编码（0，1，11，其他）
     * [status] 当前小区状态。未就绪0、就绪1，功放2，未同步3
     */
    fun reloadLayout(@NotNull ispList: Array<Int>, @NotNull status: Int) {
        //判断是否一致
        if (isNoDifference(ispList, status)) return
        //初始化工作
        initView()
        //保存和转换数据
        saveAndTransform(ispList, status)
        //重载元素的状态
        reloadStatus(entities)
        //重载元素的可见性
        reloadVisible(entities)
        //根据运营商个数改变约束与尺寸
        changeConstraintBySize(entities)
    }


    //没有差异
    private fun isNoDifference(ispList: Array<Int>, status: Int): Boolean {
        return ispList.contentEquals(lastIspList) && status == lastStatus
    }


    //初始化
    private fun initView() {
        civOne = civ_one
        civTwo = civ_two
        civThree = civ_three
        civOneStatus = civ_one_status
        civTwoStatus = civ_two_status
        civThreeStatus = civ_three_status
    }

    //保存并转换数据
    private fun saveAndTransform(ispList: Array<Int>, status: Int) {

        lastIspList = ispList
        lastStatus = status

        var tempIspList = ispList

        //如果运营商都是同一个，则只显示一个
        if (ispList.size > 1 && ispList.distinct().size == 1) {
            tempIspList = ispList.distinct().toTypedArray()
        }

        //如果列表为空，则显示未知
        if (ispList.isEmpty()) {
            tempIspList = ispList.plus(ISP.UNKNOWN.code)
        }

        val result = arrayListOf<Entity>()
        tempIspList.forEachIndexed { index, code ->
            val entity = when (index) {
                0 -> Entity(civOne, civOneStatus, ISP.getISP(code), STATUS.getStatus(status))
                1 -> Entity(civTwo, civThreeStatus, ISP.getISP(code), STATUS.getStatus(status))
                2 -> Entity(civThree, civThreeStatus, ISP.getISP(code), STATUS.getStatus(status))
                else -> Entity(civOne, civOneStatus, ISP.getISP(code), STATUS.getStatus(status))
            }
            result.add(entity)
        }
        entities = result.toTypedArray()
    }


    //重载元素的状态
    private fun reloadStatus(entities: Array<Entity>) {
        entities.forEach { entity ->
            val ispTint =
                if (entity.status == STATUS.UNREADY) entity.isp.darkColor else entity.isp.lightColor
            val civStatus = if (useFixedStatusPos) civ_status else entity.civStatus
            breathingLightStatus(civStatus, entity.status)
            modifyStatusColor(civStatus, entity.status.color)
            modifySvgColor(entity.civIsp, entity.isp.resId, ispTint)
        }
    }


    //呼吸灯效果
    private fun breathingLightStatus(civ: CircleImageView, status: STATUS) {
        civ.clearAnimation()
        if (status != STATUS.PA || !usePaAnimation) return
        //闪烁
        val alphaAnimation = AlphaAnimation(0.6f, 1.0f)
        alphaAnimation.duration = 800
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.REVERSE
        civ.animation = alphaAnimation
        alphaAnimation.start()
    }

    //更改状态颜色
    private fun modifyStatusColor(civ: CircleImageView, colorId: Int) {
        civ.circleBackgroundColor = ContextCompat.getColor(context, colorId)
    }

    //更改svg的颜色
    private fun modifySvgColor(civ: CircleImageView, resId: Int, colorId: Int) {
        civ.borderColor = ContextCompat.getColor(context, colorId)//描边颜色
        ImageViewCompat.setImageTintList(
            civ,
            AppCompatResources.getColorStateList(context, colorId)
        )//tint着色
        civ.setImageResource(resId)
    }

    //重载元素可见性
    private fun reloadVisible(entities: Array<Entity>) {
        val isNotNullOrEmpty = !entities.isNullOrEmpty()
        civOne.visibility = if (isNotNullOrEmpty) VISIBLE else INVISIBLE
        civOneStatus.visibility =
            if (!useFixedStatusPos && isNotNullOrEmpty && entities.size != 2) VISIBLE else INVISIBLE
        civTwo.visibility = if (entities.size == 2) VISIBLE else INVISIBLE
        civTwoStatus.visibility =
            if (entities.size == 2 && !useFixedStatusPos) VISIBLE else INVISIBLE
        civThree.visibility = if (entities.size == 3) VISIBLE else INVISIBLE
    }

    //根据运营商个数改变约束
    private fun changeConstraintBySize(entities: Array<Entity>) {

        val constraintSet = ConstraintSet()
        //从根布局中克隆约束
        constraintSet.clone(cst_root)
        val s = entities.size - 1
        //0.8的几次幂
        val ratio = baseRatio.pow(if (s == -1) 0 else s)
        val size = baseSize.times(ratio).toInt()

        Log.i(TAG, "=====size=====:$dp24")
        Log.i(TAG, "=====size=====:$size")

        //清空原有控件的约束
        constraintSet.clear(oneId)
        constraintSet.clear(twoId)
        constraintSet.clear(threeId)

        constraintSet.constrainWidth(oneId, size)
        constraintSet.constrainHeight(oneId, size)

        when (entities.size) {
            1 -> {//一个运营商
                //startToStart
                constraintSet.connect(oneId, START, rootId, START)
                //endToEnd
                constraintSet.connect(oneId, END, rootId, END)
                //topToTop
                constraintSet.connect(oneId, TOP, rootId, TOP)
                //bottomToBottom
                constraintSet.connect(oneId, BOTTOM, rootId, BOTTOM)
            }
            2 -> {//两个运营商
                constraintSet.constrainWidth(twoId, size)
                constraintSet.constrainHeight(twoId, size)

                constraintSet.connect(oneId, START, rootId, START, size.div(4))
                constraintSet.connect(oneId, TOP, rootId, TOP)
                constraintSet.connect(oneId, BOTTOM, rootId, BOTTOM)

                constraintSet.connect(twoId, END, rootId, END, size.div(4))
                constraintSet.connect(twoId, TOP, rootId, TOP)
                constraintSet.connect(twoId, BOTTOM, rootId, BOTTOM)


            }
            3 -> {//三个运营商
                constraintSet.constrainWidth(twoId, size)
                constraintSet.constrainHeight(twoId, size)
                constraintSet.constrainWidth(threeId, size)
                constraintSet.constrainHeight(threeId, size)

                val tSize = size.times(baseRatio).toInt()
                constraintSet.connect(oneId, START, rootId, START)
                constraintSet.connect(oneId, TOP, rootId, TOP, tSize.div(2.5).toInt())
                constraintSet.connect(oneId, END, rootId, END)

                constraintSet.connect(twoId, TOP, rootId, TOP, tSize)
                constraintSet.connect(twoId, END, oneId, END, tSize.div(2))
                constraintSet.connect(twoId, BOTTOM, rootId, BOTTOM)

                constraintSet.connect(threeId, TOP, twoId, TOP)
                constraintSet.connect(threeId, START, oneId, START, tSize.div(2))

            }
            else -> {
            }
        }
        //设置一个动画效果，让约束改变平滑一点，这一步不是必须的
        TransitionManager.beginDelayedTransition(cst_root)
        constraintSet.applyTo(cst_root)
    }

    override fun onClick(v: View?) {
        var isps =
            arrayOf(0, 1, 11, -1).toList().shuffled().take(arrayOf(1, 2, 3).random()).toTypedArray()
        var status = arrayOf(STATUS.UNREADY, STATUS.READY, STATUS.PA).random().status
        if (isps.contains(-1)) {
            isps = arrayOf(-1)
            status = STATUS.UNREADY.status
        }
        Log.i(TAG, "---isp-----:${isps.joinToString()}")
        Log.i(TAG, "---status---:$status")
        reloadLayout(isps, status)
    }

    data class Entity constructor(
        val civIsp: CircleImageView,
        val civStatus: CircleImageView,
        val isp: ISP,
        val status: STATUS
    )

    enum class STATUS(val status: Int, val color: Int) {
        UNREADY(0, R.color.color_unready),
        READY(1, R.color.color_ready),
        PA(2, R.color.color_pa),
        UNSYNC(3, R.color.color_unsync);

        companion object {
            @JvmStatic
            fun getStatus(status: Int): STATUS {
                return values().firstOrNull { it.status == status } ?: UNREADY
            }
        }
    }

    enum class ISP constructor(
        val code: Int,
        val resId: Int,
        val lightColor: Int,
        val darkColor: Int
    ) {
        MOBILE(0, R.drawable.ic_mobile, R.color.color_mobile, R.color.color_unready),
        UNICOM(1, R.drawable.ic_unicom, R.color.color_unicom, R.color.color_unready),
        TELECOM(11, R.drawable.ic_telecom, R.color.color_telecom, R.color.color_unready),
        UNKNOWN(-1, R.drawable.ic_unknown, R.color.color_unknow, R.color.color_unready);

        companion object {
            @JvmStatic
            fun getISP(code: Int): ISP {
                return values().firstOrNull { it.code == code } ?: UNKNOWN
            }
        }
    }
}