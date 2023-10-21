cmake_minimum_required(VERSION 3.19)
project(learning-openssl)

find_package(fmt CONFIG REQUIRED)
find_package(OpenSSL REQUIRED)

set(SOURCES main.cc tls_socket.cc)
set(LIBS
        fmt::fmt
        OpenSSL::SSL
        OpenSSL::Crypto
        )

if (APPLE)
    list(APPEND SOURCES tcp_socket_posix.cc)
    find_library(APPLICATION_SERVICES ApplicationServices)
    list(APPEND LIBS ${APPLICATION_SERVICES})
elseif (WIN32)
    list(APPEND SOURCES tcp_socket_win.cc)
    list(APPEND LIBS
            wsock32
            ws2_32
            mswsock
            advapi32
            )
endif ()

add_executable(learning_openssl ${SOURCES})
target_compile_features(learning_openssl PRIVATE cxx_std_20)
target_link_libraries(learning_openssl PRIVATE ${LIBS})
target_include_directories(learning_openssl PRIVATE .)
