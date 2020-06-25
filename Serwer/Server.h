//
// Created by blazej on 27.04.2020.
//

#ifndef SK_PROJ_SERVER_H
#define SK_PROJ_SERVER_H
#include <string>
#include <map>
#include "Player.h"
#include "EpollContainer.h"

class Server : public EpollContainer {
private:
    //mapa przechowująca informacje nt. graczy
    static std::map<std::string, Player*> playerList;
    static std::vector<Client*> clientsConnected;

private:

    //Tworzy socet dla serwera
    void crServerSocket(int argc, char **argv);


public:
    // ====  Konstruktor i Destruktor ====
    Server(int argc, char **argv);
    ~Server() override;

    // ==== publiczne metody ====

    void handleEvent(uint32_t event) override ;

    //dodawnie nowego klienta na serwer.
    static void addClient(Client *client);

    //zamykanie serwera. np przez ctrl + c
    void closeServer();

    static bool deleteClientFromServer(Client* client);
    //dodaj nowo połączonego gracza
    static void addPlayer(Player *player);

    //usuwanie gracza
    static void deletePlayer(std::string nick);

    //broadcast do wszystkich graczy.
    static void broadcast(char *buffer);

    //sprawdza, czy dany nick jest już na liście
    static bool checkList(std::string nick);


    // ====  Gettery i Settery ====
    static std::map<std::string, Player *> getPlayerList();

    static void setPlayerList(const std::map<std::string, Player *> &playerList);

    static void resetServer();

};


#endif //SK_PROJ_SERVER_H
