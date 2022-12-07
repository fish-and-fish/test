import java.util.HashMap;
import java.util.Map;

public class Face {

    public static void main(String[] args) throws Exception {
        // "YOUR_ACCESS_KEY_ID", "YOUR_ACCESS_KEY_SECRET" 的生成请参考https://help.aliyun.com/document_detail/175144.html
        // 如果您是用的子账号AccessKey，还需要为子账号授予权限AliyunVIAPIFullAccess，请参考https://help.aliyun.com/document_detail/145025.html
        String accessKeyId = Config.ACCESS_KEY;
        String accessSecret = Config.ACCESS_SECRET;
        SignHttp.DF.setTimeZone(new java.util.SimpleTimeZone(0, "GMT"));// 这里一定要设置GMT时区
        // 业务参数名字是大驼峰
        Map<String, String> params = new HashMap<String, String>();
        // 注意，使用签名机制调用，文件参数目前仅支持上海OSS链接，可参考https://help.aliyun.com/document_detail/175142.html文档将文件放入上海OSS中。如果是其他情况（如本地文件或者其他链接），请先显式地转换成上海OSS链接，可参考https://help.aliyun.com/document_detail/155645.html文档中的方式二，但该方案不支持web前端环境直接调用。
        params.put("ImageURLA", "http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/CompareFace/CompareFace-right1.png");
        params.put("ImageURLB", "http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/CompareFace/CompareFace-left1.png");
        // API Action，能力名称，请参考具体算法文档详情页中的Action参数，这里以图像超分为例：https://help.aliyun.com/document_detail/151947.html
        String action = "CompareFace";
        SignHttp.execute(action, accessKeyId, accessSecret, params);

        params = new HashMap<String, String>();
        params.put("ImageDataB", "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgQ****");
        params.put("ImageDataA", "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgK****");
        // API Action，能力名称，请参考具体算法文档详情页中的Action参数，这里以图像超分为例：https://help.aliyun.com/document_detail/151947.html
        SignHttp.execute(action, accessKeyId, accessSecret, params);


    }




}
