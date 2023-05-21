
import com.google.gson.JsonObject;
import com.vng.zalo.sdk.APIException;
import com.vng.zalo.sdk.oa.ZaloOaClient;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hienhh
 */
public class CheckStatusVideo {
    public static void main(String[] args) throws APIException {
        ZaloOaClient client = new ZaloOaClient();
        String access_token = "UA678v3tw0fwf8Sfa_pf0NwfWYUKvvH-EC2rMO2Ge0Gymhzxh_gqCqBRktZfmRD4KUMu7_Angsf6ceimnxY04NYrYKhAa98vLBRy7xJXv5ukrVy4WetdJsE1qcdjjSSoS93SQiMqrZDCdTfx-AIsE1Ibt1hvixPu5Ps98xQYuW5Ugl9Xnh3uFMQLxdk6k84T5E7XOPsm-Ju0bVeVYO7tEWEu_cA3i-mGDABvTOEdv2Oqj-u5teJZ9KELyNskXFqxHAELHVwllWrThhPtlORSNpRUj3tRXubDIRwd2FMSg41VdwTYtBQP94AzZMFkvE4KSzI1RPxMYWzstkShLsJPO5aHbU_j00";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("access_token", access_token);
        headers.put("token", "video_token_get_by_upload_video");
        
        JsonObject excuteRequest = client.excuteRequest("https://openapi.zalo.me/v2.0/article/upload_video/verify", "GET", null, null, headers, null);
        
        System.err.println(excuteRequest);
        
        System.exit(0);
    }
}
