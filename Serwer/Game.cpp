//
// Created by blazej on 27.04.2020.
//

#include <cstring>
#include "Game.h"
#include "Server.h"
#include "utils.h"

Game *Game::gameInstance;
std::vector<Player*> Game::playerAnswers;

const char *Game::getId() const {
    return id;
}

const std::string &Game::getName() const {
    return name;
}

void Game::setName(const std::string &name) {
    Game::name = name;
}

int Game::getRoundTime() const {
    return roundTime;
}

void Game::setRoundTime(int roundTime) {
    Game::roundTime = roundTime;
}

const std::vector<Question> &Game::getQuestions() const {
    return questions;
}

void Game::setQuestions(const std::vector<Question> &questions) {
    Game::questions = questions;
}


GameOwner *Game::getOwner() const {
    return owner;
}

void Game::setOwner(GameOwner *owner) {
    Game::owner = owner;
}

bool Game::isStarted1() const {
    return isStarted;
}

void Game::setIsStarted(bool isStarted) {
    Game::isStarted = isStarted;
}

Game::Game(GameOwner *gameOwner) {
    std::cout << "New Game is Created by client with fd: "<< gameOwner->fd << std::endl;
    gameInstance = this;
    this->setOwner(gameOwner);
    this->setIsStarted(false);
    this->setOnCreation(true);

}

bool Game::isOnCreation() const {
    return onCreation;
}

void Game::setOnCreation(bool onCreation) {
    Game::onCreation = onCreation;
}

Game::~Game() {
    gameInstance = nullptr;
    printf("Game Destrucion \n");

}


void Game::resetPoints(bool reset) {



}

bool Game::isGameOwnerSet() const {
    return this->getOwner() != nullptr;
}

void Game::setID(std::string id) {
    for(int i = 0; i < 5; i ++){
        this->id[i] = id[i];
    }

}

void Game::deleteGame() {
    delete this;
}

void Game::addQuestion(Question &question) {
    this->questions.push_back(question);
}

char startGameMessage[] = "Game is Starting";
char roundMessage[] = "round:";
char resultMessage[] = "points:";

void Game::runGame() {

    std::cout << "Gra zaczyna sie " << std::endl;
    //kolejność
    /*
     * 1. Wysłanie wiadomości o początku gry
     * 2. Wysłanie wiadomości o początku rundy
     * 3. Czekanie na odpowiedzi graczy
     * 4. Obliczanie wyników
     * 5. Zablokowanie graczom możliwości gry przez zinkrementowanie rundy.
     * 6. Odesłanie wyników do graczy
     */
    char buffer[BUFFER_SIZE];
    //zaczęcie pierwszej rundy.
    roundMutex.lock();
    round++;
    roundMutex.unlock();
    Server::broadcast(startGameMessage);
    playerMutex.lock();
    //gra się toczy, dopóki na serwerze jest chociaż jeden gracz.
    while(!Server::getPlayerList().empty()){
        playerMutex.unlock();

        //tworzenie wiadomości o nowej rundzie
        std::string message(roundMessage);
        message += round + ';';
        message.append(createQuestionString(&questions.at(round-1)));
        std::cout << message << std::endl;
        char tableMessage[message.size()+1];
        strcpy(tableMessage,message.c_str());
        tableMessage[message.size()] = '\0';
        //wysłanie wiadomości o nowej rundzie do graczy.
        Server::broadcast(tableMessage);

        //czas oczekiwania na odpowiedzi graczy.
        std::cout << "Początek nowej rundy: " << round << std::endl;

        sleep(ROUND_TIME);

        std::cout << "Koniec rundy: " << round << std::endl;

        //incrementowanie rundy, aby ignorować odpowiedzi otrzymane po czasie.
        roundMutex.lock();
        round++;
        roundMutex.unlock();

        //kalkulacja wyników graczy.
        calculateResults();

        //wysłanie wyników do graczy.
        sendResults();





    }

    std::cout << "Gra zakonczona " << std::endl;

    delete this;

}



void Game::addPlayerByTime(Player *player) {
    gameInstance->playerAnswers.push_back(player);
}

int Game::getRound() const {
    return round;
}

void Game::setRound(int round) {
    Game::round = round;
}

void Game::calculateResults() {
    playerMutex.lock();
    //pierwsza odpowiedź otrzymuje max punktów.
    int actualPoints = FIRST_ANSWER_POINTS;
    for(auto it = gameInstance->playerAnswers.begin(); it != gameInstance->playerAnswers.end(); ++it){
        Player *player = *it;
        //jeżeli gracz po drodze się rozłączył, przechodzimy do kolejnego.
        if(!Server::checkList(player->getNick())){
            continue;
        }
        //jeżeli ostatnią odpowiedzią gracza, była poprawna, dodajemy mu punkty, i obniżamy próg dla kolejnych
        if(player->lastAnswer == questions.at(round-2).getCorrectAnswer()) {
            player->setPoints(player->getPoints() + actualPoints);
            //dopóki możemy, obniżamy punkty, później gracze otrzymują tylko to co zostanie.
            if(actualPoints>=DECREASED_POINTS)
                    actualPoints -= DECREASED_POINTS;

        }



    }
    playerMutex.unlock();
}

void Game::sendResults() {
    playerMutex.lock();

    for(const auto &player: Server::getPlayerList()){
        std::string resultString = resultMessage;
        resultString.append(std::to_string(player.second->getPoints()));
    }

    playerMutex.unlock();

}




