package spring.ls.beans.factory;

import spring.ls.beans.BeansException;

public interface ObjectFactory<T> {

	T getObject() throws BeansException;
}
