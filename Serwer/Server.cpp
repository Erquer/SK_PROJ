//
// Created by blazej on 27.04.2020.
//

#include "Server.h"
#include "utils.h"
#include <sys/epoll.h>
#include <arpa/inet.h>
#include <algorithm>
#include <iostream>
#include "Game.h"

std::map<std::string, Player* > Server::playerList;
std::vector<Client*> Server::clientsConnected;
const std::map<std::string, Player *> &Server::getPlayerList() {
    return playerList;
}

void Server::setPlayerList(const std::map<std::string, Player *> &playerList) {
    Server::playerList = playerList;
}

Server::Server(int argc, char **argv) {
crServerSocket(argc,argv);
}

Server::~Server() {

}

void Server::handleEvent(uint32_t event) {
    //zadaniem serwera jest akceptacja nowych połączeń, jeżeli gra się nie toczy.
    if(event && EPOLLIN){
        std::cout << "New Client Wants to connect!!"<< std::endl;
        int connectionSocket = accept(fd,NULL,NULL);
        std::cout << "Accepted connection at socket " << connectionSocket << std::endl;

        Client *newClient = new Client(connectionSocket);

        this->addClient(newClient);
        char welcomeMessage[]= "Witamy na serwerze";
        writeData(connectionSocket, welcomeMessage);


    }




}

void Server::closeServer() {

}


void Server::addPlayer(Player *player) {
    playerMutex.lock();
playerList.insert(std::pair<std::string ,Player*> (player->getNick(),player));
playerMutex.unlock();
}

void Server::deletePlayer(std::string nick) {
    playerMutex.lock();
    playerList.erase(nick);
    playerMutex.unlock();
}

void Server::broadcast(char *buffer) {

}

bool Server::checkList(std::string nick) {
    return false;
}



void Server::addClient(Client *client) {
    clientsConnected.push_back(client);

}

void Server::crServerSocket(int argc, char **argv) {
    sockAddr = {
            .sin_family = AF_INET,
            .sin_port   = htons(atoi(argv[2])),
            .sin_addr   = {inet_addr(argv[1])}
    };
    fd = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (fd == -1)
        error(1, errno, "Failed to create server socket\n");

//    Only allows to faster use the same port - can be deleted later
    const int one = 1;
    int res = setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &one, sizeof(one));
    if (res) error(1, errno, "setsockopt failed");

    if (bind(fd, (sockaddr *) &sockAddr, sizeof(sockAddr)))
        error(1, errno, "Failed to bind server address!\n");

}


bool Server::deleteClientFromServer(Client *client) {
    auto it = std::find(clientsConnected.begin(), clientsConnected.end(),client);
    std::cout << "Before erasing: "<< std::endl;
    for(auto client: clientsConnected){
        std::cout << client->fd << " ; ";
    }
    if(it != clientsConnected.end()){
        std::cout << "Found the Client and I am going to delete him. His fd is: "<< client->fd << std::endl;
        int dist = std::distance(clientsConnected.begin(), it);
        clientsConnected.erase(it);
    }else{
        std::cout << " I couldnt find client " << std::endl;
    }
    if(!clientsConnected.empty()) {
        for (auto client: clientsConnected) {
            std::cout << client->fd << " ; ";
        }
    }
    std::cout << "Erased client";

    return false;
}
