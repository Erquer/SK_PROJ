cmake_minimum_required(VERSION 3.16)
project(SK_PROJ)

set(CMAKE_CXX_STANDARD 17)
set (CMAKE_CXX_FLAGS "--std=c++17 -Wall -O0 -g -pthread")

include_directories(.)
include_directories(Serwer)

add_executable(SK_PROJ main.cpp Serwer/EpollContainer.cpp Serwer/EpollContainer.h Serwer/Game.cpp Serwer/Game.h Serwer/Server.cpp Serwer/Server.h Serwer/Question.cpp Serwer/Question.h Serwer/Player.cpp Serwer/Player.h Serwer/GameOwner.cpp Serwer/GameOwner.h Serwer/utils.cpp Serwer/utils.h Serwer/Client.cpp Serwer/Client.h)