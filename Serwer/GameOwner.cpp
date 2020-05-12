//
// Created by blazej on 28.04.2020.
//

#include "GameOwner.h"
#include "Game.h"
#include "Server.h"
#include "utils.h"
#include <sys/epoll.h>

void GameOwner::handleEvent(uint32_t events) {
    if(events & ~EPOLLIN){
        gameMutex.lock();
        delete this;
        gameMutex.unlock();


    }else if(events && EPOLLIN){
        char buffer[BUFFER_SIZE];
        int bytes = readData(this->fd, buffer);
        std::cout <<"Game Owner odebral: " <<buffer << std::endl;
        std::string str(buffer);
        if(str.compare("dzialam\n")){
            std::cout<< "GameOwner Działa z PID: " << this->fd << std::endl;
            char confirm[] ="Potwierdzam dzialanie GameOwnera";
            writeData(this->fd,confirm);
        }else{
            char confirm[] ="NIE Potwierdzam dzialania GameOwnera";
            writeData(this->fd,confirm);
        }

    }
}

void GameOwner::getScoresAndNicks() {

}

GameOwner::~GameOwner() {
    if(Game::gameInstance->isStarted1()){
        //gra działa, usuwamy tylko GameOwnera, gra toczy się dalej.
        std::cout << "Wychodzę z trwającej gry" << std::endl;

        epoll_ctl(this->epollFd,EPOLL_CTL_DEL,fd, nullptr);
        shutdown(this->fd,SHUT_RDWR);
        close(fd);

    }else if(Game::gameInstance->isOnCreation()){
        //gra jest aktualnie tworzona przez niego, usuwamy grę i jego.
        std::cout<<"Gra którą tworzę jest usuwana" << std::endl;
        epoll_ctl(this->epollFd,EPOLL_CTL_DEL,fd, nullptr);
        shutdown(this->fd,SHUT_RDWR);
        close(fd);
        Game::gameInstance->deleteGame();

    }else if(!Game::gameInstance->isStarted1() && !Game::gameInstance->isOnCreation()){
        //gra oczekuje na graczy.
        char leaveLobby[] = "cancel";
        Server::broadcast(leaveLobby);
        epoll_ctl(this->epollFd,EPOLL_CTL_DEL,fd, nullptr);
        shutdown(this->fd,SHUT_RDWR);
        close(fd);
    }
}

GameOwner::GameOwner(Client *client) {
    this->fd = client->fd;
    epoll_event ee{EPOLLIN|EPOLLRDHUP, {.ptr=this}};
    epoll_ctl(client->epollFd,EPOLL_CTL_ADD, client->fd,&ee);

}
