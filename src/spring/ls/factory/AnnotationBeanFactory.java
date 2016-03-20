package spring.ls.factory;

import spring.ls.annotation.ScanfAnnotation;

public class AnnotationBeanFactory extends AbstractBeanFactory {
	
	ScanfAnnotation scanfAnnotation = new ScanfAnnotation(this,"spring.ls");
	
	public AnnotationBeanFactory(){
		try {
			scanfAnnotation.loadBeans();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
