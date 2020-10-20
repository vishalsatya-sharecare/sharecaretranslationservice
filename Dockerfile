FROM nexus.admin.sharecare.com/openjdk-8u151-jre-alpine-bash:1.0
# TODO: Change to the microservice name
COPY dhs-audience-service  .
COPY run-cluster.sh .
ENTRYPOINT ["./run-cluster.sh"]