package com.njut.utility;

import java.util.Comparator;

import com.njut.data.AchievementElement;

public class ComparatorAchievementElement implements Comparator{

	@Override
	public int compare(Object lhs, Object rhs) {
		AchievementElement AchievementElement0=(AchievementElement)lhs;
		AchievementElement AchievementElement1=(AchievementElement)rhs;
		 //���ȱȽϳɼ�������ɼ���ͬ����ȽϿγ���

		  int flag=((AchievementElement0.getScore())>(AchievementElement1.getScore()))?1:0;
		  if(flag==0){
		   return AchievementElement0.getCourseName().compareTo(AchievementElement1.getCourseName());
		  }else{
		   return flag;
		  }  
		 }
	

}
