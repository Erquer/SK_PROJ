//
// Created by blazej on 29.04.2020.
//
#pragma once

#include <cerrno>
#include <error.h>
#include <string>
#include <unistd.h>
#include <vector>
#include <mutex>
#include <thread>
#include "Question.h"


#define BUFFER_SIZE 255
#define FIRST_ANSWER_POINTS 4000
#define DECREASED_POINTS 400
#define ROUND_TIME 20
#define READY_TIME 10


/**
 * message composition: header(2 chars)+message;
 * possible headers by client types:
 * GameOwner: 1st char "O", second: "q"- sending question, "i"- send pin to game, "e" - exit
 * Player: 1st char "P", second: "a"- answer, "e" - exit
 * Client: 1st char "C", second: "j" - join the game(become the Player), "n"- create new game (become GameOwner), "e"- exit
 */

extern std::mutex playerMutex;
extern std::mutex gameMutex;
extern std::mutex roundMutex;
extern std::thread gameThread;

int readData(int fd, char *buffer);
void writeData(int fd, char *buffer);
std::vector<std::string> split (const std::string &s, char delim);
std::string createQuestionString(Question *question);

