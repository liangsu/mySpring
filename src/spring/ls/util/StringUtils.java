package spring.ls.util;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

	public static boolean hasLength(String text){
		return (text != null && text.length() > 0);
	}
	
	public static List<String> StringtoArray(String text, String split){
		String[] array = text.split(split);
		if(array != null && array.length > 0){
			return Arrays.asList(array);
		}
		return null;
	}

	public static boolean hasText(String text) {
		return (text != null && text.trim().length() > 0);
	}
}
