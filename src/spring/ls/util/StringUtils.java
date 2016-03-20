package spring.ls.util;

public class StringUtils {

	public static boolean hasLength(String text){
		if(text != null && text.trim().length() > 0){
			return true;
		}
		return false;
	}
}
