package main

import (
	"crypto/rsa"
	"github.com/golang-jwt/jwt/v4"
	"log"
	"os"
)

func readPrivateKey() *rsa.PrivateKey {
	key, err := jwt.ParseRSAPrivateKeyFromPEM(readKey("jwt.private.pem"))
	if err != nil {
		log.Fatalln("could not parse private key: " + err.Error())
	}
	return key
}

func readPublicKey() *rsa.PublicKey {
	key, err := jwt.ParseRSAPublicKeyFromPEM(readKey("jwt.public.pem"))
	if err != nil {
		log.Fatalln("could not parse public key: " + err.Error())
	}
	return key
}

func readKey(filename string) []byte {
	file, err := os.ReadFile(filename)
	if err != nil {
		log.Fatalln("could not read " + filename + ": " + err.Error())
	}
	return file
}
