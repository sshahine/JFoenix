#!/bin/bash

# Echo an error message to the user.
error () {
    echo "Error:"  $1 >&2
    exit 1
}

# Ensure java is installed and JAVA_HOME is set. Ant requires this.
if [ -z "$JAVA_HOME" ];
    then
        # IF JAVA_HOME not set, try to set it by finding where javac is.
        command -v javac >/dev/null 2>&1;
        if [ $? -eq 0 ];
            then
                # Javac was found, now set JAVA_HOME
                export JAVA_HOME=$(readlink -f $(command -v javac) | sed "s:/bin/javac::");

            else
                # Javac could not be found, exit and fail.
                error "JAVA_HOME is not set, is java installed?";
        fi
    
    # Javac cannot be found, and JAVA_HOME isn't set.
    else
        error "JAVA_HOME not set, is java installed?";
fi

# Ensure ant is installed
command -v ant >/dev/null 2>&1 || { error "ant cannot be found, is it\
    installed?"; }

# Set some arguments for ant.
export ANT_OPTS="-Xms1024m -Xmx1024m"

# Finally, build.
ant -f build.xml
