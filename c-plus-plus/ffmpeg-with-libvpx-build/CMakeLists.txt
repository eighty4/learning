cmake_minimum_required(VERSION 3.19)
project(ffmpeg-and-libvpx)

if (NOT DEFINED ENV{FFMPEG_WORKSPACE})
    message(FATAL_ERROR "set FFMPEG_WORKSPACE")
endif ()

set(FFMPEG_DIR $ENV{FFMPEG_WORKSPACE}/ffmpeg)
set(VPX_DIR $ENV{FFMPEG_WORKSPACE}/libvpx)

include_directories(
        ${FFMPEG_DIR}
        ${VPX_DIR}/include
)

if (WIN32)
    if (CMAKE_BUILD_TYPE MATCHES Debug OR "${CMAKE_BUILD_TYPE}" STREQUAL "")
        set(VPX_LIB_PATH ${VPX_DIR}/Win32/Release/vpxmd.lib)
    else ()
        set(VPX_LIB_PATH ${VPX_DIR}/Win32/Debug/vpx.lib)
    endif ()
    set(FFMPEG_LIBS
            ${FFMPEG_DIR}/libavcodec/libavcodec.a
            ${FFMPEG_DIR}/libavformat/libavformat.a
            ${FFMPEG_DIR}/libavutil/libavutil.a
            ${FFMPEG_DIR}/libswscale/libswscale.a
            ${VPX_LIB_PATH}
            )
elseif (APPLE)
    if (CMAKE_BUILD_TYPE MATCHES Debug OR "${CMAKE_BUILD_TYPE}" STREQUAL "")
        set(VPX_LIB_PATH ${VPX_DIR}/libvpx_g.a)
    else ()
        set(VPX_LIB_PATH ${VPX_DIR}/libvpx.a)
    endif ()
    set(LIBAV
            ${FFMPEG_DIR}/libavcodec/libavcodec.a
            ${FFMPEG_DIR}/libavformat/libavformat.a
            ${FFMPEG_DIR}/libavutil/libavutil.a
            ${FFMPEG_DIR}/libswscale/libswscale.a
            ${VPX_LIB_PATH}
            )
    find_library(APPLICATION_SERVICES ApplicationServices)
    find_library(CORE_FOUNDATION Foundation)
    find_library(CORE_SERVICES CoreServices)
    find_library(CORE_AUDIO CoreAudio)
    find_library(AUDIO_TOOLBOX AudioToolbox)
    find_library(IO_SURFACE IOSurface)
    find_library(COCOA Cocoa)
    set(FFMPEG_LIBS ${CORE_FOUNDATION} ${APPLICATION_SERVICES} ${CORE_SERVICES} ${CORE_AUDIO} ${AUDIO_TOOLBOX} ${IO_SURFACE} ${COCOA})
else ()
    message(FATAL_ERROR "this ffmpeg build not yet supported on ${CMAKE_SYSTEM_NAME}")
endif ()

# now you can link ${FFMPEG_LIBS} with a build
