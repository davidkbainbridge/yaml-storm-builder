from storm import Spout, emit, log
from random import choice
from time import sleep
from uuid import uuid4
import sys
import json

class SampleSpout(Spout):

    words = [u"nathan", u"mike", u"jackson", u"golda", u"bertels"]
    
    def initialize(self, stormconf, context):
        emit(['spout initializing'])
        self.pending = {}
        j = json.loads(sys.argv[1]);
        #log(json.dumps(j))
        #log(json.dumps(context))

    def nextTuple(self):
        #log("HELLO EMIT")
        sleep(1.0/2)
        word = choice(words)
        emit([word])
        #id = str(uuid4())
        #self.pending[id] = word
        #emit([word], id=id)

    def ack(self, id):
        pass
        #del self.pending[id]

    def fail(self, id):
        log("emitting " + self.pending[id] + " on fail")
        #emit([self.pending[id]], id=id)
        
    def declareOutputFields(self, properties):
        j = json.loads(properties);
        sys.stdout.write(json.dumps([j["outputTag"]]))

if __name__ == "__main__":
    log("RUNNING");
    SampleSpout().run()


