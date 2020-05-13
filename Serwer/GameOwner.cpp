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
        int bytes = readData(this->fd, buffer);
        //std::cout <<"Game Owner odebral: " <<buffer << std::endl;
        std::string str(buffer);
        std::string header = str.substr(0,3);
        std::cout << header << std::endl;
        std::string message = str.substr(3, strlen(buffer) - 3);
        std::cout << message << std::endl;
        if(header.compare("dzialam\n") == 0){
            std::cout<< "GameOwner Działa z PID: " << this->fd << std::endl;
            char confirm[] ="Potwierdzam dzialanie GameOwnera";
            writeData(this->fd,confirm);
        }else if(header.compare("Gq+") == 0){
            //przesłanie nowego pytania na serwer.
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
            Game::gameInstance->addQuestion(reinterpret_cast<Question &>(question));
            std::cout << "Odebrano pytanie: " << question->question <<" "<< question->correctAnswer <<" "<< question->answers.at(0)
                                                << " "<<question->answers.at(1)<<" "<<question->answers.at(2)<<" "<< question->answers.at(3)<< std::endl;
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
