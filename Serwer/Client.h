//
// Created by blazej on 29.04.2020.
//

#ifndef SK_PROJ_CLIENT_H
#define SK_PROJ_CLIENT_H
#include "EpollContainer.h"

class Client : public EpollContainer {

    void deleteClient();
public:
    Client();
    Client(int fd);
    ~Client() override;

    void handleEvent(uint32_t events) override;
};


#endif //SK_PROJ_CLIENT_H
