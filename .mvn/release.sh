#!/bin/bash

#
# perform release
#

base=${BASH_SOURCE%/*}

$base/github-squash.sh

$base/bintray-upload.sh
