//
// Created by blazej on 28.04.2020.
//

#ifndef SK_PROJ_PLAYER_H
#define SK_PROJ_PLAYER_H
#include <iostream>
#include <cstdlib>
#include <vector>
#include "EpollContainer.h"
#include "Client.h"

class Player : public EpollContainer{
private:
    std::string nick; //nick
    int points; // punkty gracza.
public:
    //wektor odpowiedzi w kolejności.
    std::vector<char> answers;

    // ==== Konstruktor i destruktor  ====
    Player(int pFd, std::string nick);
    Player(Client *cl, std::string nick);
    ~Player() override;


    // ==== publiczne metody ====
    void handleEvent(uint32_t events) override;

    void addPoints();





    // ==== gettery i settery ====
    void setNick(std::string nick) {this->nick = nick;}

    const std::string &getNick() const;

    int getPoints() const;

    void setPoints(int points);

};


#endif //SK_PROJ_PLAYER_H
