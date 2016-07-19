package spring.ls.beans.factory.config;

import spring.ls.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory{

	int AUTOWIRE_NO = 0;

	int AUTOWIRE_BY_NAME = 1;

	int AUTOWIRE_BY_TYPE = 2;

	int AUTOWIRE_CONSTRUCTOR = 3;

	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;
}
