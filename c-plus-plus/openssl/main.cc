#include "tcp_socket.h"
#include "tls_socket.h"
#include <iostream>

int main() {
    auto socket = tls::TlsSocket::Create(8080);
    if (socket) {

    }
}

//int main() {
//    int socket_server = tcp::SocketListen(8080);
//    int socket = tcp::SocketAccept(socket_server);
//    int read_len = 512;
//    auto buf = new char[read_len + 1];
//    tcp::SocketRead(socket, buf, read_len);
//    std::cout << buf << std::endl;
//    return 0;
//}
