/*
 * Copyright (c) 2013, Yaml Storm Authors and/or its affiliates.
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
 *   - Neither the name of Yaml Storm Authors or the names of its
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
package storm.yaml.test;

import org.junit.Assert;
import org.junit.Test;

import storm.yaml.configuration.YamlTopologyBuilder;
import backtype.storm.generated.StormTopology;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */

public class BuilderTest {
	@Test
	public void BuildTopologyTest() {
		StormTopology topo = new YamlTopologyBuilder("SampleTopo.yml")
				.createTopology();

		Assert.assertEquals(1, topo.get_spouts_size());
		Assert.assertEquals(2, topo.get_bolts_size());
		Assert.assertTrue(topo.get_spouts().containsKey("random"));
		Assert.assertTrue(topo.get_bolts().containsKey("multiply_bolt"));
		Assert.assertTrue(topo.get_bolts().containsKey("log_bolt"));
		Assert.assertEquals(1, topo.get_bolts().get("multiply_bolt")
				.get_common().get_inputs_size());
		Assert.assertEquals("random", topo.get_bolts().get("multiply_bolt")
				.get_common().get_inputs().keySet().iterator().next()
				.get_componentId());
		Assert.assertNotNull(topo.get_bolts().get("multiply_bolt").get_common()
				.get_inputs().values().iterator().next().get_shuffle());
		Assert.assertEquals(1, topo.get_bolts().get("log_bolt").get_common()
				.get_inputs_size());
		Assert.assertEquals("multiply_bolt", topo.get_bolts().get("log_bolt")
				.get_common().get_inputs().keySet().iterator().next()
				.get_componentId());
		Assert.assertNotNull(topo.get_bolts().get("log_bolt").get_common()
				.get_inputs().values().iterator().next().get_shuffle());
	}
}