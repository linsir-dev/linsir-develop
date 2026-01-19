FROM registry.cn-hangzhou.aliyuncs.com/dockerlinsir/linsir-dev:t01
MAINTAINER linsir
WORKDIR /app
COPY target/linsir-spring-springboot-deploy-jar-with-dependencies.jar /app/linsir-spring-springboot-deploy-jar-with-dependencies.jar
ENTRYPOINT java -jar /app/linsir-spring-springboot-deploy-jar-with-dependencies.jar com.linsir.springboot.deploy.DeployApplication
EXPOSE 9000
