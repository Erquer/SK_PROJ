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
#include <sstream>

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
        readData(this->fd, buffer);
        //std::cout << buffer << std::endl;
       char confirmMessage[] = "PIN";
//        writeData(this->fd, confirmMessage);

        if(buffer[1] == 'j'){
            std::string str(buffer);
            std::string message = str.substr(3,strlen(buffer)-3);
            std::cout << message << std::endl;


            std::vector<std::string> niPin = split(message,';');
            std::cout << "Odebrano: Nick: " << niPin.at(0) << " & PIN: " << niPin.at(1) << std::endl;

            /*
             * połączenie do gry:
             * zapewnione musi być:
             * 1. Podać nick - niepusty( po stronie klienta)
             * 2. Musi istnieć gra, sprawdzane tutaj, odsyłana konkretna wiadomość.
             * 3. Trzeba podać poprawny PIN do Gry, sprawdza serwer.
             * 4. Jeżeli gra jest tworzona, wysyła odpowiedni komunikat.
             */
            //gra istnieje i nie jest tworzona( ktoś już ją stworzył)
//            if(Game::gameInstance && !Game::gameInstance->isOnCreation()){
//
//
//            }
           // writeData(fd,confirmMessage);
            gameMutex.lock();
            if(!Game::gameInstance){
                //nie ma gry, wyslanie tylko powiadomienia.
                std::cout << "Player wants to join to not existing game" << std::endl;
                char res[] = "nogame";
                writeData(this->fd,res);
                gameMutex.unlock();
            }
            else if(Game::gameInstance && Game::gameInstance->isOnCreation()){
                //gra jest tworzona, wysłanie odp. powiadomienia.
                std::cout<<"Player wants to join game in creation" << std::endl;
                char res[] = "creating";
                writeData(this->fd,res);
                gameMutex.unlock();

            }else if(!Game::gameInstance->isOnCreation() && !Game::gameInstance->isStarted1()){
                //gra czeka na graczy


                printf("%s %s \n", niPin.at(1).c_str(),Game::gameInstance->getId());
                playerMutex.lock();
                if(niPin.at(1).compare(std::string(Game::gameInstance->getId())) == 0){
                    //pin jest zgodny.
                    std::cout << "New Player is joinning" << std::endl;
                    if(!Server::checkList(niPin.at(0))) {
                        //nick się zgadza
                        std::cout << "New Player Joined the Game" << std::endl;
                        epoll_ctl(epollFd, EPOLL_CTL_DEL, fd, nullptr);
                        Player *player = new Player(this, niPin.at(0));

                        //dodajemy nowego gracza do kolejki, a usuwamy klienta, bo nie jest już potrzebny.
                        std::cout << "Dodano Gracza o nicku: " << niPin.at(0) << " Jego FD = " << player->fd
                                  << std::endl;

                        Server::addPlayer(player);

                        char res[]= "accepted";
                        writeData(this->fd,res);
                        deleteClient();
                        playerMutex.unlock();
                        std::cout << "Dodano gracza \n";
                        gameMutex.unlock();

                    }else{
                        std::cout << "Player sent nick which already exists" << std::endl;
                        //nick się nie zgadza
                        char res[]= "nick";
                        writeData(this->fd,res);
                        playerMutex.unlock();
                        gameMutex.unlock();
                    }

                }else{
                    //pin się nie zgadza.
                    std::cout<<"Player sent wrong PIN to game" << std::endl;
                    writeData(this->fd,confirmMessage);
                    playerMutex.unlock();
                    gameMutex.unlock();
                }

            }else if(Game::gameInstance->isStarted1()){
                //gra już trwa.
                char mess[] = "already running";
                writeData(this->fd,mess);
                gameMutex.unlock();
            }




        }else if(buffer[1] == 'n'){
            std::string str(buffer);
            std::string PIN = str.substr(3,strlen(buffer)-3);
            gameMutex.lock();
            if((!Game::gameInstance)){
                //nikt nie tworzy gry.
                std::cout<< " Udzielam dostępu" << std::endl;
                epoll_ctl(epollFd,EPOLL_CTL_DEL,fd, nullptr);
                GameOwner *gameOwner = new GameOwner(this);
                Game *game = new Game(gameOwner);
                game->setID(PIN);
                //temp komenda do testowania dołączenia nowych graczy.
               // Game::gameInstance->setOnCreation(false);
                char message[] = "accepted";
                gameMutex.unlock();
                writeData(this->fd,message);
                deleteClient();
            }else if(Game::gameInstance && Game::gameInstance->isOnCreation()){
                //ktoś już tworzy grę
                std::cout << "Nie udzielam dostepu, bo ktos juz tworzy gre" << std::endl;
                char message[] = "creating";
                writeData(this->fd,message);

                gameMutex.unlock();

            }else if(Game::gameInstance && !Game::gameInstance->isOnCreation() && !Game::gameInstance->isStarted1()){
                //gra istenieje i nie jest tworzona i czeka na rozpoczęcie.
                std::cout << "Gra oczekuje na graczy"<< std::endl;

                gameMutex.unlock();
            }else{
                //gra jest stworzona i rozpoczęta.
                std::cout << "Gra trwa " << std::endl;
                char mess[] = "already runnning";
                writeData(this->fd,mess);
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
