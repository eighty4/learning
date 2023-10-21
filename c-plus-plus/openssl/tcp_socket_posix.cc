#include "tcp_socket.h"

#include <iostream>

#include "arpa/inet.h"
#include "netdb.h"
#include "netinet/in.h"
#include "pcap/socket.h"
#include "sys/socket.h"
#include "sys/un.h"
#include "unistd.h"

namespace tcp {

    int SocketListen(int port) {
        std::cout << "creating server " << port << std::endl;

        auto socket_server = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        if (socket_server == INVALID_SOCKET) {
            std::cout << "socket function failed " << SocketError() << std::endl;
            perror("socket function failed");
            return FAILED;
        }

        sockaddr_in service{};
        service.sin_family = AF_INET;
        service.sin_addr.s_addr = inet_addr("127.0.0.1");
        service.sin_port = htons(port);

        auto result = bind(socket_server, (struct sockaddr *) &service, sizeof(service));
        if (result == INVALID_SOCKET) {
            std::cout << "bind function failed " << SocketError() << std::endl;
            perror("bind function failed");
            close(socket_server);
            return FAILED;
        }

        result = listen(socket_server, SOMAXCONN);
        if (result < 0) {
            std::cout << "listen function failed " << SocketError() << std::endl;
            close(socket_server);
            return FAILED;
        }

        return socket_server;
    }

    int SocketAccept(int socket) {
        auto result = accept(socket, nullptr, nullptr);
        if (result == INVALID_SOCKET) {
            perror("accept failed");
            close(socket);
            return FAILED;
        } else {
            int opt_val = 1;
            if (int err = setsockopt(result, SOL_SOCKET, SO_NOSIGPIPE, &opt_val, sizeof (opt_val)); err < 0) {
                std::cout << "failed to set SO_NOSIGPIPE on socket with error " << err << std::endl;
                exit(1);
            }
            return result;
        }
    }

    int SocketConnect(const char *host, int port) {
        struct sockaddr_in server_address{};
        memset(&server_address, 0, sizeof(server_address));
        server_address.sin_len = sizeof(server_address);
        server_address.sin_family = AF_INET;
        server_address.sin_port = htons(port);
        server_address.sin_addr.s_addr = INADDR_ANY;

        int sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
        if (sock == -1) {
            std::cout << "socket creation error " << SocketError() << std::endl;
            return FAILED;
        }
        if (connect(sock, (struct sockaddr *) &server_address, sizeof(server_address)) == -1) {
            std::cout << "connection error " << SocketError() << std::endl;
            close(sock);
            return FAILED;
        }

        return sock;
    }

    int SocketRead(int socket, char *read_buf, int read_len) {
        return recv(socket, read_buf, read_len, 0);
    }

    int SocketSend(int socket, const char *data, const int data_len) {
        return send(socket, data, data_len, 0);
    }

    int SocketError() {
        return errno;
    }

    void CloseSocket(int socket) {
        close(socket);
    }
}
