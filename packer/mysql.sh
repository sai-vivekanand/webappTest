#!/bin/bash

# Save this as setup_mysql.sh

# Set the new password
NEW_PASSWORD="Mnblkjpoi@123"

# Run the expect script to configure mysql_secure_installation
/usr/bin/expect <<EOF
set timeout 10
spawn sudo mysql_secure_installation

expect "Enter password for user root:"
send "\r"
expect "New password:"
send "$NEW_PASSWORD\r"
expect "Re-enter new password:"
send "$NEW_PASSWORD\r"
expect "Change the password for root ? \((Press y|Y for Yes, any other key for No) :"
send "y\r"
expect "New password:"
send "$NEW_PASSWORD\r"
expect "Re-enter new password:"
send "$NEW_PASSWORD\r"
expect "Remove anonymous users? (Press y|Y for Yes, any other key for No) :"
send "y\r"
expect "Disallow root login remotely? (Press y|Y for Yes, any other key for No) :"
send "y\r"
expect "Remove test database and access to it? (Press y|Y for Yes, any other key for No) :"
send "y\r"
expect "Reload privilege tables now? (Press y|Y for Yes, any other key for No) :"
send "y\r"
expect eof
EOF

# Now run the mysql command to create a database
mysql -u root -p"$NEW_PASSWORD" -e "create database cloudSchema;"
echo  '****** mysql installed! *******'
