//
// Created by blazej on 29.04.2020.
//

#include "Client.h"
#include "Server.h"
#include "Game.h"
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
            /*
             * połączenie do gry:
             * zapewnione musi być:
             * 1. Podać nick - niepusty( po stronie klienta)
             * 2. Musi istnieć gra, sprawdzane tutaj, odsyłana konkretna wiadomość.
             * 3. Trzeba podać poprawny PIN do Gry, sprawdza serwer.
             * 4. Jeżeli gra jest tworzona, wysyła odpowiedni komunikat.
             */
            //gra istnieje i nie jest tworzona( ktoś już ją stworzył)
            if(Game::gameInstance && !Game::gameInstance->isOnCreation()){


            }
            epoll_ctl(epollFd, EPOLL_CTL_DEL, fd, nullptr);
            Player *player = new Player(this,nick);

            //dodajemy nowego gracza do kolejki, a usuwamy klienta, bo nie jest już potrzebny.
            std::cout<<"Dodano Gracza o nicku: " << nick << std::endl;

            Server::addPlayer(player);
            deleteClient();


        }else if(buffer[1] == 'n'){
            std::string str(buffer);
            std::string PIN = str.substr(3,strlen(buffer)-3);
            gameMutex.lock();
            if((!Game::gameInstance)){
                //nikt nie tworzy gry.
                GameOwner *gameOwner = new GameOwner(this);
                Game *game = new Game();
                game->setOwner(gameOwner);
                game->setID(PIN);
                char message[] = "accepted\n";
                gameMutex.unlock();
                writeData(this->fd,message);
            }else if(Game::gameInstance && Game::gameInstance->isOnCreation()){
                //ktoś już tworzy grę

                gameMutex.unlock();

            }else if(Game::gameInstance && !Game::gameInstance->isOnCreation() && !Game::gameInstance->isStarted1()){
                //gra istenieje i nie jest tworzona i czeka na rozpoczęcie.

                gameMutex.unlock();
            }else{
                //gra jest stworzona i rozpoczęta.
             gameMutex.unlock();
            }

        }else{
            delete this;
        }
        }

}
void Client::deleteClient() {
    Server::deleteClientFromServer(this);
}
Client::~Client(){
    Server::deleteClientFromServer(this);
    epoll_ctl(this->epollFd,EPOLL_CTL_DEL,fd, nullptr);
    shutdown(this->fd,SHUT_RDWR);
    close(fd);
}

Client::Client() {

}
