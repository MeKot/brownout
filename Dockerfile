 from ubuntu:12.04

 RUN apt-get update && apt-get upgrade && apt-get clean

 RUN apt-get install -y git build-essential \
 		libboost1.48-all-dev libtinyxml-dev libcurl4-openssl-dev \
 		python apache2 libapache2-mod-php5 php5-common php5-mysql mysql-client mysql-server

RUN apt-get clean

RUN git clone https://github.com/MeKot/brownout.git
WORKDIR /brownout
RUN git checkout rubis-IK

RUN cp -r PHP /var/www/
RUN /bin/bash -c "mysqld_safe --skip-grant-tables &" \
    && sleep 5 && mysql -u root < ./database/rubis.sql && echo "Successfully ran SQL"

EXPOSE 80
CMD /bin/bash -c "mysqld_safe --skip-grant-tables &" && /usr/sbin/apache2ctl -D FOREGROUND
