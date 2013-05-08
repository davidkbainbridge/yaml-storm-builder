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

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */
public class RandomNumberSpout extends BaseRichSpout {

	private static final long serialVersionUID = -186431056138307235L;

	private static final Logger log = LoggerFactory
			.getLogger(RandomNumberSpout.class);
	private long msInterval = 0;
	private long lastEmit = -1;

	private int minimumValue = 0;
	private int maximumValue = 100;
	private String generateInterval = "5s";
	private String outputTag = "number";
	private Random rand = new Random();
	private SpoutOutputCollector output = null;

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
		output = collector;
		parseGenerateInterval();
	}

	private void parseGenerateInterval() {
		Pattern p = Pattern
				.compile(
						"([0-9]+d)?\\s*([0-9]+h)?\\s*([0-9]+m)?\\s*([0-9]+s)?\\s*([0-9]+ms)?",
						Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(generateInterval);
		m.matches();
		msInterval = 0;
		String part = null;
		if ((part = m.group(1)) != null) {
			msInterval += Integer
					.parseInt(part.substring(0, part.length() - 1)) * 24 * 60 * 60;
		}
		if ((part = m.group(2)) != null) {
			msInterval += Integer
					.parseInt(part.substring(0, part.length() - 1)) * 60 * 60;
		}
		if ((part = m.group(3)) != null) {
			msInterval += Integer
					.parseInt(part.substring(0, part.length() - 1)) * 60;
		}
		if ((part = m.group(4)) != null) {
			msInterval += Integer
					.parseInt(part.substring(0, part.length() - 1));
		}
		msInterval *= 1000;
		if ((part = m.group(5)) != null) {
			msInterval += Integer
					.parseInt(part.substring(0, part.length() - 2));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see backtype.storm.spout.ISpout#nextTuple()
	 */
	@Override
	public void nextTuple() {
		if (lastEmit == -1 || new Date().getTime() - lastEmit >= msInterval) {
			log.debug("EMIT");

			output.emit(new Values(rand.nextInt(getMaximumValue()
					- getMinimumValue())
					+ getMinimumValue()));
			lastEmit = new Date().getTime();
		}
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
		declarer.declare(new Fields(getOutputTag()));
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
		parseGenerateInterval();
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
