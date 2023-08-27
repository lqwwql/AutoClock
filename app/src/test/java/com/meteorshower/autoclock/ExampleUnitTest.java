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
        object.addProperty("job_type", 1);
        object.addProperty("status", 1);
        System.out.println("object = " + object.toString());
    }

    @Test
    public void testThread() {
        MyThread m1 = new MyThread();

        Thread t1 = new Thread(m1, "窗口1");
        Thread t2 = new Thread(m1, "窗口2");

        t1.start();
        t2.start();
    }

    class MyThread implements Runnable {

        public int ticket = 100;//100票数

        @Override
        public void run() {
            while (ticket > 0) {
                ticket--;
                System.out.println(Thread.currentThread().getName() + "卖出一张票，剩余" + ticket + "张票");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}