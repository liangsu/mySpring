package spring.ls.beans;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.regex.Pattern;

import spring.ls.beans.propertyeditors.ByteArrayPropertyEditor;
import spring.ls.beans.propertyeditors.CharArrayPropertyEditor;
import spring.ls.beans.propertyeditors.CharacterEditor;
import spring.ls.beans.propertyeditors.CharsetEditor;
import spring.ls.beans.propertyeditors.ClassArrayEditor;
import spring.ls.beans.propertyeditors.ClassEditor;
import spring.ls.beans.propertyeditors.CurrencyEditor;
import spring.ls.beans.propertyeditors.CustomBooleanEditor;
import spring.ls.beans.propertyeditors.CustomCollectionEditor;
import spring.ls.beans.propertyeditors.CustomMapEditor;
import spring.ls.beans.propertyeditors.CustomNumberEditor;
import spring.ls.beans.propertyeditors.PatternEditor;
import spring.ls.beans.propertyeditors.PropertiesEditor;
import spring.ls.beans.propertyeditors.StringArrayPropertyEditor;
import spring.ls.beans.propertyeditors.TimeZoneEditor;
import spring.ls.beans.propertyeditors.ZoneIdEditor;
import spring.ls.util.ClassUtils;

public class PropertyEditorRegistrySupport implements PropertyEditorRegistry{

	private static Class<?> zoneIdClass;

	static {
		try {
			zoneIdClass = ClassUtils.forName("java.time.ZoneId", PropertyEditorRegistrySupport.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			// Java 8 ZoneId class not available
			zoneIdClass = null;
		}
	}
	
	private boolean defaultEditorsActive = false;
	
	private boolean configValueEditorsActive = false;
	
	private Map<Class<?>, PropertyEditor> defaultEditors;

	private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;
	
	private Map<Class<?>, PropertyEditor> customEditors;
	
	private Map<String, CustomEditorHolder> customEditorsForPath;
	
	private Map<Class<?>, PropertyEditor> customEditorCache;
	
	private void createDefaultEditors() {
		this.defaultEditors = new HashMap<Class<?>, PropertyEditor>(64);

		// Simple editors, without parameterization capabilities.
		// The JDK does not contain a default editor for any of these target types.
		this.defaultEditors.put(Charset.class, new CharsetEditor());
		this.defaultEditors.put(Class.class, new ClassEditor());
		this.defaultEditors.put(Class[].class, new ClassArrayEditor());
		this.defaultEditors.put(Currency.class, new CurrencyEditor());
		//this.defaultEditors.put(File.class, new FileEditor());
//		this.defaultEditors.put(InputStream.class, new InputStreamEditor());
//		this.defaultEditors.put(InputSource.class, new InputSourceEditor());
//		this.defaultEditors.put(Locale.class, new LocaleEditor());
		this.defaultEditors.put(Pattern.class, new PatternEditor());
		this.defaultEditors.put(Properties.class, new PropertiesEditor());
//		this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
		this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
//		this.defaultEditors.put(URI.class, new URIEditor());
//		this.defaultEditors.put(URL.class, new URLEditor());
//		this.defaultEditors.put(UUID.class, new UUIDEditor());
		if (zoneIdClass != null) {
			this.defaultEditors.put(zoneIdClass, new ZoneIdEditor());
		}

		// Default instances of collection editors.
		// Can be overridden by registering custom instances of those as custom editors.
		this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
		this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
		this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
		this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));

		// Default editors for primitive arrays.
		this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
		this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());

		// The JDK does not contain a default editor for char!
		this.defaultEditors.put(char.class, new CharacterEditor(false));
		this.defaultEditors.put(Character.class, new CharacterEditor(true));

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
		this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

		// Only register config value editors if explicitly requested.
		if (this.configValueEditorsActive) {
			StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
			this.defaultEditors.put(String[].class, sae);
			this.defaultEditors.put(short[].class, sae);
			this.defaultEditors.put(int[].class, sae);
			this.defaultEditors.put(long[].class, sae);
		}
		
	}
	
	@Override
	public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
		registerCustomEditor(requiredType, null, propertyEditor);
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {
		if( requiredType == null && propertyEditor == null){
			throw new IllegalArgumentException("Either requiredType or propertyPath is required");
		}
		
		if(propertyPath != null){
			if(this.customEditorsForPath == null){
				this.customEditorsForPath = new HashMap<String, CustomEditorHolder>();
			}
			this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
			
		}else{
			if(this.customEditors == null){
				this.customEditors = new HashMap<Class<?>, PropertyEditor>();
			}
			this.customEditors.put(requiredType, propertyEditor);
			this.customEditorCache = null;
		}
	}

	@Override
	public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
		Class<?> requiredTypeToUse = requiredType;
		if(propertyPath != null){
			CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyPath);
			if(editorHolder != null){
				return editorHolder.getPropertyEditor();
			}
		}
		
		return getCustomEditor(requiredTypeToUse);
	}
	
	public PropertyEditor getDefaultEditor(Class<?> requiredType){
		if( !this.defaultEditorsActive){
			return null;
		}
		PropertyEditor editor = null;
		if(this.overriddenDefaultEditors != null){
			editor = this.overriddenDefaultEditors.get(requiredType);
		}
		if(this.defaultEditors == null){
			createDefaultEditors();
		}
		
		return this.defaultEditors.get(requiredType);
	}
	
	

	private PropertyEditor getCustomEditor(Class<?> requiredType){
		if(requiredType == null || this.customEditors == null){
			return null;
		}
		
		PropertyEditor editor = this.customEditors.get(requiredType);
		if(editor == null){
			
			if(this.customEditorCache != null){
				editor = this.customEditorCache.get(requiredType);
			}
			if(editor == null){
				for( Iterator<Class<?>> it = this.customEditors.keySet().iterator(); it.hasNext() && editor == null; ){
					Class<?> key = it.next();
					
					//判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口。
					//如果是则返回 true；否则返回 false。如果该 Class 表示一个基本类型，且指定的 Class 参数正是该 Class 对象，则该方法返回 true；否则返回 false。 
					//特别地，通过身份转换或扩展引用转换，此方法能测试指定 Class 参数所表示的类型能否转换为此 Class 对象所表示的类型。
					//有关详细信息，请参阅 Java Language Specification 的第 5.1.1 和 5.1.4 节。 
					if(key.isAssignableFrom(requiredType)){//
						editor = this.customEditors.get(key);
						//缓存起来，方便下次快速找到
						if(this.customEditorCache == null){
							this.customEditorCache = new HashMap<>();
						}
						this.customEditorCache.put(requiredType, editor);
					}
				}
			}
		}
		
		return editor;
	}

	
	private static class CustomEditorHolder{
		
		private PropertyEditor propertyEditor;

		private Class<?> requiredType;
		
		public CustomEditorHolder(PropertyEditor propertyEditor, Class<?> requiredType) {
			this.propertyEditor = propertyEditor;
			this.requiredType = requiredType;
		}

		public PropertyEditor getPropertyEditor() {
			return propertyEditor;
		}

		public Class<?> getRequiredType() {
			return requiredType;
		}
		
	}
}
