package com.njut.activity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.njut.R;
import com.njut.data.CourseElement;

//点击课程弹出评一评说一说的弹窗
public class PopWindow {
	private Context context;
	private final PopupWindow popupWindow;
	private View popupview;
	private int TALK_OR_EVALUATE = 3;

	public PopWindow(Context context) {
		this.context = context;
		popupview = LayoutInflater.from(context).inflate(R.layout.popupwindow,
				null);
		popupWindow = new PopupWindow(popupview,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

	}

	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, 0, -10);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	public View getView() {
		return this.popupview;
	}

	// 隐藏菜单
	public void dismiss() {
		popupWindow.dismiss();
	}
}
