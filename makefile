# ---------------------------------------------------------
# (@) 2002-03 Jean-Michel RICHER
# ---------------------------------------------------------

AWT=sources/v20
#SWING=sources/swing21
SWING=sources/swing22

# Chose between SWING or AWT
SOURCES=${SWING}

all: make_java 
# all : make_java make_doc make_c

make_java: 
	javac -d bin -deprecation -classpath ${SOURCES} -encoding ISO-8859-1 ${SOURCES}/*.java
	jar cf bin/simlog.jar bin/*.class img/*.gif

run:
	java -classpath .:./bin SimLogWin
	
make_doc: 
		javadoc -d doc -classpath ${SOURCES}   ${SOURCES}/*.java



clean:
				rm bin/* ; \
				rm doc/* ;

