//
// Created by blazej on 27.04.2020.
//

#ifndef SK_PROJ_EPOLLCONTAINER_H
#define SK_PROJ_EPOLLCONTAINER_H
#include <cstdint>
#include <netinet/in.h>

class EpollContainer {
protected:
    static int epollFd;
public:
    int fd;
    sockaddr_in sockAddr;
    virtual void handleEvent (uint32_t events) = 0;
    virtual ~EpollContainer() {}

    int getEpollFd(){
        return epollFd;
    }

    void setEpollFd(int _epollFd) {
        epollFd = _epollFd;
    }
};


#endif //SK_PROJ_EPOLLCONTAINER_H
