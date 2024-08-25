package com.yuuki.purestatusbar.view.blur;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import com.yuuki.purestatusbar.view.ViewAnimator;

public final class DialogUtil {
    private static final String TAG = "DialogUtil";

    private DialogUtil() {
        throw new AssertionError();
    }

    /**
     * 设置 Dialog 内容的 Gravity
     *
     * @param dialog  Dialog 对象，不能为 null
     * @param gravity Gravity 常量，具体请参考 {@code android.view.Gravity} 类
     */
    public static void setGravity(Dialog dialog, int gravity) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setGravity(gravity);
    }

    /**
     * 设置 Dialog 内容的宽度。
     *
     * @param dialog Dialog 对象，不能为 null
     * @param width  宽度值
     */
    public static void setWith(Dialog dialog, int width) {


        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        View decorView = window.getDecorView();
        decorView.setPadding(0, decorView.getTop(), 0, decorView.getBottom());

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        window.setAttributes(lp);
    }

    /**
     * 设置 Dialog 内容的高度。
     *
     * @param dialog Dialog 对象，不能为 null
     * @param height 高度值
     */
    public static void setHeight(Dialog dialog, int height) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        View decorView = window.getDecorView();
        decorView.setPadding(decorView.getLeft(), 0, decorView.getRight(), 0);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = height;
        window.setAttributes(lp);
    }

    
    public static void setAnimations(Dialog dialog, int styleRes) {
        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setWindowAnimations(styleRes);
    }

    /**
     * 设置 Dialog 的背景。
     *
     * @param dialog   Dialog 对象，不能为 null
     * @param drawable Drawable 对象，不能为 null
     */
    public static void setBackgroundDrawable(Dialog dialog, Drawable drawable) {

        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setBackgroundDrawable(drawable);
    }

    /**
     * 设置 Dialog 的背景。
     *
     * @param dialog     Dialog 对象，不能为 null
     * @param drawableId Drawable 资源的 ID
     */
    public static void setBackgroundDrawableResource(Dialog dialog, int drawableId) {
        Window window = dialog.getWindow();
        if (window == null) {
            Log.e(TAG, "The Window of dialog is null.");
            return;
        }

        window.setBackgroundDrawableResource(drawableId);
    }

    /**
     * 设置dialog背景模糊
     *
     * @param dialog 对话框
     * @return 模糊层View
     */
    public static BlurView setBlur(Dialog dialog) {
        final ViewGroup dialogRoot = (ViewGroup) dialog.getWindow().getDecorView();
        final BlurView blurView = new BlurView(dialog.getContext());
        dialogRoot.setBackgroundColor(Color.TRANSPARENT);
        final View v = dialogRoot.getChildAt(0);
        v.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
                dialogRoot.addView(blurView, 0, params);
				ViewAnimator.animate(v)
					.zoomIn()
					.interpolator(new DecelerateInterpolator())			
					.duration(500)
					.start();
				ViewAnimator.animate(blurView)
					.alpha(0,1)
					.interpolator(new DecelerateInterpolator())			
					.duration(600)
					.start();
            }
        });
        return blurView;
    }

    /**
     * 设置背景深度 即灰色遮罩程度
     * *@param dialog 对话框
     *
     * @param f 参数最大为1f，越大越黑
     */
    public static void setBackgroundOverlay(Dialog dialog, float f) {
        dialog.getWindow().setDimAmount(f);
    }
}
