__author__ = "rodrigo"
__date__ = "$Nov 16, 2016 5:32:06 PM$"
    
import json
import optparse
import random
import requests
import socket
import sys
import time
import netifaces as ni
 
def collectCpuInfo(metricDict, cpuHistory, metricPath):
    
    procStatFile = open("/proc/stat")
    statLines = procStatFile.readlines()
    procStatFile.close()
 
 
    cpuLine = statLines[0].split()
    if not "cpu" in cpuLine[0]:
        print "collection of CPU Info failed"
        return
 
 

    user = int(cpuLine[1])
    nice = int(cpuLine[2])
    system = int(cpuLine[3])
    idle = int(cpuLine[4])
    iowait = int(cpuLine[5])
    irq = int(cpuLine[6])
    softirq = int(cpuLine[7])
 
 
    # Do we have history we can rely on, or is this the first iteration
    if cpuHistory.has_key('user'):
        curUser = user - cpuHistory['user']
        curNice = nice - cpuHistory['nice']
        curSystem = system - cpuHistory['system']
        curIdle = idle - cpuHistory['idle']
        curIowait = iowait - cpuHistory['iowait']
        curIrq = irq - cpuHistory['irq']
        curSoftirq = softirq - cpuHistory['softirq']
        totalCPU = curUser + curNice + curSystem + curIdle + curIowait + curIrq + curSoftirq
 
 
        # submit metrics to metricDict
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:user'
        m['value']= "{0}".format(int(float(curUser)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:nice'
        m['value']= "{0}".format(int(float(curNice)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:system'
        m['value']= "{0}".format(int(float(curSystem)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:idle'
        m['value']= "{0}".format(int(float(curIdle)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:iowait'
        m['value']= "{0}".format(int(float(curIowait)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:irq'
        m['value']= "{0}".format(int(float(curIrq)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|CpuUsage:softirq'
        m['value']= "{0}".format(int(float(curSoftirq)/totalCPU * 100 + .5))
        metricDict['metrics'].append(m)
 
 
    # Store current usages for comparison on next harvest cycle
    cpuHistory['user'] = user
    cpuHistory['nice'] = nice
    cpuHistory['system'] = system
    cpuHistory['idle'] = idle
    cpuHistory['iowait'] = iowait
    cpuHistory['irq'] = irq
    cpuHistory['softirq'] = softirq
 
 
 
 
def collectLoadAvg(metricDict, metricPath):
    """
    Collect data from /proc/loadavg and submit them
    as metrics for harvesting
    """
 
 
    # Harvest loadaverage stats from /proc/loadavg
    procLoadavgFile = open("/proc/loadavg")
    loadavgLines = procLoadavgFile.readlines()
    procLoadavgFile.close()
 
 
    if len(loadavgLines) < 1:
        print "collection of loadAverage statistics failed"
        return
 
 
    loadLine = loadavgLines[0].split()
    load1 = int(float(loadLine[0]) + 0.5)
    load5 = int(float(loadLine[1]) + 0.5)
    load15 = int(float(loadLine[2]) + 0.5)
 
 
    # submit metrics to metricDict
    m = {}
    m['type'] = 'IntCounter'
    m['name'] = metricPath + '|Load:load1'
    m['value']= "{0}".format(load1)
    metricDict['metrics'].append(m)
 
 
    m = {}
    m['type'] = 'IntCounter'
    m['name'] = metricPath + '|Load:load5'
    m['value']= "{0}".format(load5)
    metricDict['metrics'].append(m)
 
 
    m = {}
    m['type'] = 'IntCounter'
    m['name'] = metricPath + '|Load:load15'
    m['value']= "{0}".format(load15)
    metricDict['metrics'].append(m)
 
 
def collectMemInfo(metricDict, metricPath):
    """
    Raw conversion of all data stored in /proc/meminfo
    into metrics to be harvested
    """
 
 
    procMemFile = open("/proc/meminfo")
    memfileLines = procMemFile.readlines()
    procMemFile.close()
 
 
    if len(memfileLines) < 1:
        print "collection of loadAverage statistics failed"
        return
 
 
    # Convert memfileLines into metrics
    for l in memfileLines:
        elems = l.split()
        m = {}
        m['type'] = 'IntCounter'
        m['name'] = metricPath + '|MemInfoKB:{0}'.format(elems[0][:-1])
        m['value']= "{0}".format(elems[1])
        metricDict['metrics'].append(m)
 
 
 
 
 
 
 
 
 
 
def main(argv):
    
    ni.ifaddresses('ens3')
    ip = ni.ifaddresses('ens3')[ni.AF_INET][0]['addr']
    
    parser = optparse.OptionParser()
    parser.add_option("-v", "--verbose", help = "verbose output", dest = "verbose", default = False, action = "store_true")
    parser.add_option("-H", "--hostname", default = "localhost", help = "Orchestrator is running on", dest = "hostname")
    parser.add_option("-p", "--port", help = "Orchestrator is listening in", type = "int", default = 7070, dest = "port")
    parser.add_option("-m", "--metric_path", help = "metric path header for all metrics", dest = "metricPath", default = "linuxStats|{0}".format(ip))
    (options, args) = parser.parse_args();
 
 
    if options.verbose == True:
        print "Verbose enabled"
 

 
    # Configure URL and header for RESTful submission
    url = "http://{0}:{1}/metrics/compute".format(options.hostname,
        options.port)
    headers = {'content-type': 'application/json'}
 

 
    if options.verbose:
        print "Submitting to: {0}".format(url)
 
 
 
    submissionCount = 0
    cpuHistory = {}
 
 
    while True:
        # Metrics are collected in the metricDict dictionary.
        metricDict = {'metrics' : []}
 
        collectCpuInfo(metricDict, cpuHistory, options.metricPath)
        collectLoadAvg(metricDict, options.metricPath)
        collectMemInfo(metricDict, options.metricPath)

 
 
        #
        # convert metric Dictonary into a JSON message via the
        # json package.  Post resulting message to EPAgent RESTful
        # interface.
        #
      
        print(metricDict) 

        try:       
            r = requests.post(url, data = json.dumps(metricDict), headers = headers)
            submissionCount += 1
            print "Submitted metric: {0}".format(submissionCount)
            if options.verbose:
                print "jsonDump:"
                print json.dumps(metricDict, indent = 4)
                print "Response:"
                print r.text
                print "StatusCode: {0}".format(r.status_code)
        except:
            print "Submission Error - Without Orchestrator response"
       
        #Afer submited wait for a time    
        time.sleep(5)
 
 
if __name__ == "__main__":
        main(sys.argv)