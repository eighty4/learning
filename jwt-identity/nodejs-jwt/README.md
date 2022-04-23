# Using Auth0's JWT libraries 

Example uses an RS256 signature with SHA-256 keys

Read a [super high level introduction](https://jwt.io/introduction) and a 
[deeper dive exploration](https://auth0.com/learn/json-web-tokens) from Auth0

Create a 2048 bit RSA256 private key
```
openssl genrsa -out jwt.private.pem 2048
```

From the private key, generate a public key
```
openssl rsa -in jwt.private.pem -pubout -outform PEM -out jwt.public.pem
```

Create a JWT, save it in an environment variable, and verify the JWT
```
MY_JWT=$(node jwt-sign.js adam.be.g84d@gmail.com)
echo $MY_JWT
node jwt-verify.js $MY_JWT
```

Create a JWT that expires immediately, wait a second, and verify the JWT
```
MY_JWT=$(node -e "console.log(require('./jwt-identity', '1 second').sign('adam.be.g84d@gmail.com'))")
node jwt-verify.js $MY_JWT
```
