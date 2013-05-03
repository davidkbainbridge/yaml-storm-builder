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
package storm.yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import storm.yaml.configuration.BoltSpecification;
import storm.yaml.configuration.SpoutSpecification;
import storm.yaml.configuration.TopologySpecification;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;

/**
 * Apache Storm TopologyBuilder that constructs the topology from a YAML file
 * that specifies the topology components and their connections.
 * 
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 */
public class YamlTopologyBuilder extends TopologyBuilder {
	private static final Logger log = LoggerFactory
			.getLogger(TopologyBuilder.class);

	private final String yamlSpecification;
	private final InputStream yamlStream;
	private TopologySpecification specification = null;
	private boolean realized = false;

	/**
	 * Constructs a topology builder that will construct the topology based on
	 * the specified system resource or file
	 * 
	 * @param yamlSpecification
	 *            specifies a system resource or file name that is the YAML file
	 *            definition of the topology
	 */
	public YamlTopologyBuilder(String yamlSpecification) {
		this.yamlSpecification = yamlSpecification;
		this.yamlStream = null;
	}

	/**
	 * Constructs a topology builder that will construct the topology based on
	 * the specification in the YAML InputStream
	 * 
	 * @param yamlStream
	 *            specifies an {@see InputStream} that contains the YAML
	 *            definition of the topology
	 */
	public YamlTopologyBuilder(InputStream yamlStream) {
		this.yamlStream = yamlStream;
		this.yamlSpecification = null;
	}

	/**
	 * Returns either the input stream given at construction or an input stream
	 * found by looking up the YAML specification reference as a system resource
	 * or as a local file
	 * 
	 * @return the input stream to use to load the YAML configuration
	 */
	private InputStream findInputStream() {

		if (yamlStream != null) {
			return yamlStream;
		}

		InputStream is = null;

		// First attempt to see if it is a resource file
		is = ClassLoader.getSystemResourceAsStream(yamlSpecification);
		if (is == null) {
			// Try it as a simple file
			try {
				is = new FileInputStream(yamlSpecification);
			} catch (FileNotFoundException e) {
				log.warn("Unable to find topology specification named '{}'",
						yamlSpecification);
				// Ignore, not found is valid
			}
		}

		return is;
	}

	/**
	 * Constructs the topology based on the YAML topology specification
	 */
	public void realizeTopology() {
		if (isRealized()) {
			return;
		}
		
		InputStream is = null;
		try {
			is = findInputStream();
			Yaml yaml = new Yaml();
			specification = yaml.loadAs(is,
					TopologySpecification.class);

			setRealized(true);
			// Now that we have the yaml parse we can walk the structure and
			// build a topology based on that.
			for (SpoutSpecification spec : specification.getSpouts()) {
				IRichSpout spout = spec.create();
				setSpout(spec.getName(), spout);
			}

			// Now build all the bolts
			for (BoltSpecification spec : specification.getBolts()) {
				IRichBolt bolt = spec.create();
				BoltDeclarer decl = setBolt(spec.getName(), bolt);
				for (Map<String, Object> grouping : spec.getGroupings()) {
					if (grouping.containsKey("shuffle")) {
						if (grouping.containsKey("stream")) {
							decl.shuffleGrouping(
									(String) grouping.get("shuffle"),
									(String) grouping.get("stream"));
						} else {
							decl.shuffleGrouping((String) grouping
									.get("shuffle"));
						}
					}
				}
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Unable to close topology specification", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.topology.TopologyBuilder#createTopology()
	 */
	@Override
	public StormTopology createTopology() {
		// Initialize the topology from the yaml specification
		realizeTopology();

		return super.createTopology();
	}

	/**
	 * @return the realized
	 */
	public boolean isRealized() {
		return realized;
	}

	/**
	 * @param realized the realized to set
	 */
	public void setRealized(boolean realized) {
		this.realized = realized;
	}
	
	public String getName() {
		if (!isRealized()) {
			throw new IllegalStateException("Topology is not realized");
		}
		return specification.getName();
	}
	
	public String getDescription() {
		if (!isRealized()) {
			throw new IllegalStateException("Topology is not realized");
		}
		return specification.getDescription();
	}
}
