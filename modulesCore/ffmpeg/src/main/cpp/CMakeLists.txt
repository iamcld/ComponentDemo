#目前发现新添加cpp文件或者cpp文件重命名，需要重新同步下cmake文件，cpp文件才能找到生效，暂时还不知道原因
cmake_minimum_required(VERSION 3.10.2)
#CMAKE_SOURCE_DIR： 最外层CMakeLists.txt所在目录即/cpp目录
#编译的源码文件
file(GLOB SOURCE_PATH ${CMAKE_SOURCE_DIR}/*.cpp ${CMAKE_SOURCE_DIR}/*.h)

#设置生成的so动态库最后输出的路径，默认编译产出路径在build/intermediates/cmake/debug/obj
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR}/output/${ANDROID_ABI})

#add_library(
#        #设置生成的库名
#        ffmpegLibDemo
#        #设置成动态库。SHARED：生成动态库  STATIC：生成静态库
#        SHARED
#        #库源码文件
#        ffmpeg.cpp)

add_library(
        ffmpegLibDemo
        SHARED
        ${SOURCE_PATH}
)

#NDK日志库
find_library(log-lib log)

#导入编译时头文件
include_directories(${CMAKE_SOURCE_DIR}/include)

#设置链接静态库的目录，全部静态库
set(my_lib_path ${CMAKE_SOURCE_DIR}/libs/${CMAKE_ANDROID_ARCH_ABI})
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${my_lib_path}" )

# 增加库文件,单个静态库
#add_library(x264 STATIC IMPORTED)
#set_target_properties(avcodec
#        PROPERTIES IMPORTED_LOCATION ${PRJ_SOURCE_DIR}/lib/${ANDROID_ABI}/libavcodec.a)

# 注意，如果 libA 需要依赖 libB,那libB 必须排到 libA 后面
target_link_libraries(
        #目标库：设置生成的库名
        ffmpegLibDemo
        # 链接静态库和NDK库到目标库中
        avfilter avformat avcodec avutil swresample swscale
        # android系统头文件android/native_window.h，故要引入libandroid.so库
        android
        z
        OpenSLES
        ${log-lib})