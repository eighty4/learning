## Cipher suites

Set of algorithms to perform encryption and decryption including a key exchange algorithm, bulk encryption algorithm,
and a message authentication code algorithm.

Key exchange algorithm is used to exchange a key between two devices.

Bulk encryption algorithm is used to encrypt data.

The MAC algorithm provides data integrity checks to ensure data sent does not change in transit.

`TLS_AES_128_GCM_SHA256`

| Component   | Description         |
|-------------|---------------------|
| TLS         | Protocol            |
| AES_128_GCM | AEAD cipher mode    |
| SHA256      | HKDF hash algorithm |

- AEAD is Authenticated Encryption with Associated Data
- HKDF is an HMAC based key derivation function

## Key exchange algorithms

- DHE
- ECDHE
- Pre-shared key (PSK)
- PSK with DHE or ECDHE

https://en.wikipedia.org/wiki/Forward_secrecy
https://en.wikipedia.org/wiki/PKCS_12

## Creating a CSR

```bash
# create private key
openssl genrsa -out private-key.pem 2048

# create certificate signing request
openssl req -new -key private-key.pem -out csr.pem

# create public cert
openssl x509 -req -in csr.pem -signkey private-key.pem -out public-cert.pem
```

## Creating a root certificate

https://nodejs.org/api/tls.html#tlsssl-concepts
https://levelup.gitconnected.com/how-to-resolve-certificate-errors-in-nodejs-app-involving-ssl-calls-781ce48daded

```bash
# create private key
openssl genrsa -des3 -out ca.key 2048

# create root certificate
openssl req -x509 -new -nodes -key ca.key -sha256 -days 1825 -out ca.pem -subj "/CN=eighty4.local\/emailAddress=adam.be.g84d@gmail.com/C=US/ST=Illinois/L=Chicago/O=Eighty4/OU=Learning"

# macos add root cert to keychain
sudo security add-trusted-cert -d -r trustRoot -k "/Library/Keychains/System.keychain" ca.pem

# linux add root cert to trusted certs
sudo apt-get install -y ca-certificates
sudo cp ca.pem /usr/local/share/ca-certificates/ca.crt
sudo update-ca-certificates

# create private key
openssl genrsa -out learning.eighty4.local.key 2048

# create certificate signing request
openssl req -new -key learning.eighty4.local.key -out learning.eighty4.local.csr -subj "/CN=learning.eighty4.local\/emailAddress=adam.be.g84d@gmail.com/C=US/ST=Illinois/L=Chicago/O=Eighty4/OU=Learning"

cat <<\EOT >learning.eighty4.local.ext
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = learning.eighty4.local
EOT

# create 
openssl x509 -req -in learning.eighty4.local.csr -CA ca.pem -CAkey ca.key -CAcreateserial -out learning.eighty4.local.crt -days 825 -sha256 -extfile learning.eighty4.local.ext
```
