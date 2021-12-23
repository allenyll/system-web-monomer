FROM java:8
VOLUME /logs
ADD sw-admin.jar /
EXPOSE 8443
ENV JAVA_OPTS="-Xmx512m -Xms512m -Xmn128m -Xss64m"
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /sw-admin.jar
MAINTAINER allenyll
#设置时区
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
