//
// Created by blazej on 27.04.2020.
//

#ifndef SK_PROJ_GAME_H
#define SK_PROJ_GAME_H

#include "Question.h"
#include "Player.h"
#include "GameOwner.h"

class Game {
private:
    Game(GameOwner *gameOwner);

    char id[5];
    std::string name;
    int roundTime;
    static int round;
    std::vector<Question> questions;
    std::vector<Player*> playerAnswers;
    //do połączenia samego klienta nie potrzeba mutexu.

private:
    GameOwner *owner;
    bool isStarted;

    void resetPoints(bool reset = false);


public:
    // ==== konstruktor i destruktor ====
    Game();

    virtual ~Game();

    static Game* gameInstance;

    // publiczne metody
    bool isGameOwnerSet() const; //sprawdza, czy jest już właściciel gry, jeżeli tak, nie dopusza innych klientów do tworzenia gry.

    void runGame();

    static void addPlayersByTime();




    // ==== getters & setters ====
    const char *getId() const;

    const std::string &getName() const;

    void setName(const std::string &name);

    int getRoundTime() const;

    void setRoundTime(int roundTime);

    const std::vector<Question> &getQuestions() const;

    void setQuestions(const std::vector<Question> &questions);

    const std::vector<Player *> &getPlayerAnswers() const;

    void setPlayerAnswers(const std::vector<Player *> &playerAnswers);

    GameOwner *getOwner() const;

    void setOwner(GameOwner *owner);

    bool isStarted1() const;

    void setIsStarted(bool isStarted);
};


#endif //SK_PROJ_GAME_H
