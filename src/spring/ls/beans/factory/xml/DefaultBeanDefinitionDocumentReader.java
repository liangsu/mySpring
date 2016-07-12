package spring.ls.beans.factory.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.support.BeanDefinitionRegistryUtils;
import spring.ls.core.io.ClassPathResource;
import spring.ls.core.io.Resource;
import spring.ls.util.StringUtils;

public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader{

	private final String BEAN_ELEMENT = "bean";
	private final String IMPORT_ELEMENT = "import";
	private final String RESOURCE_ATTRIBUTE = "resource";
	private final String ALIAS_ELEMENT = "alias";
	private final String NAME_ATTRIBUTE = "name";
	private final String ALIAS_ATTRIBUTE = "alias";
	private final String BEANS_ELEMENT = "beans";
	
	private BeanDefinitionParserDelegate delegate;
	
	private XmlReaderContext readerContext;
	
	@Override
	public void registerBeanDefinitions(Document document, XmlReaderContext readerContext) throws Exception {
		this.readerContext = readerContext;
		doRegisterBeanDefinitions(document.getDocumentElement());
	}
	
	protected void doRegisterBeanDefinitions(Element root) throws Exception{
		this.delegate = new BeanDefinitionParserDelegate(readerContext);
		
		parseBeanDefinitions(root, delegate);
	}
	
	/**
	 * 解析第一级的元素，"bean"、"alias"、"import"
	 * @param root
	 * @param delegate
	 * @throws Exception
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) throws Exception{
		if( delegate.isDefaultNameSpace(root)){
			NodeList nodeList = root.getChildNodes();
			for(int i  = 0; i < nodeList.getLength(); i++){
				Node node = nodeList.item(i);
				if(node instanceof Element){
					Element ele = (Element) node;
					if( delegate.isDefaultNameSpace(node)){
						parseDefaultElement(ele, delegate);
					}else{
						delegate.parseCustomElement(ele);
					}
				}
			}
			
		}else{
			
			delegate.parseCustomElement(root);
		}
	}

	/**
	 * 默认标签的解析
	 * @param ele
	 * @param del
	 * @throws Exception
	 */
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) throws Exception {
		if( delegate.nodeNameEqual(ele, BEAN_ELEMENT)){
			processBeanDefinition(ele, delegate);
			
		}else if(delegate.nodeNameEqual(ele, ALIAS_ELEMENT)){
			processAliasRegistration(ele);
			
		}else if(delegate.nodeNameEqual(ele, IMPORT_ELEMENT)){
			importBeanDefintionResource(ele);
			
		}else if(delegate.nodeNameEqual(ele, BEANS_ELEMENT)){
			parseBeanDefinitions(ele, delegate);
		}
	}
	
	/**
	 * import标签解析
	 * @param ele
	 * @throws Exception
	 */
	protected void importBeanDefintionResource(Element ele) throws Exception {
		String resourcePath = ele.getAttribute(RESOURCE_ATTRIBUTE);
		Resource resource = new ClassPathResource(resourcePath); 
		getReaderContext().getReader().loadBeanDefinitions(resource);
	}

	/**
	 * 注册别名
	 * @param ele
	 * @throws Exception
	 */
	protected void processAliasRegistration(Element ele) throws Exception {
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		
		if( !StringUtils.hasLength(name)){
			valid = false;
			//throw new Exception("名称不能为空");
		}
		
		if( !StringUtils.hasLength(alias)){
			//throw new Exception("别名不能为空");
			valid = false;
		}
		
		if(valid){
			getReaderContext().getRegistry().registerAlias(name, alias);
		}
	}

	/**
	 * 解析bean标签
	 * @param ele
	 * @throws Exception 
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) throws Exception {
		try {
			//解析bean标签
			BeanDefinitionHolder bdHoler = delegate.parseBeanDefinitionElement(ele);
			
			//解析bean标签下的自定义标签
			bdHoler = delegate.decorateBeanDefinitionIfRequired(ele, bdHoler);
			
			//注册beanDefinition
			BeanDefinitionRegistryUtils.registerBeanDefinition(bdHoler, getReaderContext().getRegistry());
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException(ele.toString());
		} catch (Exception e) {
			throw e;
		}
	}
	
	protected XmlReaderContext getReaderContext() {
		return readerContext;
	}
}
