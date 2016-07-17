package spring.ls.beans;

public class BeansException extends Exception{

	public BeansException(String msg){
		super(msg);
	}
	
	public BeansException(String msg, Throwable cause){
		super(msg, cause);
	}
}
