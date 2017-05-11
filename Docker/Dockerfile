FROM library/ubuntu

# Set the working directory to /app
WORKDIR /app/

# Copy the current directory into /app/ (except files in .dockerfile)
ADD ./ /app/

# Create user and set permissions
RUN useradd executor && chmod -R 711 /app/

# Install any needed packages
RUN apt-get update && apt-get install git curl net-tools iptables g++ --assume-yes

# Set the user to run
# The masterscript must still be run by root!
# USER executor

# Run masterscript when the container launches
CMD ["./masterscript.sh"]