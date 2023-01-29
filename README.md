# Quboscanner
Very simple toy Minecraft Server Scanner.
It uses my library [mcping](https://github.com/replydev/mcping) to mass scan the servers.

## Download

You can download Quboscaner in the [releases page](https://github.com/replydev/Quboscanner/releases/) \
Or alternatively you can [build from source](#building)

## Usage

Java 19+ is required to run the scanner.
Linux VPS/Dedicated System is strongly recommended for the best results.

To run it:

`java -Dfile.encoding=UTF-8 -jar <jarName>.jar -i 164.132.200.* -p 25565-25577 -t 1000`

## Building

To build the software clone this repository with Intellij IDEA
and compile the binary using the integrated maven utility.

Alternatively you can manually install Maven and compile from commandline.
I advise using [SDKMAN](https://sdkman.io/) on *unix systems to install JDK 19 and Maven.

```
git clone https://github.com/replydev/Quboscanner.git
cd Quboscanner
mvn clean compile assembly:single
```

You will find the compiled jar in "target/" directory.

## Contribution

Feel free to fork and make changes, any pull request is welcome.