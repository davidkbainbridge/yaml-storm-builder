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
package storm.yaml.configuration.loaders.python;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import storm.yaml.configuration.NodeFactory;
import backtype.storm.topology.IRichBolt;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */
public class BoltFactory extends NodeFactory<IRichBolt> {

	private static final Logger log = Logger.getLogger(SpoutFactory.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see storm.yaml.configuration.NodeFactory#create(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IRichBolt create(String implClass,
			List<Map<String, Object>> properties) {
		log.debug(String.format("Creating Python wrapper for class %s",
				implClass));
		JSONObject jproperties = new JSONObject();
		for (Map<String, Object> property : properties) {
			for (Entry<String, Object> term : property.entrySet()) {
				jproperties.put(term.getKey(), term.getValue());
			}
		}
		return new PythonBoltWrapper(implClass, jproperties);
	}
}
