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

std::map<std::string, Player *> Server::getPlayerList() {
    return playerList;
}

void Server::setPlayerList(const std::map<std::string, Player *> &playerList) {
    Server::playerList = playerList;
}

Server::Server(int argc, char **argv) {
crServerSocket(argc,argv);
}

Server::~Server() {
  //  pthread_cancel(gameThread.native_handle());
    char exit[] = "lost";
    broadcast(exit);
    for(auto it: playerList){
        delete it.second;
    }
    playerList.clear();
    close(fd);
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
    delete this;
}


void Server::addPlayer(Player *player) {
   //mutex w Client.cpp
    playerList.insert(std::pair<std::string ,Player*> (player->getNick(),player));
}

void Server::deletePlayer(std::string nick) {
    //mutex poza funkcją
    playerList.erase(nick);

}

void Server::broadcast(char *buffer) {
    playerMutex.lock();
    for(const auto &player: playerList){
        writeData(player.second->fd, buffer);
    }
    //czekanie, dla pewności, że wszędzie doszły wiadomości.
    sleep(1);
    playerMutex.unlock();

}

bool Server::checkList(std::string nick) {
    return playerList.find(nick) != playerList.end();
}



void Server::addClient(Client *client) {
    clientsConnected.push_back(client);

}

void Server::crServerSocket(int argc, char **argv) {
//    sockAddr = {
//            .sin_family = PF_INET,
//            .sin_port   = htons(atoi(argv[2])),
//            .sin_addr.s_addr  = INADDR_ANY
//    };
    sockAddr.sin_family =PF_INET;
    sockAddr.sin_port = htons(atoi(argv[2]));
    sockAddr.sin_addr.s_addr = INADDR_ANY;
    fd = socket(PF_INET, SOCK_STREAM, 0);
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
    //std::cout << "Before erasing: "<< std::endl;
    for(auto client: clientsConnected){
        std::cout << client->fd << " ; ";
    }
    if(it != clientsConnected.end()){
        std::cout << "Found the Client and I am going to delete him. His fd is: "<< client->fd << std::endl;
        //int dist = std::distance(clientsConnected.begin(), it);
        clientsConnected.erase(it);
    }else{
        std::cout << " I couldnt find client " << std::endl;
    }
    //wyświetlanie pozostałych połączonych klientów
    if(!clientsConnected.empty()) {
        for (auto client: clientsConnected) {
            std::cout << client->fd << " ; ";
        }
    }
    //std::cout << "Erased client";

    return false;
}



void Server::resetServer() {
    playerMutex.lock();
    for(auto const& p: Server::getPlayerList()){
        epoll_ctl(epollFd, EPOLL_CTL_DEL, p.second->fd, nullptr);
        auto *client = new Client(p.second->fd);
        Server::addClient(client);
        std::cout << "Usuwam gracza o nicku: " << p.first << std::endl;
        Server::deletePlayer(p.first);
        char mess[] = "new";
        writeData(p.second->fd, mess);
    }
    playerMutex.unlock();
    //if there is still game owner, make his normal client.
    if(Game::gameInstance->isGameOwnerSet()){
        int fd = Game::gameInstance->getOwner()->fd;
        epoll_ctl(epollFd, EPOLL_CTL_DEL, fd, nullptr);
        auto *client = new Client(fd);
        Server::addClient(client);
        std::cout << "Usuwam gameownera o fd: " << fd << std::endl;
        free(Game::gameInstance->getOwner());
        char mess[] = "new";
        writeData(fd, mess);

    }
    //pętla sprawdzająca, czy klienci są dobrze dodani.
    for(auto const a: Server::clientsConnected){
        std::cout << "Client fd: " << a->fd << std::endl;
    }

}
