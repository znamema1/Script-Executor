#!/bin/bash
#
# A simple script for executing custom scripts
#	inside of an isolated system.
# Return codes:
#	0	Script succeeded, sub-script was executed and output has been sent
#	1	Repository clone failed
#	2	Commit checkout failed
#	3	Script file permission change failed (does not exist?)
#	4	Input data download failed
#	5	Repository script execution failed
#	6	Output data upload failed
#	7	Invalid parameter passed
#	8	Directory operation failed (cd, mkdir)
#

# VARIABLES

# Folders
ROOT_DIR="$(pwd)/${0%/*}"
REPO_DIR="$ROOT_DIR/repo"
IO_DIR="$ROOT_DIR/io"

# Files
IN_FILE="$IO_DIR/data"
OUT_FILE="$IO_DIR/out"
ERR_FILE="$IO_DIR/err"

# I/O URLs
BASE_URL="http://10.0.2.15:8080/Executor-1.0/docker"
URL_INPUT="$BASE_URL/getinput/$4"
URL_OUTPUT="$BASE_URL/postoutput/$4"

# In case we are elsewhere...
cd "$ROOT_DIR" || exit 8

# Print arguments

# Parameter check
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
	exit 7
fi

# Create I/O directory
mkdir -p "$IO_DIR" || exit 8

# Clone repository
git clone -q "$1" "$REPO_DIR" || exit 1

# Checkout commit
git --git-dir="$REPO_DIR/.git" --work-tree="$REPO_DIR/" checkout -q "$2" || exit 2

# Change access rights to script
chmod 777 "$REPO_DIR/$3" || exit 3

# Download input data
curl -so "$IN_FILE" "$URL_INPUT" -f || exit 4

# Add last newline into input
echo "" >> "$IN_FILE"

cd "$REPO_DIR" || exit 8
# Execute script
"./$3" <"$IN_FILE" 1>"$OUT_FILE" 2>"$ERR_FILE" || exit 5
cd "$ROOT_DIR" || exit 8

# Upload output data
curl -sX POST "$URL_OUTPUT" -d "@$OUT_FILE" -o "/dev/null" --header "Content-Type: text/plain" -f || exit 6

exit 0