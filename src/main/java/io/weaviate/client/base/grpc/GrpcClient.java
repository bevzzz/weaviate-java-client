package io.weaviate.client.base.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.weaviate.client.Config;
import io.weaviate.client.grpc.protocol.v1.WeaviateGrpc;
import java.util.Map;

public class GrpcClient {

  public static WeaviateGrpc.WeaviateBlockingStub create(Config config) {
    Metadata headers = new Metadata();
    if (config.getHeaders() != null) {
      for (Map.Entry<String, String> e : config.getHeaders().entrySet()) {
        headers.put(Metadata.Key.of(e.getKey(), Metadata.ASCII_STRING_MARSHALLER), e.getValue());
      }
    }
    ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forTarget(getAddress(config));
    if (config.isGRPCSecured()) {
      channelBuilder = channelBuilder.useTransportSecurity();
    } else {
      channelBuilder.usePlaintext();
    }
    ManagedChannel channel = channelBuilder.build();
    WeaviateGrpc.WeaviateBlockingStub blockingStub = WeaviateGrpc.newBlockingStub(channel);
    return blockingStub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(headers));
  }

  private static String getAddress(Config config) {
    if (config.getGRPCHost() != null) {
      String host = config.getGRPCHost();
      if (host.contains(":")) {
        return host;
      }
      if (config.isGRPCSecured()) {
        return String.format("%s:443", host);
      }
      return String.format("%s:80", host);
    }
    return "";
  }
}