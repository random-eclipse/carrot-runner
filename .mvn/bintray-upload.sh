#!/bin/bash

#
# publish artifact
#

base=$(git rev-parse --show-toplevel)

cd "$base"

mvn clean deploy -B -P !flatten
