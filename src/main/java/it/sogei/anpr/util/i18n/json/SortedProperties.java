package it.sogei.anpr.util.i18n.json;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/*
 * Properties che mantiene l'ordine con cui sono stati aggiunti gli elementi
 * al momento di usare il metodo store()
 */
public class SortedProperties extends Properties {

	private static final long serialVersionUID = 4862022744670019101L;

	private Vector<Object> keys = new Vector<Object>();
	private transient volatile Set<java.util.Map.Entry<Object, Object>> entrySet;

	private class MyEntrySet extends AbstractSet<Map.Entry<Object,Object>> {

		@Override
		public Iterator<java.util.Map.Entry<Object, Object>> iterator() {
			return new MyIterator();
		}

		@Override
		public int size() {
			return SortedProperties.this.size();
		}

	}

	private class MyIterator implements Iterator<Map.Entry<Object, Object>> {
		private Iterator<Object> iterator;

		public MyIterator() {
			iterator = keys.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public java.util.Map.Entry<Object, Object> next() {

			String key = (String) iterator.next();

			return new Map.Entry<Object, Object>() {

				@Override
				public String getKey() {
					return key;
				}

				@Override
				public Object getValue() {
					return getProperty(key);
				}

				@Override
				public Object setValue(Object value) {
					return setProperty(key, (String)value);
				}
			};
		}
	}


	@Override
	public synchronized Object setProperty(String key, String value) {
		this.keys.add( key );
		return super.setProperty(key, value);
	}

	@Override
	public Set<java.util.Map.Entry<Object, Object>> entrySet() {
		if (entrySet==null)
			entrySet = Collections.synchronizedSet(new MyEntrySet());
		return entrySet;
	}

	@Override
	public synchronized Enumeration<Object> keys() {
		return this.keys.elements();
	}

	@Override
	public Enumeration<?> propertyNames() {
		return this.keys();
	}

}
