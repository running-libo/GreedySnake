package com.libo.greedysnake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.libo.greedysnake.databinding.ViewSceneBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * description 场景view
 * 游戏场景由16*32个小方块组成
 */
class SceneView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var snakePaint: Paint = Paint()
    var blankPaint: Paint = Paint()
    var horizontalSpace: Float = 0f
    /** 方格间距 */
    var dividerSpace = 3f
    /** 蛇的路径坐标记录 */
    var snakeList = ArrayList<Int>()
    /** 食物的坐标位置 */
    var foodPos = 13
    var direction: Direction = Direction.RIGHT
    var timer: Timer? = null

    init {
        snakePaint.color = resources.getColor(R.color.black)
        blankPaint.color = resources.getColor(R.color.half_black)

        snakeList.add(0)
        snakeList.add(1)
        snakeList.add(2)

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
        for (j in 0..15) {
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

        }, 1000, 500)
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
        var head = snakeList[snakeList.size-1]  //头结点位置
        //横左边移动+-1，纵坐标移动+-100
        //根据当前方向，将蛇移动
        when(direction) {
            Direction.TOP -> {
                snakeList.add(head-100)
            }
            Direction.RIGHT -> {
                //往头结点左边添加一个节点
                snakeList.add(head+1)
            }
            Direction.DOWN -> {
                snakeList.add(head+100)
            }
            Direction.LEFT -> {
                snakeList.add(head-1)
            }
        }

        //往当前方向移动一步，头往前一步，需要删除尾节点
        if (snakeList.size > 0) {
            snakeList.remove(snakeList[0])
        }


    }

}