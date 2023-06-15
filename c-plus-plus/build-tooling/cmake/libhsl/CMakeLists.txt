cmake_minimum_required(VERSION 3.19)
project(libhsl)

set(CMAKE_CXX_STANDARD 20)

find_package(fmt CONFIG REQUIRED)
find_package(GTest CONFIG REQUIRED)

add_library(libhsl STATIC hsl.cc)
target_include_directories(libhsl PUBLIC ..)
target_link_libraries(
        libhsl_test
        PRIVATE
        fmt::fmt
        GTest::gtest_main
)

enable_testing()
include(GoogleTest)
add_executable(
        libhsl_test
        hsl_test.cc
        hsl.cc
)
gtest_discover_tests(libhsl_test)
