# ---- build: compile all modules and produce runnable jars ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml test-dependencies-assembly.xml ./
COPY shared shared
COPY server server
COPY client client
# The cache mount keeps downloaded Maven libraries between builds, so rebuilds are fast
RUN --mount=type=cache,target=/root/.m2 mvn -q -B install -DskipTests && mvn -q -B -pl client dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/lib

# ---- runtime: just a Java runtime and the two jars ----
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV LANG=C.UTF-8
COPY --from=build /app/server/target/server-test-dependencies.jar server.jar
# The client runs from its own jar + runtime libraries. (Its all-in-one test jar also
# bundles the server, whose Main class would win over the client's.)
COPY --from=build /app/client/target/client.jar client/client.jar
COPY --from=build /app/client/target/lib client/lib
EXPOSE 8080
# The server by default; the client service overrides this command
CMD ["java", "-jar", "server.jar"]
