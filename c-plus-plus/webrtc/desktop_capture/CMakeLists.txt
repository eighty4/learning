cmake_minimum_required(VERSION 3.19)
project(webrtc-desktop-capture)

set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 20)

set(PATH_TO_LIBWEB ?)

add_subdirectory(${PATH_TO_LIBWEBP} libs/libwebp)
add_subdirectory(../libwebrtc libs/libwebrtc)

add_definitions(${WEBRTC_DEFINITIONS})
add_executable(example-webrtc-desktop_capture webrtc_desktop_capture.cc)
target_include_directories(example-webrtc-desktop_capture PRIVATE ${WEBP_HEADERS} ${WEBRTC_HEADERS})
target_link_libraries(example-webrtc-desktop_capture ${WEBP_LIB} ${WEBRTC_LIBS})

if (MSVC)
    set_property(TARGET example-webrtc-desktop_capture PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} /FORCE:multiple /NODEFAULTLIB:msvcrtd")
endif ()
