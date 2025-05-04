FROM ubuntu:latest
LABEL authors="sonnvt"

ENTRYPOINT ["top", "-b"]