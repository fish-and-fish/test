import java.net.URI;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SignHttp {

    // HTTPMethod推荐使用POST
    static final String API_HTTP_METHOD = "POST";
    // API访问域名，与类目相关，具体类目的API访问域名请参考：https://help.aliyun.com/document_detail/143103.html
    static final String API_ENDPOINT = "facebody.cn-shanghai.aliyuncs.com";
    // API版本，与类目相关，具体类目的API版本请参考：https://help.aliyun.com/document_detail/464194.html
    static final String API_VERSION = "2019-12-30";
    static final java.text.SimpleDateFormat DF = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void execute(String action, String accessKeyId, String accessSecret, Map<String, String> bizParams) throws Exception {
        java.util.Map<String, String> params = new java.util.HashMap<String, String>();
        // 1. 系统参数
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", java.util.UUID.randomUUID().toString());//防止重放攻击
        params.put("AccessKeyId", accessKeyId);
        params.put("SignatureVersion", "1.0");
        params.put("Timestamp", DF.format(new java.util.Date()));
        params.put("Format", "JSON");
        // 2. 业务API参数
        params.put("RegionId", "cn-shanghai");
        params.put("Version", API_VERSION);
        params.put("Action", action);
        if (bizParams != null && !bizParams.isEmpty()) {
            params.putAll(bizParams);
        }
        // 3. 去除签名关键字Key
        if (params.containsKey("Signature")) {
            params.remove("Signature");
        }
        // 4. 参数KEY排序
        java.util.TreeMap<String, String> sortParams = new java.util.TreeMap<String, String>();
        sortParams.putAll(params);
        // 5. 构造待签名的字符串
        java.util.Iterator<String> it = sortParams.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(params.get(key)));
        }
        String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(API_HTTP_METHOD).append("&");
        stringToSign.append(specialUrlEncode("/")).append("&");
        stringToSign.append(specialUrlEncode(sortedQueryString));
        String sign = sign(accessSecret + "&", stringToSign.toString());
        // 6. 签名最后也要做特殊URL编码
        String signature = specialUrlEncode(sign);
        // 添加直接做post请求的方法
        try {
            // 使用生成的 URL 创建POST请求
            URIBuilder builder = new URIBuilder("http://" + API_ENDPOINT + "/?Signature=" + signature + sortQueryStringTmp);
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            HttpClient httpclient = HttpClients.createDefault();
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                System.out.println("---");
                System.out.println(EntityUtils.toString(entity));
                System.out.println("---");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String specialUrlEncode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    public static String sign(String accessSecret, String stringToSign) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        return new sun.misc.BASE64Encoder().encode(signData);
    }
}
