package com.njut.view;

/**
 * 
 */

import com.njut.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

public class OverlayView extends View {
	private Bitmap bitmap;
	private Context context;
	private Rect dst;
	private float gridWidth;
	private float shadowWidth;
	private float space;
	private int height2;
	private int width2;

	/**
	 * @param context
	 */
	public OverlayView(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
		Drawable drawable = getResources().getDrawable(
				R.drawable.calendar_list_overlay);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		bitmap = bitmapDrawable.getBitmap();
		float scale = context.getResources().getDisplayMetrics().density;
		//screenWidth = (int)(dm.widthPixels * density + 0.5f); // 屏幕宽（px，如：480px）
		int screenWidth480 = (int) (320 * scale + 0.5f);
		DisplayMetrics dm = new DisplayMetrics();

		// 取得窗口属性
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(
				dm);

		// 窗口的宽度
		int screenWidth = dm.widthPixels;
		dst = new Rect();
		if (screenWidth < screenWidth480) {
			gridWidth = screenWidth / 7.0f;
		} else {
			gridWidth = screenWidth480 / 7.0f;
		}
		space = screenWidth / 7.0f;
		shadowWidth = (int) gridWidth * 16 / 100;//?
		dst.top = (int) -shadowWidth;
		dst.bottom = (int) (gridWidth * (192 - 16) / 100);//192是图片高度，图片132X192   16是图片内容竖直上的padding
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true); 
		canvas.drawBitmap(bitmap, null, dst, mPaint);
	}

	public void darwOverlay(int position) {
		height2 =dst.bottom - dst.top;
		width2 = (int) ((height2/192f)*132);
		int temp = (int) ((width2 - gridWidth)/2f);
		float i = space - gridWidth;
		if (i > 0)
			dst.left = (int) (-temp + i / 2 + space * position);
		else
			dst.left = (int) (-temp + gridWidth * position);
		dst.right = (int) (dst.left + gridWidth + temp * 2);
		this.invalidate();

	}
}
