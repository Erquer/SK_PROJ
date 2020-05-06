//
// Created by blazej on 27.04.2020.
//

#include "Game.h"

Game *Game::gameInstance;

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

const std::vector<Player *> &Game::getPlayerAnswers() const {
    return playerAnswers;
}

void Game::setPlayerAnswers(const std::vector<Player *> &playerAnswers) {
    Game::playerAnswers = playerAnswers;
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

}

Game::~Game() {

}

void Game::resetPoints(bool reset) {



}

bool Game::isGameOwnerSet() const {
    return this->getOwner() != nullptr;
}

