/*
 * Copyright (c) 2013, Zenoss and/or its affiliates. All rights reserved.
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
 *   - Neither the name of Zenoss or the names of its
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;

/**
 * @author David Bainbridge <dbainbridge@zenoss.com>
 * 
 */
public class PythonWrapperHelper {
	private static final Logger log = Logger
			.getLogger(PythonWrapperHelper.class);

	public static final void declareOutputFields(OutputFieldsDeclarer declarer,
			String source, JSONObject properties, String type) {
		Process process = null;
		try {
			log.debug(String.format(
					"Create subprocess to get output fields for %s '%s'",
					type, source));
			String[] cmd = { "python", "-u", "-B" };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			URL url = ClassLoader.getSystemResource("resources/" + source);
			if (url == null) {
				throw new RuntimeException(String.format(
						"Unable to locate Python %s for source '%s'", type,
						source));
			}
			String name = source.substring(0, source.lastIndexOf('.'));
			log.debug(String.format("Assume class name is '%s'", name));
			pb.directory(new File(url.toURI()).getParentFile());
			pb.redirectErrorStream(false);

			// Import the class and get declared output fields
			process = pb.start();
			process.getOutputStream().write(
					String.format("import %s\n", name).getBytes());
			process.getOutputStream().write(
					String.format("%s.%s().declareOutputFields('%s')\n", name,
							name, properties.toString()).getBytes());
			process.getOutputStream().flush();
			process.getOutputStream().close();
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

			// Check the error stream
			InputStream io = process.getErrorStream();
			int c = io.read();
			byte[] buf = new byte[1024];
			int cnt = 0;
			if (c > 0) {
				baos.write((byte) c);
				while ((cnt = io.read(buf)) > 0) {
					baos.write(buf, 0, cnt);
				}
				log.error(String
						.format("Fatel error attempting to retrieve declared output fields from Python %s: '%s'\n",
								type, baos.toString()));
				throw new RuntimeException(
						String.format(
								"Failed to retrieve declared output fields from Python %s",
								type));
			} else {
				// Check stdout
				io = process.getInputStream();
				c = io.read();
				if (c > 0) {
					baos.write((byte) c);
					while ((cnt = io.read(buf)) > 0) {
						baos.write(buf, 0, cnt);
					}
					JSONArray ja = (JSONArray) JSONValue.parse(baos.toString());
					List<String> fields = new ArrayList<String>();
					for (Object x : ja) {
						fields.add((String) x);
					}

					declarer.declare(new Fields(fields));
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"Failed to retrieve declared output fields from Python %s",
					type), e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(String.format(
					"Failed to retrieve declared output fields from Python %s",
					type), e);
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}
}
