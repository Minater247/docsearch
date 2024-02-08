#!/usr/bin/env bash

set -e

EXTEN=txt

# We can avoid using a temporary file by using a pipe here
echo "$1 | $EXTEN | $(find "$1" -name "*.$EXTEN" | wc -l)"
