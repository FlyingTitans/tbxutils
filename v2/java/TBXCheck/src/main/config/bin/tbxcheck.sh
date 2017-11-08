#!/bin/sh
#
if [ -f /etc/default/amp ]
then
    . /etc/default/amp
fi

AMP_VERSION="${project.version}"

LIB="$AMP_HOME/lib"
LIBAMP="$LIB"
LIBEXT="$LIB"

JARS=""
JARS="$JARS:$LIBAMP/authzgenerator-$AMP_VERSION.jar"
JARS="$JARS:$LIBAMP/authztypes-$AMP_VERSION.jar"
JARS="$JARS:$LIBAMP/connectionpool-$AMP_VERSION.jar"
JARS="$JARS:$LIBAMP/dbconn-$AMP_VERSION.jar"
JARS="$JARS:$LIBAMP/amp-common-$AMP_VERSION.jar"

JARS="$JARS:$LIBEXT/args4j-2.0.8.jar"
JARS="$JARS:$LIBEXT/log4j-1.2.13.jar"
JARS="$JARS:$LIBEXT/antlr-runtime-3.1.3.jar"
JARS="$JARS:$LIBEXT/commons-digester-1.8.jar"
JARS="$JARS:$LIBEXT/commons-dbcp-1.2.2.jar"
JARS="$JARS:$LIBEXT/commons-pool-1.3.jar"
JARS="$JARS:$LIBEXT/commons-collections-3.2.jar"
JARS="$JARS:$LIBEXT/commons-logging-1.1.jar"
JARS="$JARS:$LIBEXT/commons-beanutils.jar"

JARS="$JARS:$LIBEXT/postgresql-8.2-505.jdbc4.jar"
JARS="$JARS:$LIBEXT/jtds-1.2.5.jar"
JARS="$JARS:$LIBEXT/ojdbc14.jar"

$AMP_HOME/jre/bin/java -classpath $JARS com.avocent.amp.authorization.generator.Main $*

