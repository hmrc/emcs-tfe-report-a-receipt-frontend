#!/bin/bash

sbt "run 8313 -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes"