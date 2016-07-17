package spring.ls.beans.factory.parsing;

import spring.ls.beans.factory.BeanDefinitionStoreException;

@SuppressWarnings("serial")
public class BeanDefinitionParsingException extends BeanDefinitionStoreException {

	/**
	 * Create a new BeanDefinitionParsingException.
	 * @param problem the configuration problem that was detected during the parsing process
	 */
	public BeanDefinitionParsingException(Problem problem) {
		super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
	}

}