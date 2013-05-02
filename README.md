yaml-storm-builder
==================

An Apache Storm topology builder that constructs the topology from a YAML
document.

This project provides a YamlTopologyBuilder class that is derived from Apache
Storm's TopologyBuilder and enables the caller to specify a YAML file from
which the topology should be built.

The specified YAML file is first looked up as a system resource
(*ClassLoader.getSystemResource*) and if it cannot be located in this way then
it is looked up as a regular file. Alternatively, the caller can also provide
an *InputStream* instance.

Below is a sample YAML file specification:

	---
	name : Sample Topology
	description : This is a sample topology definition in YAML so
	  that the topology creation is driven by a configuration file
	  as opposed to a block of Java code
	spouts :
	  - name : random
	    description: This spout will generate a random number on a specified interval
	    impl : java:org.company.storm.nodes.RandomNumberSpout
	    properties :
	      - minimumValue : 1
	        maximumValue : 100
	        generateInterval : 5s
	        outputTag : the_number
	bolts :
	  - name : multiply_bolt 
	    description: This bolt will accept a number and multiply it
	    impl : java:org.company.storm.nodes.MultiplyBolt
	    properties :
	      - mutiplier : 10
	        intputTag : the_number
	        outputTag : the_number
	    groupings :
	      - shuffle : random
	  - name : log_bolt
	    description : Logs the input number
	    impl : java:org.company.storm.nodes.LogBolt
	    properties :
	      - format : The number is {}
	        inputTag : the_number
	        outputTag : the_string
	    groupings :
	      - shuffle : multiply_bolt

**Note:** *currently on shuffle grouping is supported*