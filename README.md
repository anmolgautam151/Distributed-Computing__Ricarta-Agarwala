# Distributed-Computing__Ricarta-Agarwala


1. There are three servers in the system, numbered from zero to two.
2. There are five clients in the system, numbered from zero to four.
3. Assume that each file is replicated on all the servers, and all replicas of a file are consistent in the beginning. To host files, create separate directory for each server.
4. A client can perform a READ or WRITE operation on the files.
  (a) For READ operation, one of the servers is chosen randomly by the client to read from it.
  (b) For WRITE operation, the request should be sent to all of servers and all of the replicas of the target file should be updated in order to keep them consistent.
5. READ/WRITE on a file can be performed by only one client at a time. However, different clients are allowed to concurrently perform a READ/WRITE on different files.
6. In order to ensure the mentioned conditions, you must implement Ricart-Agrawala algorithm for distributed mutual exclusion, with the optimization proposed by Roucairol and Carvalho, so that no READ/WRITE violation
could occur. The operations on files can be seen as critical section executions.
7. The supported operations by servers are as follows:
  (a) ENQUIRY: A request from a client for information about the list of hosted files.
  (b) READ: A request to read last line from a given file.
  (c) WRITE: A request to append a string to a given file.
The servers must reply to the clients with appropriate messages after receiving each request.
8. Assume that the set of file does not change during the program’s execution. Also, assume that no server failure occurs during the execution of the program.
9. The client must be able to do the following:
  (a) Gather information about the hosted files bu querying the servers and keep the metadata for future.
  (b) Append a string hclient id, timestampi to a file fi during WRITE operation. Timestamp is the value of
the clients, local clock when the WRITE request is generated. This must be done to all replicas of fi.
  (c) Read last line of a file fi during READ.
10. Write an application that periodically generates READ/WRITE requests for a randomly chosen file from the setof files stored by the servers.
11. Display appropriate log messages to the console or to a file.

