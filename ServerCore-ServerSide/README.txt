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
            e.g. "{\"url\": \"www.google.com\"}"

    if failure:
        a json with a error key and error message value
            e.g. "{\"error\": \"empty database\"}"

