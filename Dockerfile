FROM gradle:jdk11

RUN apt-get update && apt-get install bash

WORKDIR /opt/aeron

COPY ./ /opt/aeron

RUN gradle build

RUN rm -rf logs && mkdir logs

ENTRYPOINT ["sh", "commands.sh"]