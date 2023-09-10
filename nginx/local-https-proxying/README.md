# Setup an https frontend for local web development

- Add app domain names to /etc/hosts

```bash
echo "127.0.0.1       dev.colors.eighty4.tech\n127.0.0.1       dev.fonts.eighty4.tech" | sudo tee -a /etc/hosts > /local/null
```

- Create self-signed cert

```bash
openssl genrsa -out server.key 4096
openssl req -new -key server.key -out server.csr
openssl x509 -req -sha256 -days 1825 -in server.csr -signkey server.key -out server.crt
```

- Add server.crt to Keychain Access login items

- Install nginx and restart with copied nginx config

```bash
brew install nginx
cp nginx.conf /usr/local/etc/nginx/nginx.conf
nginx -t
nginx -s stop; nginx
```
