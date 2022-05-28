# Quboscanner

## Download

You can download Quboscaner in the [releases page](https://github.com/replydev/Quboscanner/releases/) \
Or alternatively you can [build from source](#building)

## Usage

Java 8+ is required to run the scanner.
Linux VPS/Dedicated System is strongly recommended for the best results.

To run it in GUI mode just double click it or run the binary in a terminal with no arguments.

To run it in CLI mode:

`java -Dfile.encoding=UTF-8 -jar qubo.jar -range 164.132.200.* -ports 25565-25577 -th 500 -ti 1000`

## Building
To build the software clone this repository with Intellij IDEA
and compile the binary using the integrated maven utility.

Alternatively you can manually install Maven and compile from commandline using these commands:
```
sudo apt install maven
git clone https://github.com/replydev/Quboscanner.git
cd Quboscanner
mvn clean compile assembly:single
```
You will find the compiled jar in "target/" directory.

## Contribution
Feel free to fork and make changes, any pull request is welcome.