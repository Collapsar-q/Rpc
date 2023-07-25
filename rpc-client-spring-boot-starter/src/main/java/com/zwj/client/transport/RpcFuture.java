package com.zwj.client.transport;

import java.util.concurrent.*;

/**
 * @Author: zwj
 * @Description: TODO
 * @DateTime: 2023/5/10 21:40
 **/
public class RpcFuture<T> implements Future<T> {
    /**
     *  响应结果
     */
    private T response;
    //定义了一个CountDownLatch对象countDownLatch，并初始化为1。这个对象用于在请求和响应之间进行一对一的关联，并用于线程等待。
    private CountDownLatch countDownLatch=new CountDownLatch(1);
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
    /**
     *  响应数据不为空 表示完成
     * @return
     */
    @Override
    public boolean isDone() {
        return this.response!=null;
    }
    /**
     *  等待获取数据，直到有结果 也就是 countDownLatch 的值减到 0
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public T get() throws InterruptedException, ExecutionException {
        // 进入阻塞等待 countDownLatch减少值为0 返回下面结果
        countDownLatch.await();
        return response;
    }
    /**
     *  超时等待 获取数据 实现了带有超时参数的get方法，它会等待一段指定的时间，如果在指定时间内countDownLatch的值减到0，就返回response，否则返回null。
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)){
            return response;
        }
        return null;
    }
    //定义了一个setResponse方法，用于设置响应结果，并通过countDownLatch.countDown()来减少countDownLatch的值。
    public void setResponse(T response) {
        this.response=response;
        countDownLatch.countDown();
    }
}
