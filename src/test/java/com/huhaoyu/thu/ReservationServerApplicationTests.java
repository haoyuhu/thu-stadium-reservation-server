package com.huhaoyu.thu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huhaoyu.thu.common.HttpUtil;
import com.huhaoyu.thu.common.RedisUtil;
import com.huhaoyu.thu.common.SecurityUtil;
import com.huhaoyu.thu.config.SecurityConfiguration;
import com.huhaoyu.thu.service.AccountService;
import com.huhaoyu.thu.service.MailService;
import com.huhaoyu.thu.widget.VisibleEntityWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

import static com.huhaoyu.thu.common.Constants.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationServerApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    MailService mailService;

    @Test
    public void testMail() throws MessagingException {
        SimpleMailTemplate template = SimpleMailTemplate.Test;
        String[] receivers = {"test@huhaoyu.com", "im@huhaoyu.com"};
        MimeMessage message =
                mailService.createSimpleTextMailMessage(template.getSubject(), template.getBody(), receivers);
        try {
            mailService.sendMail(message);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    @Autowired
    SecurityConfiguration securityConfig;

    @Test
    public void testUtils() throws JsonProcessingException {
        String testString = "hello world!";
        String testPassword = "4d87635d88f21069";
        String encrypted = SecurityUtil.encrypt(
                testString,
                testPassword,
                securityConfig.getSymmetricEncryptionAlgorithm(),
                securityConfig.getSymmetricEncryptionAlgorithmMode(),
                securityConfig.getSymmetricEncryptionAlgorithmIV());
        String decrypted = SecurityUtil.decrypt(
                encrypted, testPassword,
                securityConfig.getSymmetricEncryptionAlgorithm(),
                securityConfig.getSymmetricEncryptionAlgorithmMode(),
                securityConfig.getSymmetricEncryptionAlgorithmIV());
        Assert.assertEquals("assert origin and string after encrypted and decrypted", testString, decrypted);

        Map<String, Object> data = new TreeMap<>();
        data.put("secret_id", testPassword);
        data.put("open_id", testString);
        data.put("group_id", testString);
        data.put("encrypted", testString);
        String queryStrings = HttpUtil.convertMapToQueryStrings(data);
        Assert.assertEquals("assert query string converter", queryStrings,
                "encrypted=hello%20world!&group_id=hello%20world!&open_id=hello%20world!&secret_id=4d87635d88f21069");

        // stadium_name, site_name, start_time, end_time, thu_account(username), cost, description
        Map<String, Object> r1 = new HashMap<>();
        Map<String, Object> r2 = new HashMap<>();

        r1.put("stadium_name", "气膜馆");
        r1.put("site_name", "羽2");
        Calendar start = Calendar.getInstance();
        start.set(2017, Calendar.JANUARY, 23, 18, 0);
        r1.put("start_time", start.getTimeInMillis());
        Calendar end = Calendar.getInstance();
        end.set(2017, Calendar.JANUARY, 23, 19, 0);
        r1.put("end_time", end.getTimeInMillis());
        r1.put("thu_account", "hhy14");
        r1.put("cost", 20.0);
        r1.put("description", "test");

        r2.put("stadium_name", "气膜馆");
        r2.put("site_name", "羽3");
        Calendar anotherStart = Calendar.getInstance();
        anotherStart.set(2017, Calendar.JANUARY, 23, 19, 0);
        r2.put("start_time", anotherStart.getTimeInMillis());
        Calendar anotherEnd = Calendar.getInstance();
        anotherEnd.set(2017, Calendar.JANUARY, 23, 20, 0);
        r2.put("end_time", anotherEnd.getTimeInMillis());
        r2.put("thu_account", "hhy14");
        r2.put("cost", 20.0);

        List<Map> list = Arrays.asList(r1, r2);
        ObjectMapper mapper = new ObjectMapper();
        String raw = mapper.writeValueAsString(list);
        String after = SecurityUtil.encrypt(
                raw,
                securityConfig.getScheduledTaskSecretKey(),
                securityConfig.getSymmetricEncryptionAlgorithm(),
                securityConfig.getSymmetricEncryptionAlgorithmMode(),
                securityConfig.getSymmetricEncryptionAlgorithmIV());
        Assert.assertEquals("assert encrypted list of map", raw, SecurityUtil.decrypt(
                after,
                securityConfig.getScheduledTaskSecretKey(),
                securityConfig.getSymmetricEncryptionAlgorithm(),
                securityConfig.getSymmetricEncryptionAlgorithmMode(),
                securityConfig.getSymmetricEncryptionAlgorithmIV()));

        Map<String, Object> test = new TreeMap<>();
        test.put("secret_id", securityConfig.getScheduledTaskSecretId());
        test.put("open_id", "d9db997f4ac716e05aeff9bc8cfcb4d7");
        test.put("group_id", 3);
        test.put("encrypted", after);
        String queries = HttpUtil.convertMapToQueryStrings(test);
        if (queries != null) {
            String md5 = DigestUtils.md5DigestAsHex(queries.getBytes());
        }
    }

    @Autowired
    RedisUtil redis;

    @Test
    public void testRedis() throws InterruptedException {
        final String testKey = "test-key";
        final String testSecondaryKey = "test-secondary-key";
        final String testStringValue = "string-value";
        final long testLongValue = 60L;
        final long expiration = 5L;
        final long MILLIS = 1000L;

        final TestObject testValue = new TestObject();
        testValue.stringValue = testStringValue;
        testValue.longValue = testLongValue;

        redis.remove(testKey);
        Assert.assertEquals("no key named test-key", false, redis.exists(testKey));
        redis.set(testKey, testValue);
        TestObject cache = (TestObject) redis.get(testKey);
        Assert.assertEquals("test string value", cache.stringValue, testStringValue);
        Assert.assertEquals("test long value", cache.longValue, testLongValue);
        redis.remove("test-key");
        Assert.assertEquals("no key named test-key after deleting", false, redis.exists(testKey));

        redis.set(testKey, testValue, expiration);
        Assert.assertEquals("has key named test-key after setting", true, redis.exists(testKey));
        Thread.sleep(expiration * 2 * MILLIS);
        Assert.assertEquals("no key named test-key after expiring", false, redis.exists(testKey));

        redis.setHash(testKey, testSecondaryKey, cache);
        redis.expire(testKey, expiration);
        cache = (TestObject) redis.getHash(testKey, testSecondaryKey);
        Assert.assertEquals("has key named test-key after setting hash", true, redis.exists(testKey));
        Assert.assertEquals("test string value", cache.stringValue, testStringValue);
        Assert.assertEquals("test long value", cache.longValue, testLongValue);
        Thread.sleep(expiration * 2 * MILLIS);
        Assert.assertEquals("no key named test-key after expiring", false, redis.exists(testKey));

        Map<String, Object> map = new HashMap<>();
        TestObject otherOne = new TestObject();
        otherOne.stringValue = testStringValue;
        otherOne.longValue = 80L;
        map.put(testSecondaryKey + ":first", cache);
        map.put(testSecondaryKey + ":second", otherOne);

        redis.setHashes(testKey, map);
        Assert.assertEquals("has key named test-key after setting hash", true, redis.exists(testKey));
        List<Object> results = redis.getHashes(testKey,
                Arrays.asList(testSecondaryKey + ":first", testSecondaryKey + ":second"));
        Assert.assertEquals("check hash item count", 2, results.size());
        redis.removePattern("*");
        Assert.assertEquals("check item count after clearing", false, redis.exists(testKey));
    }

    @Test
    public void testAnnotation() throws JsonProcessingException {
        AnnotationObject object = new AnnotationObject();
        long testLong = 1L;
        String testString = "test string";
        int testInt = 1;
        double testDouble = 1.2D;
        float testFloat = 1.1F;
        Date testDate = new Date();
        TestObject o1 = new TestObject();
        o1.longValue = testLong;
        o1.stringValue = null;
        TestObject o2 = new TestObject();
        o2.stringValue = testString;
        o2.longValue = testLong;

        List<TestObject> testList = Arrays.asList(o1, o2);
        Set<String> testSet = new HashSet<>();
        testSet.add(testString);
        Map<String, TestObject> testMap = new HashMap<>();
        testMap.put("key1", o1);
        testMap.put("key2", o2);

        object.setLongValue(testLong);
        object.setStringValue(testString);
        object.setTestDateWithAnnotation(testDate);
        object.setTestFieldNameWithNumber2AtEnd(o2);
        object.setTestList(testList);
        object.setTestMap(testMap);
        object.setTestSet(testSet);
        object.setTestNull(null);
        object.setUseless(testDouble);
        object.setTestNoAnnotation(testFloat);

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(VisibleEntityWrapper.createVisibleFieldsMap(object));
        String answer = "{\"test_null\":null,\"string_value\":\"test string\",\"test_map\":{\"key1\":{\"string_value\":null," +
                "\"long_value\":1},\"key2\":{\"string_value\":\"test string\",\"long_value\":1}}," +
                "\"test_field_name_with_number2_at_end\":{\"string_value\":\"test string\",\"long_value\":1}," +
                "\"test_date\":" + String.valueOf(testDate.getTime()) + ",\"long_value\":1,\"test_list\":" +
                "[{\"string_value\":null,\"long_value\":1},{\"string_value\":\"test string\",\"long_value\":1}]," +
                "\"test_set\":[\"test string\"]}";

        Assert.assertEquals("assert answer", answer, str);
    }

    @Autowired
    AccountService accountService;
    @Autowired
    TestConfiguration testConfig;

    @Test
    public void testVerifyAccount() {
        final String wrongUsername = "wrong-username";
        final String wrongPassword = "wrong-password";
        boolean success = accountService.verifyAccount(testConfig.getStudentId(), testConfig.getPassword());
        boolean failure = accountService.verifyAccount(wrongUsername, wrongPassword);

        Assert.assertTrue("verify account with correct account", success);
        Assert.assertFalse("verify account with wrong account", failure);
    }

}
