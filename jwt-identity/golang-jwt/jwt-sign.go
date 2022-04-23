package main

import (
	"fmt"
	"github.com/golang-jwt/jwt/v4"
	"log"
	"os"
	"time"
)

func main() {
	if len(os.Args) != 2 {
		log.Fatalln("see README.md for command line parameters")
	} else {
		email := os.Args[1]
		token := jwt.NewWithClaims(jwt.SigningMethodRS256, jwt.MapClaims{
			"email": email,
			"iat":   time.Now().Unix(),
			"exp":   time.Now().Add(time.Hour * 24 * 7).Unix(),
		})
		tokenString, err := token.SignedString(readPrivateKey())
		if err != nil {
			log.Fatalln("jwt signing error: " + err.Error())
		}
		fmt.Println(tokenString)
	}
}
