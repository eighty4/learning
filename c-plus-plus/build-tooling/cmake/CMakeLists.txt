cmake_minimum_required(VERSION 3.25)
project(cpp-cmake-build)

set(CMAKE_VERBOSE_MAKEFILE on)

add_subdirectory(libhsl)

add_executable(rgb_to_hsl rgb_to_hsl.cc)
target_link_libraries(rgb_to_hsl PRIVATE libhsl)
set_property(TARGET rgb_to_hsl PROPERTY CXX_STANDARD 20)
