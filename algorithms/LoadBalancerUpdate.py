''' 

Created on 20/02/2017 

 

@author: morei 

''' 

import requests 

if __name__ == '__main__': 
    requests.put('https://127.0.0.1:5061/http_rpc/dispatcher/dispatcher.reload?=arg=dispatcher.reload', verify=False) 
    print("Python: Orchestrator has been Updated...") 
