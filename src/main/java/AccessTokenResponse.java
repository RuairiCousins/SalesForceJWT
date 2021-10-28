import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {

    private String access_token;
    private String scope;
    private String instance_url;
    private String id;
    private String token_type;


    @Override
    public String toString() {
        return "\nAccessTokenResponse{\n" +
                "access_token: " + access_token + "\n" +
                "scope: " + scope + "\n" +
                "instance_url: " + instance_url + "\n" +
                "id: " + id + "\n" +
                "token_type: " + token_type + "\n" +
                '}';
    }
}
