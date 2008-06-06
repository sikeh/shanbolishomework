#
# Gererated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=
CXX=
FC=

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/send/GNU-Linux-x86

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/event.o \
	${OBJECTDIR}/src/rudp.o \
	${OBJECTDIR}/src/vs_recv.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS} dist/send/GNU-Linux-x86/rudp

dist/send/GNU-Linux-x86/rudp: ${OBJECTFILES}
	${MKDIR} -p dist/send/GNU-Linux-x86
	${LINK.c} -o dist/send/GNU-Linux-x86/rudp ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/event.o: src/event.c 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.c) -g -o ${OBJECTDIR}/src/event.o src/event.c

${OBJECTDIR}/src/rudp.o: src/rudp.c 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.c) -g -o ${OBJECTDIR}/src/rudp.o src/rudp.c

${OBJECTDIR}/src/vs_recv.o: src/vs_recv.c 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.c) -g -o ${OBJECTDIR}/src/vs_recv.o src/vs_recv.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/send
	${RM} dist/send/GNU-Linux-x86/rudp

# Subprojects
.clean-subprojects:
