cmake_minimum_required(VERSION 3.10.2)


project("mehendidesign")

# লাইব্রেরি তৈরি করা হচ্ছে
add_library(
        native-lib
        SHARED
        native-lib.cpp)

# লগ লাইব্রেরি খোঁজা হচ্ছে
find_library(
        log-lib
        log)

# লাইব্রেরি লিঙ্ক করা হচ্ছে
target_link_libraries(
        native-lib
        ${log-lib})