package spring.ls.test;

import org.junit.Test;

import spring.ls.beans.factory.BeanFactory;
import spring.ls.beans.factory.XmlBeanFactory;
import spring.ls.core.io.ClassPathResource;

public class TestSomething {

	@Test
	public void test1(){
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		SayHello hello = (SayHello) factory.getBean("sayHello");
		hello.say();
	}
}
