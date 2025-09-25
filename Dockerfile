FROM fedora:41
WORKDIR /app
COPY target/oda-config-service /app

CMD ["./oda-config-service"]
