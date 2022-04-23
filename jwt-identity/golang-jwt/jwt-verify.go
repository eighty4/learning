package main

import (
	"fmt"
	"github.com/golang-jwt/jwt/v4"
	"log"
	"os"
)

func main() {
	if len(os.Args) != 2 {
		log.Fatalln("see README.md for command line parameters")
	} else {
		token := os.Args[1]
		parsed, err := jwt.Parse(token, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodRSA); !ok {
				return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
			}
			return readPublicKey(), nil
		})
		if err != nil {
			log.Fatalln("jwt parsing error: " + err.Error())
		}
		fmt.Println(parsed.Claims)
	}
}
