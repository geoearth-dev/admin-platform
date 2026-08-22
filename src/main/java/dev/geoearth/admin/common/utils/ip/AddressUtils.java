package dev.geoearth.admin.common.utils.ip;


/**
 * 获取地址类
 *
 * @author qdata
 */
public class AddressUtils
{

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip)
    {
        // 内网不查询
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
//        if (AniviaConfig.isAddressEnabled())
//        {
//            try
//            {
//                String rspStr = HttpUtils.sendGet(IP_URL, "ip=" + ip + "&json=true", Constants.GBK);
//                if (StringUtils.isEmpty(rspStr))
//                {
//                    log.error("获取地理位置异常 {}", ip);
//                    return UNKNOWN;
//                }
//                JSONObject obj = JSON.parseObject(rspStr);
//                String region = obj.getString("pro");
//                String city = obj.getString("city");
//                return String.format("%s %s", region, city);
//            }
//            catch (Exception e)
//            {
//                log.error("获取地理位置异常 {}", ip);
//            }
//        }
        return UNKNOWN;
    }
}
