#ifndef EIGHTY4_LEARN_OPENSSL_TCP_SOCKET_H
#define EIGHTY4_LEARN_OPENSSL_TCP_SOCKET_H

namespace tcp {

    const int FAILED = -1;

    const int RECOVERABLE = -2;

    int SocketListen(int port);

    int SocketAccept(int socket);

    int SocketConnect(const char *host, int port);

    int SocketRead(int socket, char *read_buf, int read_lim);

    int SocketSend(int socket, const char *bytes, int bytes_len);

    int SocketError();

    void CloseSocket(int socket);

}

#endif // EIGHTY4_LEARN_OPENSSL_TCP_SOCKET_H
