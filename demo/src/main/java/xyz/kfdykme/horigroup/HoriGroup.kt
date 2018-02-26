package xyz.kfdykme.horigroup

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup

/**
 * Created by wimkf on 2018/2/26.
 */
class HoriGroup(context: Context?) : ViewGroup(context) {

    val STATE_LEFT = 0

    val STATE_COMBINE= 1

    val DEFAULT_ANIMATION_TIME:Long = 200L

    val DEFAULT_ANIMATION_COUNT:Int = 100

    val DEFAULT_WIDTH_RATIO:Float = 3/4f

    val DeFAULT_SLIDING_DISTANCE:Int = 2000

    lateinit var leftView:View
    lateinit var rightView:View

    var state:Int = -1

    var widthRatio:Float = DEFAULT_WIDTH_RATIO

    var animationTime = DEFAULT_ANIMATION_TIME

    var animationCount = DEFAULT_ANIMATION_COUNT

    var slidingDistance = DeFAULT_SLIDING_DISTANCE

    var leftViewWidth: Int  =0

    var rightViewWidth: Int = 0

    var isUse:Boolean? = null


    constructor(context: Context?, attributeSet: AttributeSet):this(context){


    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        leftViewWidth = width

        rightViewWidth = width


        if(childCount == 2){
            leftView = getChildAt(0)
            rightView = getChildAt(1)
            isUse = true
        } else{
            isUse = false
            return
        }

        leftView.layout(0,0, leftViewWidth,height)
        rightView.layout(leftViewWidth,0, rightViewWidth +leftViewWidth,height)
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!isUse!!){
            return super.onTouchEvent(event)
        }

        var velovityTracker:VelocityTracker? = null
        velovityTracker = VelocityTracker.obtain()

        velovityTracker!!.addMovement(event)
        velovityTracker!!.computeCurrentVelocity(1000)
        var xVelocity = velovityTracker.getXVelocity()

        if(xVelocity<-slidingDistance)
            changeToCombine()
        else if(xVelocity> slidingDistance){
            changeToLeft()
        }

        return true
    }

    private fun changeToLeft(){
        if(state==STATE_LEFT) return
        Log.i("HoriGroup","changeToLeft")
        animation(animationCount,0)
        state = STATE_LEFT
    }

    private fun changeToCombine(){
        if(state == STATE_COMBINE) return
        Log.i("HoriGroup","changeToCombine")

        animation(0,animationCount)
        state = STATE_COMBINE
    }

    private fun animation( start:Int , end:Int){
        var valueAnimator = ValueAnimator.ofInt(start,end)
        valueAnimator.addUpdateListener(object :ValueAnimator.AnimatorUpdateListener{

            val v:Int = (width * widthRatio /animationCount).toInt()
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                var currentValue:Int = animation?.getAnimatedValue() as Int


                val ex = v * currentValue

                leftView.layout(0,0, leftViewWidth -ex,height)
                rightView.layout(rightViewWidth -ex,0, rightViewWidth *2-ex,height)
                //    invalidate()
            }
        })
        valueAnimator.setDuration(animationTime)
        valueAnimator.start()
    }
}
