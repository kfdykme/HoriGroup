package xyz.kfdykme.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

/**
 * Created by wimkf on 2018/2/26.
 */
class HoriGroup  : RelativeLayout  {


    companion object {

        val TAG:String = "HoriGroup"

        val  STATE_LEFT = 0

        val STATE_COMBINE= 1

        val DEFAULT_ANIMATION_TIME:Long = 200L

        val DEFAULT_ANIMATION_COUNT:Int = 100

        val DEFAULT_WIDTH_RATIO:Float = 3/4f

        val DeFAULT_SLIDING_DISTANCE:Int = 2000
    }


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


    interface AnimationListener{
        fun onSuccess(state:Int)
        fun onStart(state:Int)
    }

    var mAnimationListener:AnimationListener? = null

    fun setAnimationListener(l:AnimationListener){
        mAnimationListener = l
    }

    constructor(context: Context):super(context){

    }

    constructor(context: Context, attributeSet: AttributeSet):super(context,attributeSet){


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
        var yVelocity = velovityTracker.getYVelocity()

        if(Math.abs(yVelocity) > Math.abs(xVelocity)) return false
         dealWithXVelocity(xVelocity, mAnimationListener)
        return true

    }

    fun dealWithXVelocity(xVelocity :Float,l:AnimationListener?):Boolean{

        Log.i(TAG,"dealWithXVelocity $xVelocity")
        if(xVelocity <-slidingDistance){
            changeToCombine(l)
            return true
        } else if(xVelocity > slidingDistance){
            changeToLeft(l)
            return true
        }

        return false
    }

    fun changeToLeft(l:AnimationListener?){
        if(state==STATE_LEFT) return
        Log.i("HoriGroup","changeToLeft")
        state = STATE_LEFT
        animation(animationCount,0,l)

    }

    fun changeToCombine(l:AnimationListener?){
        if(state == STATE_COMBINE) return
        Log.i("HoriGroup","changeToCombine")
        state = STATE_COMBINE
        animation(0,animationCount,l)

    }

    public fun animation( start:Int , end:Int,l:AnimationListener?){
        var valueAnimator = ValueAnimator.ofInt(start,end)
        valueAnimator.addUpdateListener(object :ValueAnimator.AnimatorUpdateListener{

            val v:Int = (width * widthRatio /animationCount).toInt()
            var isEnd:Boolean = false
            var isStart:Boolean  = false
            override fun onAnimationUpdate(animation: ValueAnimator?) {
                var currentValue:Int = animation?.getAnimatedValue() as Int

                if(currentValue == start && !isStart){
                    isStart = true
                    l?.onStart(state)
                }

                val ex = v * currentValue
                if(currentValue >= (end * 0.9) && !isEnd  ){
                    isEnd = true
                    l?.onSuccess(state)
                }
                leftView.layout(0,0, leftViewWidth -ex,height)
                rightView.layout(rightViewWidth -ex,0, rightViewWidth +leftViewWidth-ex,height)
                //    invalidate()

            }
        })
        valueAnimator.setDuration(animationTime)
        valueAnimator.start()
    }
}
