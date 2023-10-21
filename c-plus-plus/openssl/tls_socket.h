#include "tcp_socket.h"
#include <memory>
#include <openssl/ssl3.h>

namespace tls {

    class TlsSocket {
    public:
        static std::unique_ptr<TlsSocket> Create(int port);

        TlsSocket(int socket, SSL *ssl, SSL_CTX *ssl_ctx_);

        ~TlsSocket();

        int Send(char *bytes, int bytes_len);

    private:
        int socket_;
        SSL *ssl_;
        SSL_CTX *ssl_ctx_;
    };
}
