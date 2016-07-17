package spring.ls.beans.factory.parsing;

public class FailFastProblemReporter implements ProblemReporter{

	@Override
	public void fatal(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	@Override
	public void error(Problem problem) {
		throw new BeanDefinitionParsingException(problem);
	}

	@Override
	public void warning(Problem problem) {
		System.out.println(problem);
		problem.getRootCause().printStackTrace();
	}

}
