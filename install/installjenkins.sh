apt update && apt upgrade
apt install openjdk-17-jdk-headless git
curl https://pkg.jenkins.io/debian/jenkins.io-2023.key | gpg --dearmor | tee /usr/share/keyrings/jenkins-archive-keyring.gpg >/dev/null
echo "deb [signed-by=/usr/share/keyrings/jenkins-archive-keyring.gpg] https://pkg.jenkins.io/debian binary/" >> /etc/apt/sources.list.d/jenkins.list
apt update && apt install jenkins
mkdir /home/pi/build
mkdir /home/pi/deploy
echo "goto http://<IP>:8080"
echo "install plugins: git hub, ssh, ansible"