Server to Client API calls

First of all, mood and contents are decribed by integers from 0 to 3 and the mapping of each are shown below:
    PICTURE=0
    VIDEO=1
    TEXT=2
    MUSIC=3

    HUMOROUS=0
    ENERVATE=1
    ROMANTIC=2
    INSPIRE=3

    THUMBS_UP=0          <<< weird..
    THUMBS_DOWN=1

-- GET CONTENT --
To get a content from the server, do a HTTP Get request to the following:
    http://changemymood.herokuapp.com/api/getcontent/

Parameters
    content: which is an integer between 0 to 3
    mood: which is also an integer between 0 to 3

Example
    http://changemymood.herokuapp.com/api/getcontent/?content=0&mood=0

Return
    if successful:
        a json with a url key and url value
            e.g.  {
                    "url": "www.google.com"
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

