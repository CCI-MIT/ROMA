FROM ubuntu:trusty

ENV JDK_URL http://download.oracle.com/otn-pub/java/jdk/8u45-b14/jdk-8u45-linux-i586.tar.gz

ENV JAVA_HOME /java

ENV CATALINA_HOME /tomcat
ENV PATH $JAVA_HOME/bin:$CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"
WORKDIR $JAVA_HOME

# enable i386 architecture and install necessary libs
RUN dpkg --add-architecture i386 && apt-get update && apt-get -y install wget libc6:i386 libstdc++6:i386 unzip

# install i386 version of JDK
RUN wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" "$JDK_URL" -O jdk.tar.gz -o /dev/null && \
	tar zxf  jdk.tar.gz --strip-components=1 && \
	rm jdk.tar.gz
	
# download and install tomcat

WORKDIR $CATALINA_HOME
## see https://www.apache.org/dist/tomcat/tomcat-8/KEYS
RUN gpg --keyserver pool.sks-keyservers.net --recv-keys \
	05AB33110949707C93A279E3D3EFE6B686867BA6 \
	07E48665A34DCAFAE522E5E6266191C37C037D42 \
	47309207D818FFD8DCD3F83F1931D684307A10A5 \
	541FBE7D8F78B25E055DDEE13C370389288584E7 \
	61B832AC2F1C5A90F0F9B00A1C506407564C17A3 \
	79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED \
	9BA44C2621385CB966EBA586F72C284D731FABEE \
	A27677289986DB50844682F8ACB77FC2E86E29AC \
	A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 \
	DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 \
	F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE \
	F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23
	
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.5.14
ENV TOMCAT_TGZ_URL https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

RUN wget "$TOMCAT_TGZ_URL" -o /dev/null -O tomcat.tar.gz 
RUN	wget -SL "$TOMCAT_TGZ_URL.asc" -O tomcat.tar.gz.asc -o /dev/null 
RUN	gpg --verify tomcat.tar.gz.asc 
RUN	tar zxf tomcat.tar.gz --strip-components=1 
RUN	rm bin/*.bat 
RUN	rm tomcat.tar.gz* 
RUN	rm webapps/ROOT/* -rf 

ENV PANGAEA_SERVLET pangaea-servlet
ENV PANGAEA_SERVLET_WAR ${PANGAEA_SERVLET}-0.2.0.war

ADD $PANGAEA_SERVLET_WAR /tmp/$PANGAEA_SERVLET_WAR
RUN touch /tmp/$PANGAEA_SERVLET_WAR
RUN mkdir $CATALINA_HOME/webapps/$PANGAEA_SERVLET
run unzip /tmp/$PANGAEA_SERVLET_WAR -d $CATALINA_HOME/webapps/${PANGAEA_SERVLET}
ADD pangaea.properties $CATALINA_HOME/webapps/${PANGAEA_SERVLET}/WEB-INF/classes/

ADD vensim /vensim
ADD vensim/*vrf $CATALINA_HOME/bin/

ENV LD_LIBRARY_PATH /vensim:$LD_LIBRARY_PATH

WORKDIR $CATALINA_HOME/bin

EXPOSE 8080
CMD ["catalina.sh", "run"]
