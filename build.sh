#!/bin/bash

#Installing maven if not present.
sudo apt-get install maven
mvn clean package
