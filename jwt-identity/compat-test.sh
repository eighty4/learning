#!/bin/bash

set -e

echo -e "generating keys"
openssl genrsa -out jwt.private.pem 2048
openssl rsa -in jwt.private.pem -pubout -outform PEM -out jwt.public.pem
KEY_DIR=$(pwd)
echo -e "\033[0;32m\xE2\x9C\x94\033[0m keys generated"

echo -e "\nbuilding golang binaries"
cd golang-jwt
go build -o jwt-sign jwt-keys.go jwt-sign.go
go build -o jwt-verify jwt-keys.go jwt-verify.go
cd ..
echo -e "\033[0;32m\xE2\x9C\x94\033[0m binaries built"

set +e

go_sign() {
  MY_JWT=$(./golang-jwt/jwt-sign adam.be.g84d@gmail.com)
  check_signing 'golang'
}

go_verify() {
  ./golang-jwt/jwt-verify $MY_JWT > /dev/null
  check_verifying 'golang'
}

node_sign() {
  MY_JWT=$(node nodejs-jwt/jwt-sign.js adam.be.g84d@gmail.com)
  check_signing 'nodejs'
}

node_verify() {
  node nodejs-jwt/jwt-verify.js $MY_JWT > /dev/null
  check_verifying 'nodejs'
}

check_signing() {
  check_cmd "$1 signing"
}

check_verifying() {
  check_cmd "$1 verifying"
}

check_cmd() {
  if [ $? -eq 0 ]; then
    echo -e "\033[0;32m\xE2\x9C\x94\033[0m $1"
  else
    echo -e "\033[0;31m\xE2\x9D\x8C\033[0m $1"
    exit 1
  fi
}

echo -e "\ntest golang sign > golang verify"
go_sign
#echo $MY_JWT
go_verify

echo -e "\ntest nodejs sign > nodejs verify"
node_sign
#echo $MY_JWT
node_verify

echo -e "\ntest golang sign > nodejs verify"
go_sign
#echo $MY_JWT
node_verify

echo -e "\ntest nodejs sign > golang verify"
node_sign
#echo $MY_JWT
go_verify
