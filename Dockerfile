FROM openjdk:17-jdk-slim

# Set environment variables
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH

# Install dependencies
RUN apt-get update && \
    apt-get install -y wget && \
    rm -rf /var/lib/apt/lists/*

# Download and install Tomcat 10
RUN wget https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.34/bin/apache-tomcat-10.1.34.tar.gz && \
    tar -xzf apache-tomcat-10.1.34.tar.gz && \
    mv apache-tomcat-10.1.34 $CATALINA_HOME && \
    rm apache-tomcat-10.1.34.tar.gz

# Copy WAR file to webapps directory
COPY target/SWP391-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/ROOT.war

# Create tomcat user
RUN useradd -r -s /bin/false tomcat && \
    chown -R tomcat:tomcat $CATALINA_HOME

# Expose port 8080
EXPOSE 8080

# Switch to tomcat user
USER tomcat

# Start Tomcat
CMD ["catalina.sh", "run"] 