//
// Created by blazej on 29.04.2020.
//

#include <cstring>
#include <iostream>
#include <algorithm>
#include <sstream>
#include "utils.h"
#include "Question.h"

std::mutex playerMutex;
std::mutex gameMutex;
std::mutex roundMutex;
std::thread gameThread;

int readData(int fd, char *buffer) {
    char tempBuffer[BUFFER_SIZE];
    int bytes = read(fd, tempBuffer, sizeof(tempBuffer));
    if(bytes == -1 ) perror("Blad czytania deskryptora");
    //std::cout << "Przeczytano: " << bytes <<" bitow" << std::endl;
    std::string str(tempBuffer);
   // std::replace(str.begin(),str.end(), ' ', '+');
   // std::cout << str << std::endl;
    std::string recived = str.substr(0,bytes);
    //std::cout << "Recived: " << recived << std::endl;
    strcpy(buffer,recived.c_str());
    return bytes;
}

void writeData(int fd, char *buffer){

    char message[strlen(buffer) + 1];
    strcpy(message,buffer);
    strcat(message,"\n");
    int bytes = write(fd, message, strlen(message));

    if(bytes == -1){
        perror("Faled to send data to Client");
    }
    if(bytes != strlen(message)){
           perror("Failed to sent whole message");
    }
}
std::vector<std::string> split (const std::string &s, char delim) {
    std::vector<std::string> result;
    std::stringstream ss (s);
    std::string item;

    while (getline (ss, item, delim)) {
        result.push_back (item);
    }

    return result;
}

std::string createQuestionString(Question *question){
    return question->getQuestion() +';' + question->getCorrectAnswer() + ';' + question->getAnswers().at(0) +
                    ';' + question->getAnswers().at(1) + ';' + question->getAnswers().at(2) + ';' +question->getAnswers().at(3);
}
