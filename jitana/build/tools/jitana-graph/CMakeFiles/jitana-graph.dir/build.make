# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.12

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/Cellar/cmake/3.12.0/bin/cmake

# The command to remove a file.
RM = /usr/local/Cellar/cmake/3.12.0/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/bruno/Desktop/jitana/jitana

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/bruno/Desktop/jitana/build

# Include any dependencies generated for this target.
include tools/jitana-graph/CMakeFiles/jitana-graph.dir/depend.make

# Include the progress variables for this target.
include tools/jitana-graph/CMakeFiles/jitana-graph.dir/progress.make

# Include the compile flags for this target's objects.
include tools/jitana-graph/CMakeFiles/jitana-graph.dir/flags.make

tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.o: tools/jitana-graph/CMakeFiles/jitana-graph.dir/flags.make
tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.o: /Users/bruno/Desktop/jitana/jitana/tools/jitana-graph/main.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/bruno/Desktop/jitana/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.o"
	cd /Users/bruno/Desktop/jitana/build/tools/jitana-graph && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/jitana-graph.dir/main.cpp.o -c /Users/bruno/Desktop/jitana/jitana/tools/jitana-graph/main.cpp

tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/jitana-graph.dir/main.cpp.i"
	cd /Users/bruno/Desktop/jitana/build/tools/jitana-graph && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/bruno/Desktop/jitana/jitana/tools/jitana-graph/main.cpp > CMakeFiles/jitana-graph.dir/main.cpp.i

tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/jitana-graph.dir/main.cpp.s"
	cd /Users/bruno/Desktop/jitana/build/tools/jitana-graph && /Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/bruno/Desktop/jitana/jitana/tools/jitana-graph/main.cpp -o CMakeFiles/jitana-graph.dir/main.cpp.s

# Object files for target jitana-graph
jitana__graph_OBJECTS = \
"CMakeFiles/jitana-graph.dir/main.cpp.o"

# External object files for target jitana-graph
jitana__graph_EXTERNAL_OBJECTS =

tools/jitana-graph/jitana-graph: tools/jitana-graph/CMakeFiles/jitana-graph.dir/main.cpp.o
tools/jitana-graph/jitana-graph: tools/jitana-graph/CMakeFiles/jitana-graph.dir/build.make
tools/jitana-graph/jitana-graph: libjitana.a
tools/jitana-graph/jitana-graph: /usr/local/lib/libboost_system-mt.dylib
tools/jitana-graph/jitana-graph: /usr/local/lib/libboost_iostreams-mt.dylib
tools/jitana-graph/jitana-graph: /usr/local/lib/libboost_regex-mt.dylib
tools/jitana-graph/jitana-graph: tools/jitana-graph/CMakeFiles/jitana-graph.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/Users/bruno/Desktop/jitana/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX executable jitana-graph"
	cd /Users/bruno/Desktop/jitana/build/tools/jitana-graph && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/jitana-graph.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
tools/jitana-graph/CMakeFiles/jitana-graph.dir/build: tools/jitana-graph/jitana-graph

.PHONY : tools/jitana-graph/CMakeFiles/jitana-graph.dir/build

tools/jitana-graph/CMakeFiles/jitana-graph.dir/clean:
	cd /Users/bruno/Desktop/jitana/build/tools/jitana-graph && $(CMAKE_COMMAND) -P CMakeFiles/jitana-graph.dir/cmake_clean.cmake
.PHONY : tools/jitana-graph/CMakeFiles/jitana-graph.dir/clean

tools/jitana-graph/CMakeFiles/jitana-graph.dir/depend:
	cd /Users/bruno/Desktop/jitana/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/bruno/Desktop/jitana/jitana /Users/bruno/Desktop/jitana/jitana/tools/jitana-graph /Users/bruno/Desktop/jitana/build /Users/bruno/Desktop/jitana/build/tools/jitana-graph /Users/bruno/Desktop/jitana/build/tools/jitana-graph/CMakeFiles/jitana-graph.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : tools/jitana-graph/CMakeFiles/jitana-graph.dir/depend

