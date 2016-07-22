package spring.ls.beans.factory.support;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import spring.ls.beans.BeanWrapper;
import spring.ls.beans.BeanWrapperImpl;
import spring.ls.beans.BeansException;
import spring.ls.beans.TypeConverter;
import spring.ls.beans.factory.config.ConstructorArgumentValues;
import spring.ls.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import spring.ls.core.MethodParameter;
import spring.ls.core.ParameterNameDiscoverer;
import spring.ls.util.ClassUtils;
import spring.ls.util.MethodInvoker;

public class ConstructorResolver {

	private final AbstractAutowireCapableBeanFactory beanFactory;

	public ConstructorResolver(AbstractAutowireCapableBeanFactory beanFactory) {
		super();
		this.beanFactory = beanFactory;
	}

	public BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd,
			Constructor<?>[] chosenCtors, final Object[] explicitArgs) {
		
		BeanWrapperImpl bw = new BeanWrapperImpl();
		this.beanFactory.initBeanWrapper(bw);
		
		Constructor<?> constructorToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;
		
		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			//从缓存中获取解析的参数
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
				if (constructorToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached constructor...
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			if (argsToResolve != null) {
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
			}
		}
		
		if (constructorToUse == null) {
			
			ConstructorArgumentValues resolvedValues = null;
			
			int minNrOfArgs;
			if(explicitArgs != null){
				minNrOfArgs = explicitArgs.length;
			}else{
				//传入的参数为空，则使用从配置文件读取的
				ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
				//用于承载解析后的构造函数参数的值
				resolvedValues = new ConstructorArgumentValues();
				//解析到的参数个数
				minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
			}
			
			Constructor<?>[] candidates = chosenCtors;
			if(candidates == null){
				Class<?> beanClass = mbd.getBeanClass();
				candidates = mbd.isNonPublicAccessAllowed() ? beanClass.getDeclaredConstructors() : beanClass.getConstructors();
			}
			
			AutowireUtils.sortConstructors(candidates);
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Constructor<?>> ambiguousConstructors = null;//模棱两可的构造函数
			LinkedList<BeansException> causes = null;
			
			for (int i = 0; i < candidates.length; i++) {
				
				Constructor<?> candidate = candidates[i];
				Class<?>[] paramTypes = candidate.getParameterTypes();
				
				if(constructorToUse != null && argsToUse.length > paramTypes.length){
					//如果已经找到构造函数、或者需要的参数个数大于当前的构造函数参数的个数则终止，因为已经按参数个数降序排列
					break;
				}
				if( paramTypes.length < minNrOfArgs){
					//参数个数不相等
					continue;
				}
				
				ArgumentsHolder argsHolder = null;
				if(resolvedValues != null){
					
					try {
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
						if(paramNames == null){
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if(pnd != null){
								paramNames = pnd.getParameterNames(candidate);
							}
						}
						//解析参数：转为真正的类型、确定依赖注入
						argsHolder = createArgumentArray(beanName, mbd, bw, resolvedValues, paramTypes, 
								paramNames, candidate, true);
						
					} catch (Exception e) {
						if(causes == null){
							causes = new LinkedList<BeansException>();
						}
						BeansException be = new BeansException("确定构造函数错误!"+candidate);
						causes.add(be);
					}
					
				}else{
					
					if( paramTypes.length != explicitArgs.length){
						continue;
					}
					argsHolder = new ArgumentsHolder(argsToUse);
				}
				
				//获取参数匹配的权重
				//探测是否有不确定性的构造函数存在，例如不同构造函数的参数为父子关系
				int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
						argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
				// 选择构造函数
				if (typeDiffWeight < minTypeDiffWeight) {
					constructorToUse = candidate;
					argsHolderToUse = argsHolder;
					argsToUse = argsHolder.arguments;
					minTypeDiffWeight = typeDiffWeight;
					ambiguousConstructors = null;
				}
				else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
					if (ambiguousConstructors == null) {
						ambiguousConstructors = new LinkedHashSet<Constructor<?>>();
						ambiguousConstructors.add(constructorToUse);
					}
					ambiguousConstructors.add(candidate);
				}
				
			}
			
			if (constructorToUse == null) {
				if (causes != null) {
					BeansException ex = causes.removeLast();
//					for (Exception cause : causes) {
//						this.beanFactory.onSuppressedException(cause);
//					}
					throw ex;
				}
				throw new BeansException("我醉了。。。");
			}
			else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
				//如果有模棱两可的构造函数，并且是严格解决构造函数，则抛出异常
				throw new BeansException("彻底醉了"+ambiguousConstructors);
			}

			if (explicitArgs == null) {
				//缓存构造函数、构造函数的参数
				argsHolderToUse.storeCache(mbd, constructorToUse);
			}
			
		}
		
		//创建实例
		Object beanInstance = beanFactory.getInstantiationStrategy().instantiate(mbd, beanName, beanFactory, constructorToUse, argsToUse);
		bw.setWrappedInstance(beanInstance);
		
		return bw;
	}

	/**
	 * 解析准备好的构造参数的值
	 * @param beanName
	 * @param mbd
	 * @param bw
	 * @param constructorToUse
	 * @param argsToResolve
	 * @return
	 */
	private Object[] resolvePreparedArguments(String beanName, RootBeanDefinition mbd, BeanWrapperImpl bw,
			Constructor<?> constructorToUse, Object[] argsToResolve) {
		
		
		return null;
	}

	/**
	 * 解析构造函数的参数个数
	 * @param beanName
	 * @param mbd
	 * @param bw
	 * @param cargs
	 * @param resolvedValues 用于存放解析后的参数
	 * @return
	 */
	private int resolveConstructorArguments(String beanName, RootBeanDefinition mbd,BeanWrapper bw, 
			ConstructorArgumentValues cargs,ConstructorArgumentValues resolvedValues) {
		
		TypeConverter converter = (this.beanFactory.getCustomTypeConverter() != null ?
				this.beanFactory.getCustomTypeConverter() : bw);
		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(beanFactory, beanName, mbd, converter);
		cargs.getIndexedArgumentValues().entrySet();
		
		int minNrOfArgs = cargs.getArgumentCount();
		
		for(Entry<Integer, ValueHolder> entry : cargs.getIndexedArgumentValues().entrySet()){
			int index = entry.getKey();
			if(index < 0){
				throw new BeansException(beanName+"构造函数的参数index小于0");
			}
			
			if (index > minNrOfArgs) {
				minNrOfArgs = index + 1;
			}
			
			ConstructorArgumentValues.ValueHolder valueHolder = entry.getValue();
			if(valueHolder.isConverted()){
				resolvedValues.addIndexedArgumentValue(index, valueHolder);
				
			}else{
				//主要解决ref依赖
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				ConstructorArgumentValues.ValueHolder resolvedValueHolder =
						new ConstructorArgumentValues.ValueHolder(valueHolder.getName(), valueHolder.getType(), resolvedValue);
				resolvedValueHolder.setSource(valueHolder);
				resolvedValues.addIndexedArgumentValue(index, resolvedValueHolder);
			}
		}
		
		for (ConstructorArgumentValues.ValueHolder valueHolder : cargs.getGenericArgumentValues()) {
			if (valueHolder.isConverted()) {
				resolvedValues.addGenericArgumentValue(valueHolder);
			}
			else {
				//主要解决ref依赖
				Object resolvedValue =
						valueResolver.resolveValueIfNecessary("constructor argument", valueHolder.getValue());
				ConstructorArgumentValues.ValueHolder resolvedValueHolder =
						new ConstructorArgumentValues.ValueHolder(valueHolder.getName(), valueHolder.getType(), resolvedValue);
				resolvedValueHolder.setSource(valueHolder);
				resolvedValues.addGenericArgumentValue(resolvedValueHolder);
			}
		}
		
		return minNrOfArgs;
	}
	
	private ArgumentsHolder createArgumentArray(
			String beanName, RootBeanDefinition mbd, BeanWrapper bw, 
			ConstructorArgumentValues resolvedValues,Class<?>[] paramTypes, 
			String[] paramNames, Object methodOrCtor,boolean autowiring) throws BeansException {
		
		//String methodType = (methodOrCtor instanceof Constructor ? "constructor" : "factory method");
		TypeConverter converter = (this.beanFactory.getCustomTypeConverter() != null ?
				this.beanFactory.getCustomTypeConverter() : bw);
		
		ArgumentsHolder args = new ArgumentsHolder(paramTypes.length);
		Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = 
				new HashSet<ConstructorArgumentValues.ValueHolder>();
		Set<String> autowiredBeanNames = new LinkedHashSet<String>(4);
		
		for(int paramIndex = 0; paramIndex < paramTypes.length; paramIndex++){
			Class<?> paramType = paramTypes[paramIndex];
			String paramName = ( paramNames != null ? paramNames[paramIndex] : null);
			
			//获取valueHolder
			ConstructorArgumentValues.ValueHolder valueHolder = 
					resolvedValues.getArgumentValue(paramIndex, paramType, paramName, usedValueHolders);
			
			if (valueHolder == null && !autowiring) {
				valueHolder = resolvedValues.getGenericArgumentValue(null, null, usedValueHolders);
			}
			
			if(valueHolder != null){
				usedValueHolders.add(valueHolder);
				Object originalValue = valueHolder.getValue();
				Object convertedValue;
				//参数的值被转换过
				if(valueHolder.isConverted()){
					convertedValue = valueHolder.getConvertedValue();
					args.preparedArguments[paramIndex] = convertedValue;
				}else{
					//参数的值没有被转换过
					ConstructorArgumentValues.ValueHolder sourceHolder =
							(ConstructorArgumentValues.ValueHolder) valueHolder.getSource();
					Object sourceValue = sourceHolder.getValue();
					
					//转换值为真正需要的值,如将：TypedStringValue转换为int或者string
					convertedValue = converter.convertIfNecessary(originalValue, paramType,
							MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex));
					
					try {
						args.resolveNecessary = true;
						args.preparedArguments[paramIndex] = sourceValue;
						
					} catch (Exception e) {
						throw new BeansException("参数解析失败！",e);
					}
				}
				args.arguments[paramIndex] = convertedValue;
				args.rawArguments[paramIndex] = originalValue;
				
			}else{
				
				// No explicit match found: we're  suppeitherosed to autowire or
				// have to fail creating an argument array for the given constructor.
				if (!autowiring) {
					throw new BeansException("参数不能自动注入");
				}
				try {
					MethodParameter param = MethodParameter.forMethodOrConstructor(methodOrCtor, paramIndex);
					Object autowiredArgument = resolveAutowiredArgument(param, beanName, autowiredBeanNames, converter);
					args.rawArguments[paramIndex] = autowiredArgument;
					args.arguments[paramIndex] = autowiredArgument;
					args.preparedArguments[paramIndex] = new AutowiredArgumentMarker();//如果该参数需要依赖注入，则标记准备参数为AutowiredArgumentMarker
					args.resolveNecessary = true;
				}
				catch (BeansException ex) {
					throw new BeansException("参数不能自动注入",ex);
				}
			}
			
		}
		
		for (String autowiredBeanName : autowiredBeanNames) {
			//this.beanFactory.registerDependentBean(autowiredBeanName, beanName);
//			if (this.beanFactory.logger.isDebugEnabled()) {
//				this.beanFactory.logger.debug("Autowiring by type from bean name '" + beanName +
//						"' via " + methodType + " to bean named '" + autowiredBeanName + "'");
//			}
		}
		
		return args;
	}
	
	/**
	 * 解决依赖注入
	 */
	protected Object resolveAutowiredArgument(
			MethodParameter param, String beanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) {

//		return this.beanFactory.resolveDependency(
//				new DependencyDescriptor(param, true), beanName, autowiredBeanNames, typeConverter);
		return null;
	}
	
	/**
	 * 
	 * @author warhorse
	 *
	 */
	private static class ArgumentsHolder {
		
		public final Object rawArguments[];//"1"

		public final Object arguments[];//1

		public final Object preparedArguments[];//TypedStringValue

		public boolean resolveNecessary = false;
		
		public ArgumentsHolder(int size){
			this.rawArguments = new Object[size];
			this.arguments = new Object[size];
			this.preparedArguments = new Object[size];
		}
		
		public ArgumentsHolder(Object[] args) {
			this.rawArguments = args;
			this.arguments = args;
			this.preparedArguments = args;
		}
		
		/**
		 * 获取参数和构造函数的不同的权重
		 * @param paramTypes
		 * @return
		 */
		public int getTypeDifferenceWeight(Class<?>[] paramTypes) {
			// If valid arguments found, determine type difference weight.
			// Try type difference weight on both the converted arguments and
			// the raw arguments. If the raw weight is better, use it.
			// Decrease raw weight by 1024 to prefer it over equal converted weight.
			int typeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.arguments);
			int rawTypeDiffWeight = MethodInvoker.getTypeDifferenceWeight(paramTypes, this.rawArguments) - 1024;
			return (rawTypeDiffWeight < typeDiffWeight ? rawTypeDiffWeight : typeDiffWeight);
		}
		
		/**
		 * 获取参数和构造函数的不同的权重
		 * @param paramTypes
		 * @return
		 */
		public int getAssignabilityWeight(Class<?>[] paramTypes) {
			for (int i = 0; i < paramTypes.length; i++) {
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.arguments[i])) {
					return Integer.MAX_VALUE;
				}
			}
			for (int i = 0; i < paramTypes.length; i++) {
				if (!ClassUtils.isAssignableValue(paramTypes[i], this.rawArguments[i])) {
					return Integer.MAX_VALUE - 512;
				}
			}
			return Integer.MAX_VALUE - 1024;
		}
		
		/**
		 * 缓存构造函数、构造函数的参数
		 * @param mbd
		 * @param constructorOrFactoryMethod
		 */
		public void storeCache(RootBeanDefinition mbd, Object constructorOrFactoryMethod) {
			synchronized (mbd.constructorArgumentLock) {
				mbd.resolvedConstructorOrFactoryMethod = constructorOrFactoryMethod;
				mbd.constructorArgumentsResolved = true;
				if (this.resolveNecessary) {
					mbd.preparedConstructorArguments = this.preparedArguments;
				}
				else {
					mbd.resolvedConstructorArguments = this.arguments;
				}
			}
		}

	}
	
	/**
	 * 用于标记参数需要依赖注入
 	 */
	private static class AutowiredArgumentMarker {
	}
	
	/**
	 * 用于获取带有注解ConstructorProperties构造函数的参数的名称
	 * @author warhorse
	 *
	 */
	private static class ConstructorPropertiesChecker {

		public static String[] evaluate(Constructor<?> candidate, int paramCount) {
			ConstructorProperties cp = candidate.getAnnotation(ConstructorProperties.class);
			if (cp != null) {
				String[] names = cp.value();
				if (names.length != paramCount) {
					throw new IllegalStateException("Constructor annotated with @ConstructorProperties but not " +
							"corresponding to actual number of parameters (" + paramCount + "): " + candidate);
				}
				return names;
			}
			else {
				return null;
			}
		}
	}
	
}
