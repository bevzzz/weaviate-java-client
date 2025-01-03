package io.weaviate.integration.client.batch;

import io.weaviate.client.Config;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.batch.model.BatchDeleteOutput;
import io.weaviate.client.v1.batch.model.BatchDeleteResponse;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.data.replication.model.ConsistencyLevel;
import io.weaviate.client.v1.filters.WhereFilter;
import io.weaviate.integration.client.WeaviateDockerCompose;
import io.weaviate.integration.client.WeaviateTestGenerics;
import io.weaviate.integration.tests.batch.ClientBatchDeleteTestSuite;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class ClientBatchDeleteTest {

  private WeaviateClient client;
  private final WeaviateTestGenerics testGenerics = new WeaviateTestGenerics();


  @ClassRule
  public static WeaviateDockerCompose compose = new WeaviateDockerCompose();

  @Before
  public void before() {
    String httpHost = compose.getHttpHostAddress();
    Config config = new Config("http", httpHost);

    client = new WeaviateClient(config);
    testGenerics.createTestSchemaAndData(client);
  }

  @After
  public void after() {
    testGenerics.cleanupWeaviate(client);
  }

  Supplier<Result<List<WeaviateObject>>> getObjects = () -> client.data().objectsGetter().run();

  @Test
  public void testBatchDeleteDryRunVerbose() {
    Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete = (whereFilter) -> client.batch().objectsBatchDeleter()
            .withDryRun(true)
            .withOutput(BatchDeleteOutput.VERBOSE)
            .withClassName("Pizza")
            .withWhere(whereFilter)
            .run();
    ClientBatchDeleteTestSuite.testBatchDeleteDryRunVerbose(getObjects, batchDelete);
  }

  @Test
  public void testBatchDeleteDryRunMinimal() {
    Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete = (whereFilter) -> client.batch().objectsBatchDeleter()
            .withDryRun(true)
            .withOutput(BatchDeleteOutput.MINIMAL)
            .withClassName("Soup")
            .withWhere(whereFilter)
            .run();
    ClientBatchDeleteTestSuite.testBatchDeleteDryRunMinimal(getObjects, batchDelete);
  }

  @Test
  public void testBatchDeleteNoMatchWithDefaultOutputAndDryRun() {
    Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete = (whereFilter) -> client.batch().objectsBatchDeleter()
            .withClassName("Pizza")
            .withWhere(whereFilter)
            .withConsistencyLevel(ConsistencyLevel.QUORUM)
            .run();
    ClientBatchDeleteTestSuite.testBatchDeleteNoMatchWithDefaultOutputAndDryRun(getObjects, batchDelete);
  }

  @Test
  public void testBatchDeleteAllMatchesWithDefaultDryRun() {
    Function<WhereFilter, Result<BatchDeleteResponse>> batchDelete = (whereFilter) -> client.batch().objectsBatchDeleter()
            .withOutput(BatchDeleteOutput.VERBOSE)
            .withClassName("Pizza")
            .withWhere(whereFilter)
            .withConsistencyLevel(ConsistencyLevel.QUORUM)
            .run();
    ClientBatchDeleteTestSuite.testBatchDeleteAllMatchesWithDefaultDryRun(getObjects, batchDelete);
  }
}
