Server to Client API calls

First of all, mood and contents are decribed by integers from 0 to 3 and the mapping of each are shown below:
    PICTURE="PI"
    VIDEO="VI"
    TEXT="TE"
    AUDIO="AU"

    FUNNY="FU"
    INSPIRED="IN"
    ROMANTIC="RO"
    EXCITED="EX"

    THUMBS_UP=0          <<< weird..
    THUMBS_DOWN=1

-- Servers --
Our Test Server: http://testcmm.herokuapp.com/
Our Deploy Server: http://changemymood.herokuapp.com/

-- GET CONTENT --
To get a content from the server, do a HTTP Get request to the following:
    http://changemymood.herokuapp.com/api/getcontent/

Parameters
    content: which is a 2 character string {"PI", "VI", "TE", "AU"}
    mood: which is a 2 character string {"FU", "IN", "RO", "EX"}

Example
    http://changemymood.herokuapp.com/api/getcontent/?content=PI&mood=FU

Return
    if successful:
        a json with a url key and url value
            e.g.  {
                    "url": "www.google.com"
										"mid": 65
                  }

    if failure:
        a json with an error key and error message value
            e.g.  {
                    "error": "empty database"
                  }

-- Rate Content --
To rate a content from the server, do a HTTP Post request to the following:
    http://changemymood.herokuapp.com/api/ratecontent/

Parameters
    mid: which is the mid of a content
    rank: please input 0 for thumbs up and 1 for thumbs down

Return
    if successful:
       a json with a success key
           e.g.  {
                   "success": "updated rank"
                 }
    if failutre:
       a json with an error key similar to getContent's error json    

