package spring.ls.bean;

import spring.ls.annotation.Component;
import spring.ls.annotation.Scope;

@Component
//@Scope("singleton")
public class Dog {

	public void say(){
		System.out.println("wang wang wang!!!");
	}
}
