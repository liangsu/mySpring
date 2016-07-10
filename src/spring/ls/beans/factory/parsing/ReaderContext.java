package spring.ls.beans.factory.parsing;

import spring.ls.core.io.Resource;

/**
 * 文件读取环境
 * 其中包含了读取文件的：文件资源、 解析监听器、解析记录器、 
 * @author warhorse
 *
 */
public class ReaderContext {

	private final Resource resource;

	public ReaderContext(Resource resource) {
		super();
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

}
