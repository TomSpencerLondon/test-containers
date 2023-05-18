### Practical Integration Testing using Testcontainers

#### Where to use Testcontainers?

![image](https://github.com/TomSpencerLondon/LeetCode/assets/27693622/80c1ab5b-1324-46ff-893b-1610af23cd96)

Integration tests are like unit tests but have a wider scope as they cover multiple modules.
Mocks leave untested code at the system boundaries. Fakes also leave untested code at the system boundaries.
H2 databases are not full representations of your database. H2 is not able to emulate the target database system behaviour
100% accurately. 

Testcontainers allow us to mock external dependencies at system boundaries. With the help of test containers 
we can use docker containers for nginx, kafka, postgreSQL. The containers are controlled by the developer and integrated into the
test lifecycle. This can help find bugs earlier in the lifecycle.

#### Getting started

We require Docker version 9 or newer to run testcontainers. The CI build will also need a connection to a Docker daemon.
There is support for:
- AWS CodeBuild
- CircleCI
- DroneCI
- Gitlab CI
- Bitbucket Pipelines
- Tekton

#### Tooling requirements
To run test containers we need test frameworks like:
- JUnit5
- JUnit4
- Spock
Build tools could include:
- Maven
- Gradle
We also require a Java environment. Testcontainers can also be used with Node.

#### A first example

Our first test with a postgres test container is quite simple:

```java
public class ContainerTest {

    @Test
    void firstContainerTest() {
        var container = new GenericContainer<>(DockerImageName.parse("postgres"));
        container.start();
        assertThat(container.isRunning())
                .isTrue();
        container.stop();
    }
}
```
It includes a GenericContainer which is the default container for TestContainers which is effectively a wrapper around the 
Postgres database. We have access to start, stop and an isRunning() query on the TestContainer.

#### Junit5 integration
We can now add JUnit5 integration so that we don't need to take care of starting and stopping the testcontainers.
First we add the junit-jupiter integration for testcontainers:
```xml
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${testcontainers.version}</version>
    </dependency>
```

We can now add a test with less set up for starting and stopping the container:
```java
@Testcontainers
public class ContainerTest {

    @Container
    private final GenericContainer container = new GenericContainer<>(DockerImageName.parse("postgres"));

    @Test
    void firstContainerTest() {
        assertThat(container.isRunning())
                .isTrue();
    }
}

```

We can also add a static variable for a redis container:
```java
@Testcontainers
public class ContainerTest {

    @Container
    private final GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));

    @Container
    private static GenericContainer redis = new GenericContainer<>(DockerImageName.parse("redis"));

    @Test
    void firstContainerTest() {
        assertThat(postgres.isRunning())
                .isTrue();
    }

    @Test
    void secondContainerTest() {
        assertThat(postgres.isRunning())
                .isTrue();
        assertThat(redis.isRunning())
                .isTrue();

        System.out.println("Posgres Containernam: " + postgres.getContainerName());
        System.out.println("Redis Containernam: " + redis.getContainerName());
    }
}
```

We can also create an AbstractClass from which we extend in each of our tests:
```java
@Testcontainers
public class AbstractContainerTest {
    @Container
    protected final GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));

    @Container
    protected static GenericContainer redis = new GenericContainer<>(DockerImageName.parse("redis"));
}
```

When we run the tests we notice that each test class uses different instances of the testcontainer.
JUnit4 uses the following integration. We don't need the testcontainers jupiter dependency but just need to use JUnit4 on its own:

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.1</version>
    <scope>test</scope>
</dependency>
```

The ContainerTest for JUnit4 should look like this:
```java
public class ContainerTest {
    @Rule
    public GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));
    
    @ClassRule
    public static GenericContainer redis = new GenericContainer<>(DockerImageName.parse("redis"));
    
    @Test
    public void firstContainerTest() {
        assertTrue(postgres.isRunning());
        assertTrue(redis.isRunning());

        System.out.println("Postgres Containername: " + postgres.getContainerName());
        System.out.println("Redis Containername: " + redis.getContainerName());
    } 
}
```

#### Configure Logging
TestContainers uses SLF4J logging. We can add this to our project:
```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.11</version>
</dependency>
```
This gives very extensive logs for the test. We can add more fine-grained output by adding a logback-test.xml file in our test/resources:
https://www.testcontainers.org/supported_docker_environment/logging_config/

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT"/>
    </root>

    <logger name="org.testcontainers" level="INFO"/>
    <!-- The following logger can be used for containers logs since 1.18.0 -->
    <logger name="tc" level="INFO"/>
    <logger name="com.github.dockerjava" level="WARN"/>
    <logger name="com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.wire" level="OFF"/>
</configuration>
```

This gives a more focused test log output:
```bash
/usr/lib/jvm/graalvm-ce-java17-22.3.1/bin/java -ea -Didea.test.cyclic.buffer.size=1048576 -javaagent:/snap/intellij-idea-ultimate/430/lib/idea_rt.jar=38345:/snap/intellij-idea-ultimate/430/bin -Dfile.encoding=UTF-8 -classpath /home/tom/.m2/repository/org/junit/platform/junit-platform-launcher/1.9.0/junit-platform-launcher-1.9.0.jar:/home/tom/.m2/repository/org/junit/vintage/junit-vintage-engine/5.9.0/junit-vintage-engine-5.9.0.jar:/snap/intellij-idea-ultimate/430/lib/idea_rt.jar:/snap/intellij-idea-ultimate/430/plugins/junit/lib/junit5-rt.jar:/snap/intellij-idea-ultimate/430/plugins/junit/lib/junit-rt.jar:/home/tom/Projects/test-containers/test-containers-demo/target/test-classes:/home/tom/Projects/test-containers/test-containers-demo/target/classes:/home/tom/.m2/repository/org/junit/jupiter/junit-jupiter/5.9.0/junit-jupiter-5.9.0.jar:/home/tom/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.9.0/junit-jupiter-api-5.9.0.jar:/home/tom/.m2/repository/org/opentest4j/opentest4j/1.2.0/opentest4j-1.2.0.jar:/home/tom/.m2/repository/org/junit/platform/junit-platform-commons/1.9.0/junit-platform-commons-1.9.0.jar:/home/tom/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/home/tom/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.9.0/junit-jupiter-params-5.9.0.jar:/home/tom/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.9.0/junit-jupiter-engine-5.9.0.jar:/home/tom/.m2/repository/org/junit/platform/junit-platform-engine/1.9.0/junit-platform-engine-1.9.0.jar:/home/tom/.m2/repository/org/testcontainers/testcontainers/1.17.3/testcontainers-1.17.3.jar:/home/tom/.m2/repository/junit/junit/4.13.2/junit-4.13.2.jar:/home/tom/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/tom/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar:/home/tom/.m2/repository/org/apache/commons/commons-compress/1.21/commons-compress-1.21.jar:/home/tom/.m2/repository/org/rnorth/duct-tape/duct-tape/1.0.8/duct-tape-1.0.8.jar:/home/tom/.m2/repository/org/jetbrains/annotations/17.0.0/annotations-17.0.0.jar:/home/tom/.m2/repository/com/github/docker-java/docker-java-api/3.2.13/docker-java-api-3.2.13.jar:/home/tom/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.10.3/jackson-annotations-2.10.3.jar:/home/tom/.m2/repository/com/github/docker-java/docker-java-transport-zerodep/3.2.13/docker-java-transport-zerodep-3.2.13.jar:/home/tom/.m2/repository/com/github/docker-java/docker-java-transport/3.2.13/docker-java-transport-3.2.13.jar:/home/tom/.m2/repository/net/java/dev/jna/jna/5.8.0/jna-5.8.0.jar:/home/tom/.m2/repository/org/testcontainers/junit-jupiter/1.17.3/junit-jupiter-1.17.3.jar:/home/tom/.m2/repository/org/assertj/assertj-core/3.24.2/assertj-core-3.24.2.jar:/home/tom/.m2/repository/net/bytebuddy/byte-buddy/1.12.21/byte-buddy-1.12.21.jar:/home/tom/.m2/repository/ch/qos/logback/logback-classic/1.2.11/logback-classic-1.2.11.jar:/home/tom/.m2/repository/ch/qos/logback/logback-core/1.2.11/logback-core-1.2.11.jar com.intellij.rt.junit.JUnitStarter -ideVersion5 -junit5 @w@/tmp/idea_working_dirs_junit.tmp @/tmp/idea_junit.tmp -socket34715
15:58:15.191 [main] INFO  org.testcontainers.utility.ImageNameSubstitutor - Image name substitution will be performed by: DefaultImageNameSubstitutor (composite of 'ConfigurationFileImageNameSubstitutor' and 'PrefixingImageNameSubstitutor')
15:58:15.254 [main] INFO  org.testcontainers.dockerclient.DockerClientProviderStrategy - Loaded org.testcontainers.dockerclient.UnixSocketClientProviderStrategy from ~/.testcontainers.properties, will try it first
15:58:15.974 [main] INFO  org.testcontainers.dockerclient.DockerClientProviderStrategy - Found Docker environment with local Unix socket (unix:///var/run/docker.sock)
15:58:15.979 [main] INFO  org.testcontainers.DockerClientFactory - Docker host IP address is localhost
15:58:16.011 [main] INFO  org.testcontainers.DockerClientFactory - Connected to docker: 
  Server Version: 23.0.3
  API Version: 1.42
  Operating System: Ubuntu 22.10
  Total Memory: 15859 MB
15:58:16.062 [main] INFO  🐳 [testcontainers/ryuk:0.3.3] - Creating container for image: testcontainers/ryuk:0.3.3
15:58:16.255 [main] INFO  🐳 [testcontainers/ryuk:0.3.3] - Container testcontainers/ryuk:0.3.3 is starting: 8533e41402762903db676c55a6a2c97e27ad269bb72a1321317b47ac0144fb07
15:58:16.888 [main] INFO  🐳 [testcontainers/ryuk:0.3.3] - Container testcontainers/ryuk:0.3.3 started in PT0.859064252S
15:58:16.894 [main] INFO  org.testcontainers.utility.RyukResourceReaper - Ryuk started - will monitor and terminate Testcontainers containers on JVM exit
15:58:16.894 [main] INFO  org.testcontainers.DockerClientFactory - Checking the system...
15:58:16.895 [main] INFO  org.testcontainers.DockerClientFactory - ✔︎ Docker server version should be at least 1.6.0
15:58:16.914 [main] INFO  🐳 [redis:latest] - Creating container for image: redis:latest
15:58:16.954 [main] INFO  🐳 [redis:latest] - Container redis:latest is starting: bb04af41f283feda1f914d974303b9a955a469c4ffe2583930716c5b93daeab6
15:58:17.385 [main] INFO  🐳 [redis:latest] - Container redis:latest started in PT0.490280752S
15:58:17.415 [main] INFO  🐳 [postgres:latest] - Creating container for image: postgres:latest
15:58:17.465 [main] INFO  🐳 [postgres:latest] - Container postgres:latest is starting: 6ff23ac6d37f6e46af0e9a01617846ac982b9db7650ab9d8c776e5c9f46958b8
15:58:17.822 [main] INFO  🐳 [postgres:latest] - Container postgres:latest started in PT0.412317696S
15:58:18.114 [main] INFO  🐳 [postgres:latest] - Creating container for image: postgres:latest
15:58:18.155 [main] INFO  🐳 [postgres:latest] - Container postgres:latest is starting: 1f42041a85ea6936583e6584bdeef9c7a2f6dbe75eb96df9a62f8554eef4ae69
15:58:18.577 [main] INFO  🐳 [postgres:latest] - Container postgres:latest started in PT0.463806289S
Posgres Containernam: /vigorous_gates
Redis Containernam: /infallible_napier
15:58:19.108 [main] INFO  🐳 [redis:latest] - Creating container for image: redis:latest
15:58:19.149 [main] INFO  🐳 [redis:latest] - Container redis:latest is starting: b25a83a4b3ad90e582f1519bac081f9aa16caa8861e021982216980499bb739f
15:58:19.538 [main] INFO  🐳 [redis:latest] - Container redis:latest started in PT0.430599284S
15:58:19.545 [main] INFO  🐳 [postgres:latest] - Creating container for image: postgres:latest
15:58:19.588 [main] INFO  🐳 [postgres:latest] - Container postgres:latest is starting: d88355b92850377e904f71b11b65df02ecaed71c192b4be9db19b070c0dc0c46
15:58:19.983 [main] INFO  🐳 [postgres:latest] - Container postgres:latest started in PT0.438084662S
15:58:20.263 [main] INFO  🐳 [postgres:latest] - Creating container for image: postgres:latest
15:58:20.316 [main] INFO  🐳 [postgres:latest] - Container postgres:latest is starting: 73e321a39e5b4cb43b89eb3ecfe073e3c20cfb265aa1b536ae5a73fae2768e98
15:58:20.754 [main] INFO  🐳 [postgres:latest] - Container postgres:latest started in PT0.49180326S
Posgres Containernam: /focused_meitner
Redis Containernam: /angry_kilby
```


#### Port mapping
We can add a Spring JDBC template for our next demo:

```java
public class TaskListService {

    private final DataSource dataSource;

    public TaskListService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public TaskListEntry insert(TaskListEntry entry) {
        final var jdbcTemplate = new JdbcTemplate(dataSource);
        final String id = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO TASKLIST_ENTRIES values(?, ?)", id, entry.getText());

    }

}
```

We have added the following dependencies:

```xml
 <dependencies>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>5.9.0</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>testcontainers</artifactId>
      <version>${testcontainers.version}</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>${testcontainers.version}</version>
    </dependency>
    <dependency>
      <groupId>ch.qos.logback</groupId>
      <artifactId>logback-classic</artifactId>
      <version>1.2.11</version>
    </dependency>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-jdbc</artifactId>
      <version>5.3.22</version>
    </dependency>
    <dependency>
      <groupId>com.zaxxer</groupId>
      <artifactId>HikariCP</artifactId>
      <version>5.0.1</version>
    </dependency>
    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <version>42.4.2</version>
    </dependency>

  </dependencies>

```

We add a test:
```java
@Testcontainers(disabledWithoutDocker = true)
public class TaskListServiceTest {

    @Container
    private GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres:12.12"))
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "postgres")
            .withEnv("POSTGRES_DB", "tasklist")
            .withClasspathResourceMapping("database/INIT.sql",
                    "/docker-entrypoint-initdb.d/INIT.sql",
                    BindMode.READ_ONLY)
            .withExposedPorts(5432);

//    @Container
//    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.12")
//            .withUsername("postgres")
//            .withPassword("postgres")
//            .withInitScript("database/INIT.sql")
//            .withDatabaseName("tasklist")
//            .withExposedPorts(5432);

    private TaskListService service;

    @BeforeEach
    void setUp() {
        final var dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("tasklist");
        dataSource.setUser("postgres");
        dataSource.setPortNumbers(new int[]{postgres.getMappedPort(5432)});
        dataSource.setPassword("postgres");

        service = new TaskListService(dataSource);
    }

    @Test
    void shouldInsertNewEntries() {
        TaskListEntry entry = service.insert(new TaskListEntry("write tests"));
        assertNotNull(entry);
        assertNotNull(entry.getId());
    }
}

```