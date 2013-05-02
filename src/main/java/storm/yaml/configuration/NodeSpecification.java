/*
 * Copyright (c) 2013, Yaml Storm Builder Authors and/or its affiliates.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Yaml Storm Builder or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package storm.yaml.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */
public class NodeSpecification<T> {
	public final static String LOADER_PACKAGE_LIST_PROPERTY = "storm.yaml.loader.package.list";
	public final static String DEFAULT_LOADER_PACKAGE_LIST = "storm.yaml.configuration.loaders";

	private static final Logger log = LoggerFactory
			.getLogger(NodeSpecification.class);

	private String type = null;
	private String name = null;
	private String description = null;
	private String impl = null;
	private List<Map<String, Object>> properties = null;
	private List<Map<String, Object>> groupings = null;

	@SuppressWarnings("unused")
	private NodeSpecification() {
	}

	protected NodeSpecification(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the params
	 */
	public List<Map<String, Object>> getProperties() {
		return properties;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setProperties(List<Map<String, Object>> properties) {
		this.properties = properties;
	}

	/**
	 * @return the impl
	 */
	public String getImpl() {
		return impl;
	}

	/**
	 * @param impl
	 *            the impl to set
	 */
	public void setImpl(String impl) {
		this.impl = impl;
	}

	/**
	 * @return the groupings
	 */
	public List<Map<String, Object>> getGroupings() {
		return groupings;
	}

	/**
	 * @param groupings the groupings to set
	 */
	public void setGroupings(List<Map<String, Object>> groupings) {
		this.groupings = groupings;
	}

	public T create() {
		// Examine the name and then based on the name we might create
		// a different spout implementation. If no implementation type
		// is specified then assume java
		String implType = "java";
		String implClass = null;
		int idx = -1;
		if ((idx = getImpl().indexOf(':')) == -1) {
			implClass = getImpl();
		} else {
			implClass = getImpl().substring(idx + 1);
		}

		// Walk the package name path and look for a factory to
		// construct the instance based on impl type and the impl class
		String[] packages = System.getProperty(
				NodeSpecification.LOADER_PACKAGE_LIST_PROPERTY,
				NodeSpecification.DEFAULT_LOADER_PACKAGE_LIST).split(":");
		Class<? extends NodeFactory<T>> clazz = null;
		NodeFactory<T> factory = null;
		for (String pkg : packages) {
			String className = pkg + '.' + implType + '.' + getType()
					+ "Factory";
			try {
				clazz = (Class<? extends NodeFactory<T>>) Class
						.forName(className);
				factory = clazz.newInstance();
				T inst = factory.create(implClass);
				for (Map<String, Object> property : getProperties()) {
					for (Entry<String, Object> entry : property.entrySet()) {
						try {
							BeanUtils.setProperty(inst, entry.getKey(),
									entry.getValue());
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				return inst;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
