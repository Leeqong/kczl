package com.njut.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.njut.data.CommentElement;

public class CommentComparator implements Comparator<CommentElement>{

	@Override
	public int compare(CommentElement lhs, CommentElement rhs) {
		// TODO Auto-generated method stub
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date datelhs = null;
		Date daterhs = null;
		try {
			datelhs = sf.parse(lhs.getTimestap());
			daterhs = sf.parse(rhs.getTimestap());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(datelhs.before(daterhs)) return 1;
		if(datelhs.after(daterhs)) return -1;
		return 0;
	}

}
