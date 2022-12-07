import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Robot {

    private static final String DING_DING_ROBOT_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    private static final String DING_DING_ROBOT_SEND_TOKEN = "d0e3dd5c621017b15a71b078062f769dd22147eb0d2e722831249ab2eb73ce02";

    public static void main(String[] args) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("msgtype", "text");

        Map<Object, Object> textMap = new HashMap<Object, Object>();
        textMap.put("content", "我就是我, @13606668139 是不一样的烟火");
        map.put("text", textMap);

        Map<Object, Object> atMap = new HashMap<Object, Object>();
        List<String> mobileList = new ArrayList<String>();
        mobileList.add("13606668139");
        atMap.put("atMobiles", mobileList);
        map.put("at", atMap);


        HttpTemplate.httpPostForObject(DING_DING_ROBOT_SEND_URL + DING_DING_ROBOT_SEND_TOKEN,
                map);
    }
}
