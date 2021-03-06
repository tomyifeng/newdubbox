package com.alibaba.dubbo.rpc.benchmark;

/**
 * nfs-rpc Apache License http://code.google.com/p/nfs-rpc (c) 2011
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple Processor RPC Benchmark Client Thread
 * 
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public abstract class AbstractClientRunnable implements ClientRunnable {

    private static final Log LOGGER             = LogFactory.getLog(AbstractClientRunnable.class);

    private CyclicBarrier    barrier;

    private CountDownLatch   latch;

    private long             endTime;

    private boolean          running            = true;

    // response time spread
    private long[]           responseSpreads    = new long[9];

    // error request per second
    private long[]           errorTPS           = null;

    // error response times per second
    private long[]           errorResponseTimes = null;

    // tps per second
    private long[]           tps                = null;

    // response times per second
    private long[]           responseTimes      = null;

    // benchmark startTime
    private long             startTime;

    // benchmark maxRange
    private int              maxRange;

    private ServiceFactory   serviceFactory     = new ServiceFactory();

    public AbstractClientRunnable(String protocol, String serialization, String targetIP, int targetPort, int clientNums, int rpcTimeout,
                                  CyclicBarrier barrier, CountDownLatch latch, long startTime, long endTime){

        this.barrier = barrier;
        this.latch = latch;
        this.startTime = startTime;
        this.endTime = endTime;
        serviceFactory.setProtocol(protocol);
        serviceFactory.setTargetIP(targetIP);
        serviceFactory.setClientNums(clientNums);
        serviceFactory.setTargetPort(targetPort);
        serviceFactory.setConnectTimeout(rpcTimeout);
        serviceFactory.setSerialization(serialization);
        maxRange = (Integer.parseInt(String.valueOf((endTime - startTime))) / 1000000) + 1;
        errorTPS = new long[maxRange];
        errorResponseTimes = new long[maxRange];
        tps = new long[maxRange];
        responseTimes = new long[maxRange];
        // init
        for (int i = 0; i < maxRange; i++) {
            errorTPS[i] = 0;
            errorResponseTimes[i] = 0;
            tps[i] = 0;
            responseTimes[i] = 0;
        }
    }

    public void run() {
        try {
            barrier.await();
        } catch (Exception e) {
            // IGNORE
        }
        runJavaAndHessian();
        latch.countDown();
    }

    private void runJavaAndHessian() {
        while (running) {
            long beginTime = System.nanoTime() / 1000L;
            if (beginTime >= endTime) {
                running = false;
                break;
            }
            try {
                Object result = invoke(serviceFactory);
                long currentTime = System.nanoTime() / 1000L;
                if (beginTime <= startTime) {
                    continue;
                }
                long consumeTime = currentTime - beginTime;
                sumResponseTimeSpread(consumeTime);
                int range = Integer.parseInt(String.valueOf(beginTime - startTime)) / 1000000;
                if (range >= maxRange) {
                    System.err.println("benchmark range exceeds maxRange,range is: " + range + ",maxRange is: "
                                       + maxRange);
                    continue;
                }
                if (result != null) {
                    tps[range] = tps[range] + 1;
                    responseTimes[range] = responseTimes[range] + consumeTime;
                } else {
                    LOGGER.error("server return result is null");
     ��� �c�����:skS  2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         