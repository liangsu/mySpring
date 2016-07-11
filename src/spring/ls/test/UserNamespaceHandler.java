package spring.ls.test;

import spring.ls.beans.factory.xml.NamespaceHandlerSupport;

public class UserNamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		super.registerBeanDefinitionParser("user", new UserBeanDefinitionParser());
	}

}
