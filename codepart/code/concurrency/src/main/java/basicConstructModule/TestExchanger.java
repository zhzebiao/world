package basicConstructModule;

import java.util.concurrent.Exchanger;

/**
 * @author zhzeb
 * @date 2020/5/26 6:18
 */
public class TestExchanger {

    public static void main(String[] args) {
        final Exchanger<String> e=new Exchanger<String>();
        final Exchanger<String> e1=new Exchanger<String>();

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:到达位置，提交one");
                    Object exchange = e.exchange("one");
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:调用exchange后返回："+exchange);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:到达位置，提交two");
                    Object exchange = e.exchange("two");
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:调用exchange后返回："+exchange);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:到达位置，提交three");
                    Object exchange = e1.exchange("three");
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:调用exchange后返回："+exchange);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:到达位置，提交four");
                    Object exchange = e1.exchange("four");
                    System.out.println(Thread.currentThread().getName()+">>>>>>>>:调用exchange后返回："+exchange);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }
}