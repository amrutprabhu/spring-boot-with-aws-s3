package com.amrut.prabhu;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class SpringBootWithS3ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("s3://mybucket/samplefile.txt")
    private Resource s3SampleFile;

    @Container
    private static LocalStackContainer localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
            // to create secrets on startup
            .withCopyFileToContainer(MountableFile.forClasspathResource("script.sh", 0775),
                    "/etc/localstack/init/ready.d/")
            .withServices(LocalStackContainer.Service.S3);

    @BeforeAll
    static void beforeAll() throws IOException, InterruptedException {
        System.setProperty("spring.cloud.aws.s3.endpoint", "http://s3.localhost.localstack.cloud:" + localStackContainer.getMappedPort(4566));
        System.setProperty("spring.cloud.aws.s3.region", "eu-central-1");
        System.setProperty("spring.cloud.aws.credentials.access-key", "none");
        System.setProperty("spring.cloud.aws.credentials.secret-key", "none");
        // needed to set this, for the GitHub pipeline to succeed.
        System.setProperty("spring.cloud.aws.region.static", "eu-central-1");

    }

    @Test
    void contextLoads() throws Exception {

        String data = """
                        {
                        "name" : "amrut"
                        }
                        """;
        //Given
        Assertions.assertThat(s3SampleFile.exists()).isFalse();

        mockMvc.perform(MockMvcRequestBuilders.post("/data")
                .content(data))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("amrut")));

        mockMvc.perform(MockMvcRequestBuilders.get("/data"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("amrut")));

    }

}
