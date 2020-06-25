//
// Created by blazej on 27.04.2020.
//

#include <cstring>
#include "Game.h"
#include "Server.h"
#include "utils.h"
#include <algorithm>

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
    printf("Game Destrucion; Closing Server. \n");
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
char endMessage[] = "end:";
char ownerMess[] = "round";

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
    //char buffer[BUFFER_SIZE];
    //zaczęcie pierwszej rundy.
    roundMutex.lock();
    round++;
    roundMutex.unlock();
    Server::broadcast(startGameMessage);
    playerMutex.lock();
    //gra się toczy, dopóki na serwerze jest chociaż jeden gracz. lub gra się nie skończyła.
    while(!Server::getPlayerList().empty() && round <= (int)questions.size()){
        playerMutex.unlock();

        //tworzenie wiadomości o nowej rundzie
        std::string message(roundMessage);
        message += std::to_string(round) + ';';
        message.append(createQuestionString(&questions.at(round-1)));
        std::cout << message << std::endl;
        char tableMessage[message.size()+1];
        strcpy(tableMessage,message.c_str());
        tableMessage[message.size()] = '\0';
        std::cout << tableMessage << std::endl;
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


    if(isGameOwnerSet())
        writeData(this->getOwner()->fd,ownerMess);

    }
    if(isGameOwnerSet()){
        sendEveryThingToGameOwner();
    }
    sendBestThreeToPlayers();
    std::cout << "Gra zakonczona " << std::endl;

    //time for players to see their scores.
    sleep(10);

    Server::resetServer();
    std::cout << "Server reseted" << std::endl;
    this->resetGame();

    std::cout << "Game reseted" << std::endl;

}


void Game::sendEveryThingToGameOwner(){
    //na koniec gry wysyłane są wszystkie dane nt. graczy i ich wyników.
    std::string message(endMessage);
    playerMutex.lock();
    for(const auto &pl: Server::getPlayerList()){
        message.append(createPlayerMessage(pl.second));
    }
    playerMutex.unlock();
    message.at(message.size()-1) = '\0';
    char charMessage[message.size()];
    strcpy(charMessage,message.c_str());
    writeData(gameInstance->getOwner()->fd,charMessage);
    std::cout << "wyslano wiadomosc z danymi do game ownera: " << message << std::endl;


}

bool comparePoints(Player *p1, Player *p2){
    return (p1->getPoints() > p2->getPoints());
}

void Game::sendBestThreeToPlayers(){
    std::vector<Player*> players;
    playerMutex.lock();
    for(const auto &pl:Server::getPlayerList()){
        players.push_back(pl.second);
    }
    playerMutex.unlock();
    std::sort(players.begin(),players.end(),comparePoints);
    std::string bestString(endMessage);
    if(players.size() >= 3) {
        for (int i = 0; i < 3; i++) {
            bestString.append(players.at(i)->getNick() + ';' + std::to_string(players.at(i)->getPoints()) + ':');
        }
    }
    else{
        for(size_t i = 0; i < players.size();i++){
            bestString.append(players.at(i)->getNick() + ';' + std::to_string(players.at(i)->getPoints()) + ':');

        }
    }
    char charMessage[bestString.size()];
    strcpy(charMessage,bestString.c_str());
    charMessage[bestString.size()-1] = '\0';
    Server::broadcast(charMessage);
}


void Game::addPlayerByTime(Player *player) {
    Game::playerAnswers.push_back(player);
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
    std::cout << "Calculating the results" << std::endl;
    for(auto *player : Game::playerAnswers){
        std::cout << "sprawdzam gracza o nicku: " << player->getNick() << std::endl;
        //jeżeli gracz po drodze się rozłączył, przechodzimy do kolejnego.
        if(!Server::checkList(player->getNick())){
            continue;
        }
        //jeżeli ostatnią odpowiedzią gracza, była poprawna, dodajemy mu punkty, i obniżamy próg dla kolejnych
        if(player->lastAnswer == questions.at(round-2).getCorrectAnswer()) {
            std::cout << "Player otrzymuje punkty: " << actualPoints << std::endl;
            player->setPoints(player->getPoints() + actualPoints);
            //dopóki możemy, obniżamy punkty, później gracze otrzymują tylko to co zostanie.
            if(actualPoints>=DECREASED_POINTS)
                    actualPoints -= DECREASED_POINTS;

        }
    }
    Game::playerAnswers.clear();
    playerMutex.unlock();
}

void Game::sendResults() {
    playerMutex.lock();
    std::cout << "Sending the Results" << std::endl;
    for (const auto &player: Server::getPlayerList()) {
        std::string resultString = resultMessage;
        resultString.append(std::to_string(player.second->getPoints()));
        char result[resultString.size() + 1];
        std::strcpy(result, resultString.c_str());
        result[resultString.size()] = '\0';
        writeData(player.second->fd, result);
    }
    sleep(1);
    playerMutex.unlock();

}
std::string Game::createPlayerMessage(Player *player){
    //tworzy string zawierający nick, odpowiedzi i punkty danego gracza.
    //Postać: nick;<odp>;<odp>;...;<punkty>|
    std::string playerString = player->getNick() + ";";
    for(size_t i = 0; i < gameInstance->getQuestions().size(); i++){
        std::string s(1,player->answers.at(i));
        playerString.append(s + ";" );

    }
    playerString.append(std::to_string(player->getPoints()));
    playerString.append("=");

    return playerString;
}

void Game::resetGame() {
   // gameMutex.lock();

    this->setOwner(nullptr);
    this->questions.clear();
    this->playerAnswers.clear();
    this->onCreation= false;
    this->isStarted = false;
    strcpy(this->id,"");
    this->round = 0;
    this->name = "";
    this->gameInstance = nullptr;

  //  gameMutex.unlock();

}



