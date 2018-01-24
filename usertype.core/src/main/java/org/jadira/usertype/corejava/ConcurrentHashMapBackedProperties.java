package org.jadira.usertype.corejava;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ConcurrentHashMapBackedProperties extends Properties {

	private static final long serialVersionUID = 23632462472472L;

	private ConcurrentHashMap<Object, Object> properties;

	public ConcurrentHashMapBackedProperties(Properties properties) {
		this.properties.putAll((Map<Object, Object>) properties);
	}

	public ConcurrentHashMapBackedProperties(ConcurrentHashMapBackedProperties cloneFrom) {
		this.properties = new ConcurrentHashMap<>(cloneFrom.properties);
	}

	public ConcurrentHashMapBackedProperties() {
		super();
		properties = new ConcurrentHashMap<>();
	}

	@Override
	public Object setProperty(String key, String value) {
		return properties.put(key, value);
	}

	@Override
	public String getProperty(String key) {
		return (String) this.properties.get(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String val = getProperty(key);
		return (val == null) ? defaultValue : val;
	}

	@Override
	public Enumeration<?> propertyNames() {
		return properties.keys();
	}

	@Override
	public Set<String> stringPropertyNames() {
		ConcurrentHashMap<String, String> strProps = new ConcurrentHashMap<>();
		for (Enumeration<?> e = keys(); e.hasMoreElements();) {
			Object k = e.nextElement();
			Object v = get(k);
			if (k instanceof String && v instanceof String) {
				this.properties.put((String) k, (String) v);
			}
		}
		return strProps.keySet();
	}

	@Override
	public void load(Reader reader) throws IOException {
		try{
			super.load(reader);
			this.properties.putAll(this);
		}
		finally{
			super.clear();
		}
	}

	@Override
	public void load(InputStream inStream) throws IOException {
		try {
			super.load(inStream);
			this.properties.putAll(this);
		} finally {
			super.clear();
		}
	}

	@Override
	@Deprecated
	public void save(OutputStream out, String comments) {
		try {
			store(out, comments);
		} catch (IOException e) {
		}
	}

	@Override
	public void store(Writer writer, String comments) throws IOException {
		store((writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer), comments);
	}

	@Override
	public void store(OutputStream out, String comments) throws IOException {
		store(new BufferedWriter(new OutputStreamWriter(out)), comments);
	}

	private void store(BufferedWriter bw, String comments) throws IOException {
		if (comments != null) {
			// TODO write comments
		}
		bw.write("#" + new Date().toString());
		bw.newLine();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			String key = entry.getKey().toString();
			String val = entry.getValue().toString();
			bw.write(key + "=" + val);
			bw.newLine();
		}
		bw.flush();
	}

	@Override
	public void list(PrintStream out) {
		out.println("-- listing properties --");
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			out.println(entry.getKey().toString() + "=" + entry.getValue().toString());
		}
	}

	@Override
	public void list(PrintWriter out) {
		out.println("-- listing properties --");
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			out.println(entry.getKey().toString() + "=" + entry.getValue().toString());
		}
	}

	@Override
	public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
		try{
			super.loadFromXML(in);
			this.properties.putAll(this);
		}
		finally{
			super.clear();
		}
	}

	@Override // TODO
	public void storeToXML(OutputStream os, String comment) throws IOException {

		throw new UnsupportedOperationException("Error: This method is not supported");
	}

	@Override // TODO
	public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {

		throw new UnsupportedOperationException("Error: This method is not supported");
	}

	// Overriding Hashtable

	@Override
	public int size() {
		return properties.size();
	}

	@Override
	public boolean isEmpty() {
		return properties.isEmpty();
	}

	@Override
	public Enumeration<Object> keys() {
		return properties.keys();
	}

	@Override
	public Enumeration<Object> elements() {
		return properties.elements();
	}

	@Override
	public boolean contains(Object value) {
		return properties.contains(value);
	}

	@Override
	public boolean containsValue(Object value) {
		return properties.containsValue(value);
	}

	@Override
	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		return properties.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		return properties.put((String) key, (String) value);
	}

	@Override
	public Object remove(Object key) {
		return properties.remove(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map<? extends Object, ? extends Object> t) {
		properties.putAll((Map<Object, Object>) t);
	}

	@Override
	public void clear() {
		properties.clear();
	}

	@Override
	public String toString() {
		return properties.toString();
	}

	@Override
	public Set<Object> keySet() {
		return properties.keySet();
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		return properties.entrySet();
	}

	@Override
	public Collection<Object> values() {
		return properties.values();
	}

	@Override
	public boolean equals(Object o) {
		return properties.equals(o);
	}

	@Override
	public int hashCode() {
		return properties.hashCode();
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		return properties.getOrDefault((String) key, (String) defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super Object, ? super Object> action) {
		properties.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> function) {
		properties.replaceAll(function);
	}

	@Override
	public Object putIfAbsent(Object key, Object value) {
		return properties.putIfAbsent((String) key, (String) value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return properties.remove(key, value);
	}

	@Override
	public boolean replace(Object key, Object oldValue, Object newValue) {
		return properties.replace((String) key, (String) oldValue, (String) newValue);
	}

	@Override
	public Object replace(Object key, Object value) {
		return properties.replace((String) key, (String) value);
	}

	@Override
	public Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
		return properties.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public Object computeIfPresent(Object key,
			BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return properties.computeIfPresent(key, remappingFunction);
	}

	@Override
	public Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return properties.compute(key, remappingFunction);
	}

	@Override
	public Object merge(Object key, Object value,
			BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		return properties.merge(key, value, remappingFunction);
	}

	@Override
	public Object clone() {
		return new ConcurrentHashMapBackedProperties(this);
	}
	
	@Override // TODO
	protected void rehash() {
		throw new UnsupportedOperationException("Error: This method is not supported");
	}
}
