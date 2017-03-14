package com.yufa.smell.Util;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/12/22.
 * 菜单显示辅助类
 */

public class MenuShow {
    //菜单列表的布局
    private RelativeLayout menuList;
    //用于判断菜单的显示状况，true为显示，false为隐藏
    private boolean menuListPlay = false;
    //用于记录有多少个动画在执行
    private int annimationCount = 0;
    //点击菜单按钮后显示菜单列表的方法

    public MenuShow(RelativeLayout menuList){
        this.menuList = menuList;
    }

    public void showList() {
        //当点击的时候就可以进行判断，只要annimationCount值大于0，说明还有动画在执行，不进行操作
        if (annimationCount > 0) {
            return ;
        }
        //菜单显示，就将菜单列表隐藏
        if (menuListPlay) {
            //将菜单列表隐藏，并改变标记
            hideMenu(menuList,300);
            menuListPlay = false;
            return;
        }
        //菜单列表不显示的时候，就将菜单列表显示
        if (!menuListPlay) {
            showMenu(menuList);
            menuListPlay = true;
            return;
        }
    }
    /**
     * 显示菜单
     * @param view  要显示的菜单
     */
    private void showMenu(RelativeLayout view) {
//		view.setVisibility(View.VISIBLE);
        //如果要显示菜单，那么就将相应的控件设为有焦点
        //获取父容器中有几个子控件
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            view.getChildAt(i).setEnabled(true);
        }
        //动画集
        AnimationSet animSet = new AnimationSet(true);
        //渐变动画
        AlphaAnimation alphAnim = new AlphaAnimation(0,1);
        animSet.addAnimation(alphAnim);
        //旋转动画
        /*
        RotateAnimation rotaAnim = new RotateAnimation(-90, 0, Animation.RELATIVE_TO_SELF,
                1f, Animation.RELATIVE_TO_SELF, 1f);
        animSet.addAnimation(rotaAnim);
        */
        animSet.setDuration(500);
        animSet.setFillAfter(true);//动画停留在动画结束的位置
        view.startAnimation(animSet);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画开始的时候回调
                annimationCount ++;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //动画执行过程调用
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画结束的时候调用
                annimationCount --;
            }
        });
    }
    /**
     * 隐藏菜单
     * @param view  要隐藏的菜单 ,startOffset 动画延迟执行的时间
     */
    private void hideMenu(RelativeLayout view,long startOffset) {
//		view.setVisibility(View.GONE);
        //如果要隐藏菜单，那么就将相应的控件设为没有焦点
        //获取父容器中有几个子控件
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            view.getChildAt(i).setEnabled(false);
        }
        /**
         * 旋转动画
         * RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue)
         * fromDegrees 开始旋转角度
         * toDegrees 旋转的结束角度
         * pivotXType X轴 参照物 （X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF）
         * pivotXValue x轴 旋转的参考点（x坐标的伸缩值）
         * pivotYType Y轴 参照物 （Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF）
         * pivotYValue Y轴 旋转的参考点 (（Y坐标的伸缩值） )
         */
        //动画集
        AnimationSet animSet = new AnimationSet(true);
        //渐变动画
        AlphaAnimation alphAnim = new AlphaAnimation(1,0);
        animSet.addAnimation(alphAnim);
        //旋转动画
        /*
        RotateAnimation rotaAnim = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF,
                1f, Animation.RELATIVE_TO_SELF, 1f);
        animSet.addAnimation(rotaAnim);
        */
        animSet.setDuration(500);
        animSet.setFillAfter(true);//动画停留在动画结束的位置
        animSet.setStartOffset(startOffset);		//设置动画的延迟执行
        view.startAnimation(animSet);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                annimationCount ++;
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                annimationCount --;
            }
        });
    }
}
