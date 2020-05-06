//
// Created by blazej on 29.04.2020.
//

#include "Client.h"
#include "Server.h"
#include "utils.h"
#include <sys/epoll.h>
#include <iostream>
#include <cstring>

Client::Client(int fd) {
    this->fd = fd;
    epoll_event ee{EPOLLIN|EPOLLRDHUP, {.ptr=this}};
    epoll_ctl(Client::epollFd,EPOLL_CTL_ADD, fd, &ee);
}


/*
 * Kody i wiadomości:
 * Cj+nick - join game + nick gracza.
 * Cn+PIN - new game + PIN do gry.
 * Ce+null - exit.
 */
void Client::handleEvent(uint32_t events) {
    std::string();
    //został wysłany jakiś inny event niż epollin
    if(events & ~EPOLLIN){
        error(0, errno, "Event on client with fd = '%d'. Disconnecting.. ", this->fd);
        playerMutex.lock();
        delete this;
        playerMutex.unlock();
    }else if(events && EPOLLIN){
        //kolejność..
        //czytanie wiadomości -> sprawdzanie co chce zrobić -> odpowiednia reakcja.
        char buffer[BUFFER_SIZE];
        int bytes = readData(this->fd, buffer);
        std::cout << buffer << std::endl;
        char confirmMessage[] = "Odebrano wiadomosc \n";
        writeData(this->fd, confirmMessage);

        if(buffer[1] == 'j'){
            std::string str(buffer);
            std::string nick = str.substr(3,strlen(buffer)-3);
            Player *player = new Player(this,nick);
            //dodajemy nowego gracza do kolejki, a usuwamy klienta, bo nie jest już potrzebny.
            std::cout<<"Dodano Gracza o nicku: " << nick << std::endl;
            deleteClient();
            Server::addPlayer(player);

        }else if(buffer[1] == 'n'){

        }else{
         //   delete this;
        }
        }

}
void Client::deleteClient() {
    Server::deleteClientFromServer(this);
}
Client::~Client(){
    Server::deleteClientFromServer(this);
    epoll_ctl(epollFd, EPOLL_CTL_DEL, fd, nullptr);
    shutdown(fd, SHUT_RDWR);
    close(fd);
}

Client::Client() {

}
