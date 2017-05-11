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
#	9	Missing root privileges
#	10	Firewall setup failed
#

# Root privileges check
if [ "$(id -u)" -ne 0 ]; then
	exit 9
fi

# VARIABLES
RUN_USR="executor"

# Folders
APP_DIR="$(pwd)/${0%/*}"
REPO_DIR="$APP_DIR/repo"
IO_DIR="$APP_DIR/io"

# Files
IN_FILE="$IO_DIR/data"
OUT_FILE="$IO_DIR/out"
ERR_FILE="$IO_DIR/err"

# I/O URLs
HOST="172.17.0.1"
PORT="8080"
BASE_URL="http://$HOST:$PORT/Executor-1.0/docker"
URL_INPUT="$BASE_URL/getinput/$4"
URL_OUTPUT="$BASE_URL/postoutput/$4"

CURL_TIMEOUT="5"

# IPtables
IPT_ALLOW="$APP_DIR/iptables_allow.rules"
IPT_DENY="$APP_DIR/iptables_deny.rules"

# In case we are elsewhere...
cd "$APP_DIR" || exit 8

# Parameter check
if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
	exit 7
fi

# Create I/O directory
mkdir -p "$IO_DIR" || exit 8

# Clone repository
git clone -q "$1" "$REPO_DIR" || exit 1

# Checkout commit
git --git-dir="$REPO_DIR/.git" --work-tree="$REPO_DIR/" checkout -qf "$2" || exit 2

# Change access rights to script
chmod u+x "$REPO_DIR/$3" || exit 3

# Download input data
curl -sfo "$IN_FILE" --connect-timeout "$CURL_TIMEOUT" "$URL_INPUT" || exit 4

# Add last newline into input
echo "" >> "$IN_FILE"


# Save network rules
iptables -L INPUT >"/dev/null"
iptables-save > "$IPT_ALLOW" || exit 10
# Disable networking
iptables-restore < "$IPT_DENY" || exit 10

# Grant ownership for repo and io folders
chown -R "$RUN_USR":"$RUN_USR" "$REPO_DIR" "$IO_DIR"

cd "$REPO_DIR" || exit 8
# Execute script
su -c "\"./$3\" <\"$IN_FILE\" 1>\"$OUT_FILE\" 2>\"$ERR_FILE\"" "$RUN_USR" || exit 5
cd "$APP_DIR" || exit 8

# Enable networking
iptables-restore < "$IPT_ALLOW" || exit 10

# Upload output data
curl -sfX POST "$URL_OUTPUT" --data-binary "@$OUT_FILE" -o "/dev/null" --connect-timeout "$CURL_TIMEOUT" --header "Content-Type: text/plain" || exit 6

exit 0
