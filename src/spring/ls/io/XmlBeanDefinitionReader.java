package spring.ls.io;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.bcel.internal.generic.DDIV;

import spring.ls.beans.BeanDefinitionReader;
import spring.ls.beans.factory.config.BeanDefinitionHolder;
import spring.ls.beans.factory.support.BeanDefinitionRegistry;
import spring.ls.beans.factory.support.BeanDefinitionRegistryUtils;
import spring.ls.beans.factory.xml.NamespaceHandlerResolver;
import spring.ls.beans.factory.xml.XmlReaderContext;
import spring.ls.core.io.ClassPathResource;
import spring.ls.core.io.EncodedResource;
import spring.ls.core.io.Resource;
import spring.ls.util.StringUtils;

public class XmlBeanDefinitionReader implements BeanDefinitionReader{

	private BeanDefinitionRegistry beanDefinitionRegistry;
	private BeanDefinitionParserDelegate delegate;
	private NamespaceHandlerResolver namespaceHandlerResolver;
	
	public XmlBeanDefinitionReader(BeanDefinitionRegistry beanDefinitionRegistry) {
		this.beanDefinitionRegistry = beanDefinitionRegistry;
	}
	
	@Override
	public void loadBeanDefinitions(Resource resource) throws Exception {
		if(!resource.exists()){
			throw new FileNotFoundException(resource.getDescription());
		}
		
		EncodedResource encodedResource = new EncodedResource(resource,"utf-8");
		InputStream is = encodedResource.getResource().getInputStream();
		InputSource inputSource = new InputSource(is);
		inputSource.setEncoding(encodedResource.getEncoding());
		
		XmlReaderContext readerContext = new XmlReaderContext(resource, this, this.namespaceHandlerResolver);
		this.delegate = new BeanDefinitionParserDelegate(readerContext);
		
		loadBeanDefinitions(inputSource, this.delegate);
	}
	
	private void loadBeanDefinitions(InputSource inputSource, BeanDefinitionParserDelegate delegate) throws Exception{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputSource);
		loadBeanDefinitions(document.getDocumentElement(), delegate);
	}
	
	/**
	 * 解析第一级的元素，"bean"、"alias"、"import"
	 * @param root
	 * @param delegate
	 * @throws Exception
	 */
	private void loadBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) throws Exception {
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
		if( delegate.nodeNameEqual(ele, "bean")){
			processBeanDefinition(ele, delegate);
			
		}else if(delegate.nodeNameEqual(ele, "alias")){
			processAliasRegistration(ele);
			
		}else if(delegate.nodeNameEqual(ele, "import")){
			importBeanDefintionResource(ele);
			
		}else if(delegate.nodeNameEqual(ele, "beans")){
			loadBeanDefinitions(ele, delegate);
		}
	}

	private void importBeanDefintionResource(Element ele) throws Exception {
		String resourcePath = ele.getAttribute("resource");
		Resource resource = new ClassPathResource(resourcePath); 
		loadBeanDefinitions(resource);
	}

	/**
	 * 注册别名
	 * @param ele
	 * @throws Exception
	 */
	private void processAliasRegistration(Element ele) throws Exception {
		String name = ele.getAttribute("name");
		String alias = ele.getAttribute("alias");
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
			beanDefinitionRegistry.registerAlias(name, alias);
		}
	}

	/**
	 * 解析bean标签
	 * @param ele
	 * @throws ClassNotFoundException
	 */
	private void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) throws ClassNotFoundException {
		try {
			BeanDefinitionHolder bdHoler = delegate.parseBeanDefinitionElement(ele);
			
			//解析bean标签下的自定义标签
			bdHoler = delegate.decorateBeanDefinitionIfRequired(ele, bdHoler);
			
			BeanDefinitionRegistryUtils.registerBeanDefinition(bdHoler, beanDefinitionRegistry);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ClassNotFoundException(ele.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
