package community.mingle.api.configuration;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.amazonaws.regions.Regions.AP_NORTHEAST_2;
import static community.mingle.api.configuration.ProjectBaseConfiguration.Profile.*;

@Configuration
public class AmazonS3Config {

    public static final String S3_CLIENT = "amazonS3";

    @Bean(S3_CLIENT)
    @Profile(LOCAL)
    public AmazonS3Client amazonS3LocalClient() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(AP_NORTHEAST_2)
                .build();
    }

    @Bean(S3_CLIENT)
    @Profile({DEV, PROD})
    public AmazonS3Client amazonS3DevClient() {
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withRegion(AP_NORTHEAST_2)
                .build();
    }

}
