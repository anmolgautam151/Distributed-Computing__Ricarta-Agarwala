#!/bin/bash

# Change this to your netid
netid=axg190014

# Root directory of your project
PROJDIR=/home/013/a/ax/axg190014/aos2

# Local Directory of Config
CONFIGLOCAL=/home/anmol/aos2

# Directory where the config file is located on your local system
CONFIGSERVER=$CONFIGLOCAL/config_server_online.dat
CONFIGCLIENT=$CONFIGLOCAL/config_client_online.dat

# Directory your java classes are in
BINDIR=$PROJDIR/src

# Your main project class
PROG=Main


n1=0
n2=0


#### Server
sc1=1
cc1=0
emp=0

cat $CONFIGSERVER | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
  read i
  echo "Number of Servers - " $i
  while [[ $n1 -lt $i ]]
  do
    read line
    p=$( echo $line | awk '{ print $1 }' )
    echo $p
    host=$( echo $line | awk '{ print $2 }' )
    echo $host
    port=$( echo $line | awk '{ print $3 }' )
    echo $port
	  gnome-terminal -e "bash -c 'ssh -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $sc1 $cc1 $p $host $port $emp $emp $emp $emp;$SHELL'" &
	  #gnome-terminal -e "bash -c 'ssh -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $sc1 $cc1 $p $port;$SHELL'" &
	  n1=$(( n1 + 1 ))
  done
)


#### client
sc2=0
cc2=1

cat $CONFIGCLIENT | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
  read i m1_h m2_h m3_h m1_p m2_p m3_p
  echo "Number of Clients - " $i
  while [[ $n2 -lt $i ]]
  do
    read line
    p=$( echo $line | awk '{ print $1 }' )
    echo $p
    host=$( echo $line | awk '{ print $2 }' )
    echo $host
	  gnome-terminal -e "bash -c 'ssh -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $sc2 $cc2 $p $m1_h $m1_p $m2_h $m2_p $m3_h $m3_p;$SHELL'" &
	  #gnome-terminal -e "bash -c 'ssh -o StrictHostKeyChecking=no $netid@$host java -cp $BINDIR $PROG $sc2 $cc2 $p $emp;$SHELL'" &
	  n2=$(( n2 + 1 ))
  done
)

