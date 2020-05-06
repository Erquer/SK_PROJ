//
// Created by blazej on 28.04.2020.
//

#ifndef SK_PROJ_GAMEOWNER_H
#define SK_PROJ_GAMEOWNER_H


#include "Client.h"

/**
 * Klasa użytkowika, który tworzy grę, nie ma pól, jedyne co robi, to wysyła na serwer pytania i odpowiedzi,
 * następnie może wysłać żądanie o informacji nt. graczy.
 */
class GameOwner : public Client{
public:
    void getScoresAndNicks();

    void handleEvent(uint32_t events) override;
    ~GameOwner() override;

};


#endif //SK_PROJ_GAMEOWNER_H
