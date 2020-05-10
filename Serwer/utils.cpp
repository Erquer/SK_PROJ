//
// Created by blazej on 29.04.2020.
//

#include <cstring>
#include <iostream>
#include <algorithm>
#include "utils.h"

std::mutex playerMutex;
std::mutex gameMutex;
std::mutex roundMutex;
std::thread gameThread;

int readData(int fd, char *buffer) {
    char tempBuffer[BUFFER_SIZE];
    int bytes = read(fd, tempBuffer, sizeof(tempBuffer));
    if(bytes == -1 ) perror("Blad czytania deskryptora");
    std::cout << "Przeczytano: " << bytes <<" bitow" << std::endl;
    std::string str(tempBuffer);
   // std::replace(str.begin(),str.end(), ' ', '+');
   // std::cout << str << std::endl;
    std::string recived = str.substr(0,bytes);
    std::cout << "Recived: " << recived << std::endl;
    strcpy(buffer,recived.c_str());
    return bytes;
}

void writeData(int fd, char *buffer){
    int bytes = write(fd, buffer, strlen(buffer));

    if(bytes == -1){
        perror("Faled to send data to Client");
    }
    if(bytes != strlen(buffer)){
           perror("Failed to sent whole message");
    }
}
