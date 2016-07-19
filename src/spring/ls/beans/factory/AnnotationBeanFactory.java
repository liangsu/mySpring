package spring.ls.beans.factory;

import spring.ls.annotation.ScanfAnnotation;
import spring.ls.beans.factory.support.AbstractAutowireCapableBeanFactory;

public class AnnotationBeanFactory extends AbstractAutowireCapableBeanFactory {
	
	ScanfAnnotation scanfAnnotation = new ScanfAnnotation(this,"spring.ls");
	
	public AnnotationBeanFactory(){
		try {
			scanfAnnotation.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
