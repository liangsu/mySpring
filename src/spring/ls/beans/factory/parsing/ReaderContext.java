package spring.ls.beans.factory.parsing;

import spring.ls.core.io.Location;
import spring.ls.core.io.Resource;

/**
 * 文件读取环境
 * 其中包含了读取文件的：文件资源、 解析监听器、解析记录器、 
 * @author warhorse
 *
 */
public class ReaderContext {

	private final Resource resource;
	
	private final ProblemReporter problemReporter;

	public ReaderContext(Resource resource, ProblemReporter problemReporter) {
		this.resource = resource;
		this.problemReporter = problemReporter;
	}

	public Resource getResource() {
		return resource;
	}

	public ProblemReporter getProblemReporter() {
		return problemReporter;
	}
	
	public void fetal(String message, Object source){
		fetal(message, source, null);
	}
	
	public void fetal(String message, Object source, Throwable cause){
		Location location = new Location(getResource(), source);
		problemReporter.fatal( new Problem(message, location, cause));
	}
	
	public void error(String message, Object source){
		error(message, source, null);
	}
	
	public void error(String message, Object source, Throwable cause){
		Location location = new Location(getResource(), source);
		problemReporter.error( new Problem(message, location, cause));
	}
	
	public void warning(String message, Object source){
		warning(message, source, null);
	}
	
	public void warning(String message, Object source, Throwable cause){
		Location location = new Location(getResource(), source);
		problemReporter.warning( new Problem(message, location, cause));
	}

}
