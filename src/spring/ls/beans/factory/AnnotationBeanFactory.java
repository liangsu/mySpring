package spring.ls.beans.factory;

import spring.ls.annotation.ScanfAnnotation;

public class AnnotationBeanFactory extends AbstractBeanFactory {
	
	ScanfAnnotation scanfAnnotation = new ScanfAnnotation(this,"spring.ls");
	
	public AnnotationBeanFactory(){
		try {
			scanfAnnotation.register();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
