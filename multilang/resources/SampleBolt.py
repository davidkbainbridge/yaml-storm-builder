import storm
import json
import sys

class SampleBolt(storm.BasicBolt):
    
    def initialize(self, stormconf, context):
        j = json.loads(sys.argv[1]);
        storm.log(json.dumps(j))
        storm.log(json.dumps(context))
        
    def process(self, tup):
        storm.log("Processing tuple in Python")
        storm.log("Start With %d" % tup.values[0])
        storm.emit([tup.values[0] * 2])
        
    def declareOutputFields(self, properties):
        j = json.loads(properties);
        sys.stdout.write(json.dumps([j["outputTag"]]))

if __name__ == "__main__":
    SampleBolt().run()


