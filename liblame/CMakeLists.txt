cmake_minimum_required(VERSION 3.6.0)
set(CURRENT_DIR ${CMAKE_SOURCE_DIR})
message("CURRENT_DIR:" ${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_SOURCE_DIR}src/main/cpp/lame)

set(LAME_DIR src/main/cpp/lame)
message("LAME_DIR:" ${LAME_DIR})

aux_source_directory(src/main/cpp/lame SRC_LIST)

add_library(mp3lame
        SHARED
        src/main/cpp/MP3Recorder.c
        ${SRC_LIST})

#add_library(mp3lame
#        SHARED
#        src/main/cpp/MP3Recorder.c
#        src/main/cpp/lame/bitstream.c
#        src/main/cpp/lame/fft.c
#        src/main/cpp/lame/id3tag.c
#        src/main/cpp/lame/mpglib_interface.c
#        src/main/cpp/lame/presets.c
#        src/main/cpp/lame/quantize.c
#        src/main/cpp/lame/reservoir.c
#        src/main/cpp/lame/tables.c
#        src/main/cpp/lame/util.c
#        src/main/cpp/lame/VbrTag.c
#        src/main/cpp/lame/encoder.c
#        src/main/cpp/lame/gain_analysis.c
#        src/main/cpp/lame/lame.c
#        src/main/cpp/lame/newmdct.c
#        src/main/cpp/lame/psymodel.c
#        src/main/cpp/lame/quantize_pvt.c
#        src/main/cpp/lame/set_get.c
#        src/main/cpp/lame/takehiro.c
#        src/main/cpp/lame/vbrquantize.c
#        src/main/cpp/lame/version.c)


find_library( # Sets the name of the path variable.
        log-lib
        log)

target_link_libraries(mp3lame
        ${log-lib})

