FROM openjdk:17 as build

#NON-ROOT USER 
ARG USERNAME=drsuser
ARG USER_UID=3000
ARG USER_GID=$USER_UID

# Create the user
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && apt-get update \
    && apt-get install -y sudo \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME 


USER $USERNAME

COPY . /drs/

WORKDIR /drs

RUN microdnf install dos2unix

RUN dos2unix mvnw
RUN chmod +x mvnw
RUN dos2unix .mvn/wrapper/maven-wrapper.properties

RUN ./mvnw clean install -Dmaven.test.skip=true

RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM bellsoft/liberica-openjdk-alpine:17.0.3.1-2
ARG DEPENDENCY=/drs/target/dependency

COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "net.catenax.dapsreg.DapsregApplication"]
EXPOSE 8080
