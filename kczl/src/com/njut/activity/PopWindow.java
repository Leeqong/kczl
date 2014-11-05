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

//����γ̵�����һ��˵һ˵�ĵ���
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
		// �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����������ģ�
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

	}

	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent, 0, -10);
		// ʹ��ۼ�
		popupWindow.setFocusable(true);
		// ����������������ʧ
		popupWindow.setOutsideTouchable(true);
		// ˢ��״̬
		popupWindow.update();
	}

	public View getView() {
		return this.popupview;
	}

	// ���ز˵�
	public void dismiss() {
		popupWindow.dismiss();
	}
}
