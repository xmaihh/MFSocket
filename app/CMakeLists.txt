cmake_minimum_required(VERSION 3.6.0)
set(CURRENT_DIR ${CMAKE_SOURCE_DIR})
message("CURRENT_DIR:" ${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_SOURCE_DIR}src/main/cpp/inc)


add_library( sound
        SHARED
        src/main/cpp/play_sound.cpp
        src/main/cpp/common.cpp
        src/main/cpp/common_platform.cpp)

add_library( fmod
        SHARED
        IMPORTED )

set_target_properties( fmod
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libfmod.so
        )

add_library( fmodL
        SHARED
        IMPORTED )

set_target_properties( fmodL
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libfmodL.so
        )

find_library( # Sets the name of the path variable.
        log-lib
        log)

target_link_libraries( sound
        fmod
        fmodL
        ${log-lib} )