package mfh;

/**
 * @Author: mfh
 * @Date: 2019-04-17 13:31
 **/
public enum Key {
    ENDPOINT("testcore.oss.foticit.com.cn"),
    ACCESSKEY("dGVzdGNvcmU="),
    SECRETKEY("dddeabc0f5437949c0c46f694d3a7970"),
    BUCKETNAME("testcore");
    String val;

    Key(String val) {
        this.val = val;
    }
}
