package com.inovision.apitest.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.inovision.apitest.model.Host;

public class TestHost extends junit.framework.TestCase {

	public void testSetterGetters() throws Exception {
		Host host = new Host();
		BeanMap beanMap = new BeanMap(host);
		Iterator<Entry<Object, Object>> itr = beanMap.entryIterator();
		while(itr.hasNext()) {
			Entry<Object, Object> entry = itr.next();
			//System.out.println(entry);
			System.out.println(entry.getKey() + "=" + beanMap.getType((String)entry.getKey()));//BeanUtils.getPropertyDescriptor(Host.class, (String) entry.getKey()).getPropertyType());
		}
	}
	
	private void setNullOnEmpty(Object obj) {
		BeanMap beanMap = new BeanMap(obj);
		Iterator<Entry<Object, Object>> itr = beanMap.entryIterator();
		while(itr.hasNext()) {
			Entry<Object, Object> entry = itr.next();
			String prop = (String)entry.getKey();
			Object value = entry.getValue();
			if(beanMap.getType(prop).isAssignableFrom(String.class) && (value != null)) {
				String str = (String) value;
				if(StringUtils.isEmpty(str)) {
					try {
						org.apache.commons.beanutils.BeanUtils.setProperty(obj, prop, null);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			//System.out.println(entry.getKey() + "=" + beanMap.getType((String)entry.getKey()));//BeanUtils.getPropertyDescriptor(Host.class, (String) entry.getKey()).getPropertyType());
		}
		
	}
}
