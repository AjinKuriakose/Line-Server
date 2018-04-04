# Line-Server

#### The System
Line-Server acts as a network-server that serves individual lines of an immutable text file over the network to clients using a REST API.

The endpoint /lines/<line_index> returns an HTTP status of 200 and the text of the requested line or an HTTP 413 status if the requested line is beyond the end of the file.

I have used Spring boot to create an in-memory Tomcat server and used JAX-RS(Jersey) to develop the service endpoint.During the  start-up process the input file is scanned and an index file is created which holds the offset(long value) from the begining of the file, for each line in the input file. As the offsets are of fixed size, we can easily fetch the offset in the input file from the index file , with out doing a linear scan on either of the files. Also I have used a simple fixed capacity LRU cache to hold the records that were accessed recently and is initiated to max-capacity during startup.

When a new request comes in with a valid Line ID, the index file is scanned to get the offset corresponding to the line in the input file and a RandomAccessFile object is used to seek the line corresponding to the offset.This helps avoid scanning the entire file for each request and can be considered to be having O(1) time complexity.(Except for the costs associated with opening, reading file etc).Also repeated requests or same requests coming in quick succession will be served from the cache , avoiding file seek. For an invalid request(line ID larger than the lines in the file), I have used Jersey to send back a response with status 413.

#### How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?
For files smaller than the available memory, the system should be fairly fast and stable. As the file size grows larger, the index file will also occupy larger disk space and the time taken during start up for index creation will increase linearly.For larger files, once the server starts serving requests, I believe there won't be any additional overheads(except for increased chances for cache misses) as accessing the line from the file is a constant time operation as explained previously.
#### How will your system perform with 100 users? 10000 users? 1000000 users?
I have used the embedded Tomcat server in Spring boot and have not altered the max-threads value.Hence the system will be able to support multiple simultaneous clients.Beyond the default number of threads (200),as specified in the server configuration, the requests to the server will be queued and hence delayed.Since I am not using a persistent data storage, very large number of users(i.e higher load) will result in fewer cache hits and larger number of I/O operations resulting in poor performance. 
This can be increased 
#### What documentation , websites, papers, etc did you consult in doing this assignment?
#### Third party libraries and tools used
#### How long did you spend on this exercise? If you had unlimited more time tospend on this, how would you spend it and how would you prioritize each item?
#### If you were to critique your code, what would you have to say about it?
