package io.weaviate.client.v1.schema.api;

import org.apache.hc.core5.http.HttpStatus;

import io.weaviate.client.Config;
import io.weaviate.client.base.BaseClient;
import io.weaviate.client.base.ClientResult;
import io.weaviate.client.base.Response;
import io.weaviate.client.base.Result;
import io.weaviate.client.base.http.HttpClient;
import io.weaviate.client.base.util.UrlEncoder;
import io.weaviate.client.v1.schema.model.Tenant;

public class TenantsCreator extends BaseClient<Tenant[]> implements ClientResult<Boolean> {

  private String className;
  private Tenant[] tenants;

  public TenantsCreator(HttpClient httpClient, Config config) {
    super(httpClient, config);
  }

  public TenantsCreator withClassName(String className) {
    this.className = className;
    return this;
  }

  public TenantsCreator withTenants(Tenant... tenants) {
    this.tenants = tenants;
    return this;
  }

  @Override
  public Result<Boolean> run() {
    return createTenants(tenants);
  }

  private Result<Boolean> createTenants(Tenant[] tenants) {
    String path = String.format("/schema/%s/tenants", UrlEncoder.encodePathParam(className));
    Response<Tenant[]> resp = sendPostRequest(path, tenants, Tenant[].class);
    return new Result<>(resp.getStatusCode(), resp.getStatusCode() == HttpStatus.SC_OK, resp.getErrors());
  }
}
