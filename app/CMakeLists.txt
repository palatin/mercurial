cmake_minimum_required(VERSION 3.4.1)
add_library(image_rotate
            SHARED
            src/main/cpp/image_rotate.cpp)
target_link_libraries(image_rotate -ljnigraphics)