package spring.ls.test;

import spring.ls.beans.Dog;
import spring.ls.beans.SayHello;
import spring.ls.beans.factory.ApplicationContext;
import spring.ls.beans.factory.BeanFactory;

public class MainClass {

	public static void main(String[] args) {
		BeanFactory factory = new ApplicationContext("beans.xml");
		SayHello sayHello = (SayHello) factory.getBean("sayHello");
		sayHello.say();
		Dog dog = (Dog) factory.getBean("dog");
		dog.say();
		Dog dog2 = (Dog) factory.getBean("dog");
		System.out.println(dog);
		System.out.println(dog2);
	}
}
