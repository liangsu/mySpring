package spring.ls.test;

import org.junit.Test;

import spring.ls.bean.SayHello;
import spring.ls.core.io.ClassPathResource;
import spring.ls.factory.BeanFactory;
import spring.ls.factory.XmlBeanFactory;

public class TestSomething {

	@Test
	public void test1(){
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.properties"));
		SayHello hello = (SayHello) factory.getBean("sayHello");
		hello.say();
	}
}
