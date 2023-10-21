#include "tls_socket.h"
#include "tcp_socket.h"
#include <openssl/ssl3.h>
#include <openssl/err.h>
#include <iostream>
#include <fmt/format.h>

std::unique_ptr<tls::TlsSocket> tls::TlsSocket::Create(int port) {
    SSL_CTX *ssl_ctx = SSL_CTX_new(TLS_client_method());
    if (!ssl_ctx) {
        std::cout << "failed creating ssl ctx" << std::endl;
        return nullptr;
    }
    SSL *ssl = SSL_new(ssl_ctx);
    if (!ssl) {
        std::cout << "failed creating ssl" << std::endl;
        SSL_CTX_free(ssl_ctx);
        return nullptr;
    }
    int cert_result = SSL_CTX_use_certificate_file(ssl_ctx, "learning.eighty4.local.crt", SSL_FILETYPE_PEM);
    if (cert_result != 1) {
        int error_code = SSL_get_error(ssl, cert_result);
        auto error_buf = new char[256];
        ERR_error_string_n(error_code, error_buf, 256);
        std::cout << fmt::format("failed loading cert file, error: {}", error_buf) << std::endl;
        SSL_free(ssl);
        SSL_CTX_free(ssl_ctx);
        return nullptr;
    }
    int socket = tcp::SocketConnect("127.0.0.1", port);
    if (socket == tcp::FAILED || socket == tcp::RECOVERABLE) {
        std::cout << "failed creating socket connection" << std::endl;
        SSL_free(ssl);
        SSL_CTX_free(ssl_ctx);
        return nullptr;
    }
    int connect_result = SSL_connect(ssl);
    if (connect_result != 1) {
        int error_code = SSL_get_error(ssl, connect_result);
        std::cout << fmt::format("failed performing tls handshake, error: {}", error_code) << std::endl;
        SSL_free(ssl);
        SSL_CTX_free(ssl_ctx);
        return nullptr;
    }
    return std::make_unique<tls::TlsSocket>(socket, ssl, ssl_ctx);
}

tls::TlsSocket::TlsSocket(int socket, SSL *ssl, SSL_CTX *ssl_ctx_) : socket_(socket), ssl_(ssl), ssl_ctx_(ssl_ctx_) {}

tls::TlsSocket::~TlsSocket() {
    SSL_free(ssl_);
    tcp::CloseSocket(socket_);
    SSL_CTX_free(ssl_ctx_);
}

int tls::TlsSocket::Send(char *bytes, int bytes_len) {
    return SSL_write(ssl_, bytes, bytes_len);
}
