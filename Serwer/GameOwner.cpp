//
// Created by blazej on 28.04.2020.
//

#include "GameOwner.h"
#include "Game.h"
#include "Server.h"
#include "utils.h"
#include <sys/epoll.h>
#include <iostream>
#include <cstring>
#include <sstream>

void GameOwner::handleEvent(uint32_t events) {
    if(events & ~EPOLLIN){
        gameMutex.lock();
        delete this;
        gameMutex.unlock();


    }else if(events && EPOLLIN){
        char buffer[BUFFER_SIZE];
        readData(this->fd, buffer);
        //std::cout <<"Game Owner odebral: " <<buffer << std::endl;
        std::string str(buffer);
        std::string header = str.substr(0,3);
        std::cout << header << std::endl;
        std::string message = str.substr(3, strlen(buffer) - 3);
        std::cout << message << std::endl;
        if(header.compare("Gs+") == 0){
            //zmiana stanu gry
            if(message.compare("start") == 0) {
                playerMutex.lock();
                gameMutex.lock();
                if (Server::getPlayerList().empty()) {
                    //brak graczy.
                    gameMutex.unlock();
                    char mess[] = "no players";
                    writeData(this->fd, mess);
                    playerMutex.unlock();

                } else if (Game::gameInstance->getQuestions().empty()) {
                    //na serwerze nie ma nawet jednego pytania
                    playerMutex.unlock();
                    gameMutex.unlock();
                    char mess[] = "no questions";
                    writeData(this->fd, mess);

                } else {
                    //jest przynajmniej jeden gracz
                    playerMutex.unlock();
                    gameMutex.unlock();

                    //czekanie na graczy.
                    sleep(READY_TIME);

                    char gameOwnerMessage[] = "started";
                    writeData(this->fd, gameOwnerMessage);
                    //start nowego wątku dla gry.
                    gameMutex.lock();
                    Game::gameInstance->setIsStarted(true);
                    gameThread = std::thread(&Game::runGame, Game::gameInstance);
                    std::cout << "Utworzono Wątek gry" << std::endl;
                    gameThread.detach();
                }
            }else if(message.compare("ready") == 0){
                gameMutex.lock();
                if(Game::gameInstance->getQuestions().empty()){
                    char mess[] = "no questions";
                    writeData(this->fd,mess);
                    gameMutex.unlock();
                } else{
                    //zmiana stanu gry, na oczekiwanie na graczy.
                    Game::gameInstance->setOnCreation(false);
                    char mess[] = "ready";
                    writeData(this->fd,mess);
                    gameMutex.unlock();

                }
            }

        }else if(header.compare("Gq+") == 0){
            //przesłanie nowego pytania na serwer.
            std::cout<<"Odbieram pytanie od GameOwnera" << std::endl;
            std::vector<std::string> newQuestion = split(message,';');
            //pytanie -> odp a -> odp b -> odp c -> odp d -> poprawna odpowiedz.
            char confirm[] = "otrzymano pytanie";
            std::vector<std::string> ans;
            ans.push_back(newQuestion.at(1));
            ans.push_back(newQuestion.at(2));
            ans.push_back(newQuestion.at(3));
            ans.push_back(newQuestion.at(4));
            char correct[newQuestion.at(5).size()];
            strcpy(correct,newQuestion.at(5).c_str());

            auto *question = new Question(newQuestion.at(0),ans,correct[0]);
            Game::gameInstance->addQuestion(*question);
            std::cout << "Odebrano pytanie: " << question->question <<" "<< question->correctAnswer <<" "<< question->answers.at(0)
                                                << " "<<question->answers.at(1)<<" "<<question->answers.at(2)<<" "<< question->answers.at(3)<< std::endl;
            writeData(this->fd,confirm);
        }else if(header.compare("Gp+") == 0){
            //Game Owner prosi o listę graczy połączonych do gry.
            std::cout << "GameOwner prosi o liste graczy" << std::endl;
            playerMutex.lock();
            if(Server::getPlayerList().empty()){
                //pusta lista, wysyłamy odpowiednie powiadomienie.
                char res[] = "empty";
                writeData(this->fd,res);
                playerMutex.unlock();
                std::cout<<"Wysłano empty \n";

            }else{
                //wysyłamy listę graczy.
                std::string players = "";
               // std::cout << "Wchodze do petli \n" ;
                for(const auto &player : Server::getPlayerList()){
                    Player *player1 = player.second;
                    players.append(';'+player1->getNick() + "," + std::to_string(player1->getPoints()) );
                }
                players.replace(0,1,"");
              //  std::cout<<"Player String:"<< players<< std::endl;
                players = "players:" + players;
                char sendPlayers[players.size()+1];
                strcpy(sendPlayers,players.c_str());
                sendPlayers[players.size()] = '\0';

                writeData(this->fd,sendPlayers);
                playerMutex.unlock();
            }

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
