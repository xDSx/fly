#!/bin/bash

#
# Wrapper script needs improvement!
#
# @author Andre van Hoorn

BINDIR=$(cd "$(dirname "$0")"; pwd)/

JAVAARGS="-Dlog4j.configuration=./log4j.properties -Dkieker.monitoring.asyncBlockOnFullQueue=true"
MAINCLASSNAME=kieker.tools.logReplayer.FilesystemLogReplayerStarter
CLASSPATH=$(ls "${BINDIR}/../lib/"*.jar | tr "\n" ":")$(ls "${BINDIR}/../dist/"*.jar | tr "\n" ":")${BINDIR}

java ${JAVAARGS} -cp "${CLASSPATH}" ${MAINCLASSNAME} "$@"