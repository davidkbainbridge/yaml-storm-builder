/*
 * Copyright (c) 2013, Yaml Storm Builder Authors  and/or its affiliates. All rights reserved.
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
 *   - Neither the name of Yaml Storm Builder Authors  or the names of its
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

import org.apache.log4j.Logger;

import storm.yaml.TopologyCheck;

/**
 * @author David Bainbridge <davidk.bainbridge@gmail.com>
 * 
 */
public class MyTopologyCheck implements TopologyCheck {

	private static final Logger log = Logger.getLogger(MyTopologyCheck.class);
	private String test1 = null;
	private String test2 = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see storm.yaml.TopologyCheck#validateTopology()
	 */
	@Override
	public boolean validateTopology() {
		log.debug(String.format("Validate topology %s =?= %s", test1, test2));
		if (test1 == null && test2 == null) {
			return true;
		} else if (test1 == null || test2 == null) {
			return false;
		} else {
			return test1.equals(test2);
		}
	}

	/**
	 * @return the test1
	 */
	public String getTest1() {
		return test1;
	}

	/**
	 * @param test1
	 *            the test1 to set
	 */
	public void setTest1(String test1) {
		this.test1 = test1;
	}

	/**
	 * @return the test2
	 */
	public String getTest2() {
		return test2;
	}

	/**
	 * @param test2
	 *            the test2 to set
	 */
	public void setTest2(String test2) {
		this.test2 = test2;
	}
}
