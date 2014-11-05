package com.njut.utility;

import java.util.List;

import org.nutlab.kczl.kczlApplication;

import com.njut.data.AchievementElement;

public class GPACal {
	public double getWholeGPA(List<AchievementElement> achievementElements) {
		double gpas = 0.0;
		double credit = 0.0;
		for (int i = 0; i < achievementElements.size(); i++) {
			if (achievementElements.get(i).getPoint() <= 0.0)
				continue;
			else {
				gpas += achievementElements.get(i).getPoint()
						* achievementElements.get(i).getCredit();
				credit += achievementElements.get(i).getCredit();
			}
		}
		return gpas / credit;
	}

	public double getDegreeGPA(List<AchievementElement> achievementElements) {
		double gpas = 0.0;
		double credit = 0.0;
		int type = 0;
		if (kczlApplication.Person.getGrade().trim().equals("2010"))
			type = 1;
		if (kczlApplication.Person.getGrade().trim().equals("2009"))
			return -1000.0;
		for (int i = 0; i < achievementElements.size(); i++) {
			if (achievementElements.get(i).getPoint() <= 0.0)
				continue;
			else {
				if (type == 0
						&& achievementElements.get(i).getType().equals("±ØÐÞ¿Î")) {
					gpas += achievementElements.get(i).getPoint()
							* achievementElements.get(i).getCredit();
					credit += achievementElements.get(i).getCredit();
				} else {
					if (type == 1
							&& achievementElements.get(i).getType()
									.equals("Ñ§Î»¿Î")) {
						gpas += achievementElements.get(i).getPoint()
								* achievementElements.get(i).getCredit();
						credit += achievementElements.get(i).getCredit();
					}
				}
			}

		}
		return gpas / credit;
	}
}
