# Hands-on MongoDB Atlas

Demo repository to try the free-tier MongoDB Atlas cloud offering. To get this
project running, get a free database instance at https://cloud.mongodb.com
Then configure the network access IP whitelist so you can connect to your DB instance from you IP address. Also, create a DB user with password.

## Running

```bash
$ ./gradlew kick ass
$ ./gradlew run --args="--password <<YOUR PWD HERE>>"
```

## Maintainer

M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

## License

This software is provided under the MIT open source license, read the `LICENSE`
file for details.
