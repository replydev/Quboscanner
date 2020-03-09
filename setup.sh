#quboScanner setup - @zreply on Telegram - quboscanner.tk

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, please insert rar package and restart"
	exit 0
fi

sudo apt update
sudo apt upgrade -y
sudo apt install default-jre -y
sudo apt install unrar -y
unrar $1
chmod +x start.sh
./start.sh -hwid