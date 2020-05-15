#include <iostream>
#include <sys/socket.h>
#include <string>
#include <error.h>
#include <sys/epoll.h>
#include <csignal>

#include "Serwer/utils.h"
#include "Serwer/Server.h"
#include "Serwer/Game.h"


Server *server;
void ctrl_c(int);
void handleEpollEvents(Server *server);

int main(int argc, char **argv) {
    if(argc != 3){
        perror("Error: Missing arguments; enter <PORT> <IP address> to properly run server");
    }

    server = new Server(argc, argv);

    int res = listen(server->fd, SOMAXCONN);
    if (res)
        error(1, errno, "Failed to execute 'listen'\n");

    // graceful ctrl+c exit
    signal(SIGINT, ctrl_c);
    // prevent dead sockets from throwing pipe errors on write
    signal(SIGPIPE, SIG_IGN);

    server->setEpollFd(epoll_create1(0));
    epoll_event ee{EPOLLIN, {.ptr=server}};
    epoll_ctl(server->getEpollFd(), EPOLL_CTL_ADD, server->fd, &ee);

    handleEpollEvents(server);

    server->closeServer();

    std::cout << "Hello, World!" << std::endl;
    return 0;
}

void handleEpollEvents(Server *server){
    epoll_event ee;
    while(1){
        if(-1 == epoll_wait(server->getEpollFd(), &ee, 1, -1)){
            error(1, errno,"Server Epoll Function Failed \n");
            server->closeServer();
            exit(0);
        }
        ((EpollContainer *) ee.data.ptr)->handleEvent(ee.events);

    }
}

void ctrl_c(int){
    server->closeServer();
    printf("\nClosing server - caused by ctrl_c\n");
    exit(0);
}