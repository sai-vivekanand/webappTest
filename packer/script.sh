#sudo yum update
#sudo yum upgrade
#sudo amazon-linux-extras install -y nginx1
echo Start Java Installation
sudo yum install maven -y --exclude=java-1.8.0*
sudo yum install java-17-openjdk -y
#JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-17.0.6.0.9-0.3.ea.el8.x86_64" | sudo tee -a /etc/profile
#echo "export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64" >>~/.bashrc
#echo "export PATH=$PATH:$JAVA_HOME/bin" >>~/.bashrc
#echo "export JAVA_HOME=$JAVA_HOME" | sudo tee -a /etc/profile
echo 'export PATH=$PATH:$JAVA_HOME/bin' | sudo tee -a /etc/profile
source /etc/profile

echo Java Location
java --version
echo completed Java Installation

sudo dnf install mysql-server -y
sudo systemctl start mysqld
sudo systemctl enable mysqld
sleep 10
sudo yum install -y expect

sudo chmod 770 /opt/cloud-app-0.0.1-SNAPSHOT.jar
sudo cp /opt/webservice.service /etc/systemd/system
sudo chmod 770 /etc/systemd/system/webservice.service
sudo systemctl start webservice.service
sudo systemctl enable webservice.service
sudo systemctl restart webservice.service
sudo systemctl status webservice.service
sudo groupadd -f csye6225
sudo useradd -r -g csye6225 -s /usr/sbin/nologin csye6225 || true

# Now set the ownership of the /opt/application directory
sudo chown -R csye6225:csye6225 /opt

echo '****** Copied webservice! *******'
