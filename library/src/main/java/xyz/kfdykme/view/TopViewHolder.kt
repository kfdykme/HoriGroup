package xyz.kfdykme.topgroupview

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * Created by wimkf on 2018/3/12.
 */
open class TopViewHolder{

    companion object {

        val TOUCH_DRAG = 1

        val TOUCH_NO_DRAG = 0

        var TAG = "TopViewHolder"

        val LT = 0

        val LB = 1

        val RT = 2

        val RB = 3

        val NOT_OVERLAP = -1
    }

    var x:Int =0
    var y:Int =0

    var l = 0
    var r = 0
    var t = 0
    var b = 0

    var width = 5

    var height = 5

    var basic :Int? = null

    var view:View? = null

    var dx = 0

    var dy = 0

    var mTopGroupView :TopGroupView? = null

    var touchState:Int = TOUCH_NO_DRAG

    var realX = 0

    var realY = 0

    constructor(view:View){
        this.view= view
        basic = view.context.resources.getDimension(R.dimen.basic).toInt()
        view.setOnTouchListener(mOnTouchListener)
        view.setOnLongClickListener(mOnLongClickListener)
    }

    var mOnTouchListener = object :View.OnTouchListener{

        var lx = 0
        var ly = 0

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            when(event!!.action){
                MotionEvent.ACTION_DOWN->{
                    lx = event!!.rawX.toInt()
                    ly = event!!.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE->{

                    if(touchState == TOUCH_DRAG)
                    {

                        var rx = event!!.rawX.toInt()
                        var ry = event!!.rawY.toInt()

                        dx += rx - lx
                        dy += ry - ly

                        layout()

                        lx = rx
                        ly = ry
                    }
                }
                MotionEvent.ACTION_UP->{

                    if(touchState == TOUCH_DRAG)
                        switchTouchState()
                    reflashDxDy()
                    layout()

                    if(mTopGroupView!!.checkOverLap(this@TopViewHolder)
                            && touchState == TOUCH_NO_DRAG)
                        switchTouchState()

                    if(touchState == TOUCH_NO_DRAG)
                        saveAsRealXY()
                    else {
                        loadFromRealXY()
                        layout()
                        touchState = TOUCH_NO_DRAG
                    }
                }
            }

            return false
        }

    }

    var mOnLongClickListener = object :View.OnLongClickListener{

        override fun onLongClick(v: View?): Boolean {
            switchTouchState()
            return false
        }
    }


    fun onUpdateXYWH(){

        l = x
        r = x+width
        t = y
        b = t+ height
    }

    fun reflashDxDy(){
        x += dx/basic!!
        y += dy/basic!!
        dx = 0
        dy = 0
        onUpdateXYWH()
    }

    fun switchTouchState(){
        when(touchState){
            TOUCH_DRAG->{
                touchState = TOUCH_NO_DRAG
            }
            TOUCH_NO_DRAG ->{
                touchState = TOUCH_DRAG
            }
        }

        //for test
        var tv = view!!.findViewById<TextView>(R.id.tv)
        if(touchState == TOUCH_DRAG)
            tv.text = "drag"
        else
            tv.text = "can't drag"


    }

    fun isOverLap(it:TopViewHolder):Int{
        if (isIn(it.l,it.t) )return LT
        if (isIn(it.l,it.b) )return LB
        if (isIn(it.r,it.t) )return RT
        if (isIn(it.r,it.b)) return RB

        return NOT_OVERLAP
    }

    fun isIn(tx:Int,ty:Int):Boolean{
        var isIn = false
        if(tx > l && tx < r && ty > t && ty <b)
            isIn = true


        //Log.i(TAG,"$tx $ty $l $r $t $b $isIn")

        return isIn
    }
    
    fun layout(){
        view!!.layout(
                l* basic!! + dx,
                t* basic!! + dy,
                r* basic!! + dx,
                b* basic!! + dy)

    }

    fun loadFromRealXY(){
        x = realX
        y = realY
        onUpdateXYWH()
    }

    fun saveAsRealXY(){
        realX = x
        realY = y

    }
}