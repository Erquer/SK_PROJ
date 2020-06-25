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

    char id[5];
    std::string name;
    int roundTime;
    int round;
public:
    int getRound() const;

    void setRound(int round);

    static void addPlayerByTime(Player *player);

    static std::vector<Player*> playerAnswers;

private:
    std::vector<Question> questions;
    //odpowiedzi graczy, którzy odpowiadali w kolejności ich odpowiadania.

    //do połączenia samego klienta nie potrzeba mutexu.

private:
    GameOwner *owner;
    bool isStarted;
    bool onCreation;



    void calculateResults();

    void sendResults();


public:
    // ==== konstruktor i destruktor ====
    Game();

    ~Game();

    static Game* gameInstance;

    // publiczne metody
    bool isGameOwnerSet() const; //sprawdza, czy jest już właściciel gry, jeżeli tak, nie dopusza innych klientów do tworzenia gry.

    void runGame();

    void deleteGame();

    void addQuestion(Question &question);


    // ==== getters & setters ====
    const char *getId() const;

    void setID(std::string id);

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

    bool isOnCreation() const;

    void setOnCreation(bool onCreation);

    Game(GameOwner *gameOwner);

    void sendEveryThingToGameOwner();

    void sendBestThreeToPlayers();

    void resetGame();


    std::string createPlayerMessage(Player *player);
};


#endif //SK_PROJ_GAME_H
