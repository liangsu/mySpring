package spring.ls.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

public class AutowireUtils {

	public static void sortConstructors(Constructor<?>[] ctors){
		Arrays.sort(ctors, new Comparator<Constructor<?>>(){

			@Override
			public int compare(Constructor<?> c1, Constructor<?> c2) {
				boolean p1 = Modifier.isPublic(c1.getModifiers());
				boolean p2 = Modifier.isPublic(c2.getModifiers());
				if(p1 != p2){
					return (p1 ? -1 : 1);
				}
				int c1pl = c1.getParameterTypes().length;
				int c2pl = c2.getParameterTypes().length;
				return (c1pl < c2pl ? 1 : (c1pl > c2pl ? -1 : 0));
			}
			
		});
	}
}
