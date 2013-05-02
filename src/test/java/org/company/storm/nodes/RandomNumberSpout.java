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
package org.company.storm.nodes;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */
public class RandomNumberSpout extends BaseRichSpout {

	private static final long serialVersionUID = -186431056138307235L;

	private static final Logger log = LoggerFactory
			.getLogger(RandomNumberSpout.class);

	private int minimumValue = 0;
	private int maximumValue = 100;
	private String generateInterval = "5s";
	private String outputTag = "number";

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.spout.ISpout#open(java.util.Map,
	 * backtype.storm.task.TopologyContext,
	 * backtype.storm.spout.SpoutOutputCollector)
	 */
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.spout.ISpout#nextTuple()
	 */
	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * backtype.storm.topology.IComponent#declareOutputFields(backtype.storm
	 * .topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the minimumValue
	 */
	public int getMinimumValue() {
		return minimumValue;
	}

	/**
	 * @param minimumValue
	 *            the minimumValue to set
	 */
	public void setMinimumValue(int minimumValue) {
		this.minimumValue = minimumValue;
	}

	/**
	 * @return the maximumValue
	 */
	public int getMaximumValue() {
		return maximumValue;
	}

	/**
	 * @param maximumValue
	 *            the maximumValue to set
	 */
	public void setMaximumValue(int maximumValue) {
		this.maximumValue = maximumValue;
	}

	/**
	 * @return the generateInterval
	 */
	public String getGenerateInterval() {
		return generateInterval;
	}

	/**
	 * @param generateInterval
	 *            the generateInterval to set
	 */
	public void setGenerateInterval(String generateInterval) {
		this.generateInterval = generateInterval;
	}

	/**
	 * @return the outputTag
	 */
	public String getOutputTag() {
		return outputTag;
	}

	/**
	 * @param outputTag
	 *            the outputTag to set
	 */
	public void setOutputTag(String outputTag) {
		this.outputTag = outputTag;
	}

}
