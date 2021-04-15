#!/bin/bash

## NETID
netid=axg190014

## PROJECT DIRECTORY ON DC MACHINE
#PROJDIR=/home/013/a/ax/axg190014/aos2/src

## SERVER AND CLIENT LOCAL CONFIG FILES
#CONFIGSERVER=/home/anmol/aos2/config_server_online.dat
CONFIGCLEANUP=/home/anmol/aos2/cleanup_online.dat

n=0

cat $CONFIGCLEANUP | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
  read i 
  echo $i
  while [[ $n -lt $i ]]
  do
    read line
      host=$( echo $line | awk '{ print $1 }' )
      echo $host
      gnome-terminal -e "bash -c 'ssh -o StrictHostKeyChecking=no $netid@$host killall -u $netid;'" &
      sleep 1
      n=$(( n + 1 ))
  done
   
)


echo "Cleanup complete"
