//
// Created by blazej on 28.04.2020.
//
#include <sys/epoll.h>
#include <arpa/inet.h>
#include <iostream>
#include <cstring>
#include <unistd.h>
#include "Server.h"
#include "Player.h"
#include "utils.h"


void Player::handleEvent(uint32_t events) {
    std::string();
    //został wysłany jakiś inny event niż epollin
    if(events & ~EPOLLIN){
        error(0, errno, "Event on client with fd = '%d'. Disconnecting.. ", this->fd);
        playerMutex.lock();
        delete this;
        playerMutex.unlock();
    }else if(events && EPOLLIN) {
        //kolejność..
        //czytanie wiadomości -> sprawdzanie co chce zrobić -> odpowiednia reakcja.
        char buffer[BUFFER_SIZE];
        int bytes = readData(this->fd, buffer);
        std::cout << buffer << " Wysyłam potwierdzenie do klienta" <<std::endl;
        char confirmMessage[] = "Odebrano wiadomosc, jako gracz.\n";
        writeData(this->fd, confirmMessage);
    }
}

Player::~Player() {
    Server::deletePlayer(this->getNick());
    epoll_ctl(epollFd, EPOLL_CTL_DEL, fd, nullptr);
    shutdown(fd, SHUT_RDWR);
    close(fd);

}

Player::Player(int pFd, std::string nick) {
    this->fd = pFd;
    this->nick = nick;
    //stworzenie eventu
    epoll_event ee{EPOLLIN|EPOLLRDHUP, {.ptr=this}};
    //dodanie do epolla, nowego wydarzenia związanego z tym graczem.
    epoll_ctl(Player::epollFd, EPOLL_CTL_MOD, pFd, &ee);

}

const std::string &Player::getNick() const {
    return nick;
}

int Player::getPoints() const {
    return points;
}

void Player::setPoints(int points) {
    Player::points = points;
}

Player::Player(Client *cl, std::string nick) {
    this->fd = cl->fd;
    this->nick = nick;
    points = 0;
    epoll_event ee{EPOLLIN|EPOLLRDHUP, {.ptr=this}};
    epoll_ctl(cl->epollFd,EPOLL_CTL_ADD, cl->fd,&ee);


}
