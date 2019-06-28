package com.meteorshower.autoclock;

import com.google.gson.JsonObject;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.util.Util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void testmyown() {
//        String value = "30";
//        float result = Float.parseFloat(value);
//        result = (-result);
//        System.out.println("result = " + result);
        JsonObject object = new JsonObject();
        object.addProperty("job_type",1);
        object.addProperty("status",1);
        System.out.println("object = " + object.toString());
    }
}