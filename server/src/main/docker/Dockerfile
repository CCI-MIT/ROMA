FROM tomcat:8-jre8

RUN	rm ${CATALINA_HOME}/webapps/ROOT/* -rf 

ENV spring.profiles.active=container
ENV ROMA_SERVER roma-server
ENV ROMA_SERVER_WAR ${ROMA_SERVER}-0.2.0.war

ADD $ROMA_SERVER_WAR ${CATALINA_HOME}/webapps/${ROMA_SERVER}.war
RUN touch ${CATALINA_HOME}/webapps/${ROMA_SERVER}.war


EXPOSE 8080
CMD ["catalina.sh", "run"]
