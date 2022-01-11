package com.libo.greedysnake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.libo.greedysnake.databinding.ViewSceneBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * description 场景view
 * 游戏场景由16*32个小方块组成
 */
class SceneView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private var snakePaint: Paint = Paint()
    var blankPaint: Paint = Paint()
    var horizontalSpace: Float = 0f
    /** 方格间距 */
    private var dividerSpace = 3f
    /** 蛇的路径坐标记录 */
    private var snakeList = ArrayList<Int>()
    /** 食物的坐标位置 */
    var foodPos = 0
    var direction: Direction = Direction.RIGHT
    var timer: Timer? = null

    init {
        snakePaint.color = resources.getColor(R.color.black)
        blankPaint.color = resources.getColor(R.color.half_black)

        restartGame()

        post {
            bindListener()
        }
    }

    private var mBinding: ViewSceneBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.view_scene, this, true
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        horizontalSpace = (measuredWidth/32).toFloat()
    }

    private fun bindListener() {
        //注意：不能往当前方法的返方向走
        mBinding.ivTop.setOnClickListener {
            if (direction != Direction.DOWN) {
                direction = Direction.TOP
            }
        }
        mBinding.ivRight.setOnClickListener {
            if (direction != Direction.LEFT) {
                direction = Direction.RIGHT
            }
        }
        mBinding.ivBottom.setOnClickListener {
            if (direction != Direction.TOP) {
                direction = Direction.DOWN
            }
        }
        mBinding.ivLeft.setOnClickListener {
            if (direction != Direction.RIGHT) {
                direction = Direction.LEFT
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //画16*32的方格，而且有间距
        //画16行
        var curPaint: Paint
        for (j in 0..17) {
            //画每一行矩形
            for (i in 0..31) {
                if (snakeList.contains(j*100+i) || foodPos == (j*100+i)) {
                    //当前格在蛇的记录范围里，需要使用snakePaint
                    curPaint = snakePaint
                } else {
                    //使用blankPaint
                    curPaint = blankPaint
                }
                canvas.drawRect(i*(horizontalSpace+dividerSpace) + dividerSpace, j*(horizontalSpace+dividerSpace) + dividerSpace,
                    i*(horizontalSpace+dividerSpace) + dividerSpace + horizontalSpace,
                    j*(horizontalSpace+dividerSpace) + dividerSpace + horizontalSpace, curPaint)
            }
        }
    }

    /**
     * 开始游戏
     */
    fun startGame() {
        timer = Timer()
        timer?.schedule(object: TimerTask() {
            override fun run() {
                move()
                invalidate()
            }

        }, 1000, 400)
    }

    /**
     * 结束游戏
     */
    fun stopGame() {
        timer?.cancel()
    }

    /**
     * 继续移动
     */
    fun move() {
        if (addHead()) {
            restartGame()
            return
        }

        removeTail()

        handleFood()
    }

    /**
     * 添加头节点
     * 返回是否需要重新开始游戏
     */
    private fun addHead(): Boolean {
        var head = snakeList[snakeList.size-1]  //头结点位置
        //横左边移动+-1，纵坐标移动+-100
        //根据当前方向，将蛇移动
        when(direction) {
            //移动过程中，需要判断是否超出屏幕
            Direction.TOP -> {
                //如果头在第一行，还是向上，那么就失败了
                if (head < 100) {
                    //在第一行
                    return true
                } else {
                    snakeList.add(head-100)
                }
            }
            Direction.RIGHT -> {
                if (head % 100 == 31) {
                    //在最右一列
                    return true
                } else {
                    snakeList.add(head+1)
                }
            }
            Direction.DOWN -> {
                if (head >= 1700) {
                    //在最后一行
                    return true
                } else {
                    snakeList.add(head+100)
                }
            }
            Direction.LEFT -> {
                if (head % 100 == 0) {
                    //在最左一列
                    return true
                } else {
                    snakeList.add(head-1)
                }
            }
        }
        return false
    }

    /**
     * 删除尾节点
     */
    private fun removeTail() {
        //往当前方向移动一步，头往前一步，需要删除尾节点
        if (snakeList.size > 0) {
            snakeList.remove(snakeList[0])
        }
    }

    /**
     * 重新开始游戏
     */
    private fun restartGame() {
        snakeList.clear()
        snakeList.add(0)
        snakeList.add(1)
        snakeList.add(2)
        foodPos = 13
        direction = Direction.RIGHT
    }

    /**
     * 如果在蛇移动的过程中，包含了食物的位置，那么就吃掉了食物
     */
    private fun handleFood() {
        if (snakeList.contains(foodPos)) {
            //吃到了食物

            //从全盘面随机生成一个点数，且不再蛇的范围内，作为新生成食物
            foodPos = (Math.random()*18).toInt()*100 + (Math.random()*32).toInt()

            //蛇头节点长度+1
            addHead()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        stopGame()
    }

}