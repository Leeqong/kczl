package com.njut.utility;

import java.util.List;

import com.njut.data.AchievementElement;
import com.njut.data.CourseElement;

public class sortHelper {
	
	
public List<AchievementElement> AchievementElementDESC(List<AchievementElement> list) {
    int i, j;
    AchievementElement temp;
    for (i = 1; i < list.size(); i++) {
        for (j = i, temp = (AchievementElement)list.get(i); j > 0 && temp.getScore() > ((AchievementElement)list.get(j-1)).getScore(); j--)
            list.set(j,(AchievementElement)list.get(j - 1));
        list.set(j,temp);
    }
	
	return list;
}

public List<CourseElement> CourseElementDESC(List<CourseElement> list) {
    int i, j;
    CourseElement temp;
    for (i = 1; i < list.size(); i++) {
        for (j = i, temp = (CourseElement)list.get(i); j > 0 && Integer.parseInt(temp.getTime().split(":")[0]) < Integer.parseInt(((CourseElement)list.get(j-1)).getTime().split(":")[0]); j--)
            list.set(j,(CourseElement)list.get(j - 1));
        list.set(j,temp);
    }
	
	return list;
}
}
