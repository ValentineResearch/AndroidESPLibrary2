package com.esplibrary.client;

import com.esplibrary.bluetooth.BTUtil;
import com.esplibrary.packets.ESPPacket;

public class ESPRequest {

    public final ESPPacket packet;
    public ResponseHandler respHandler;
    public long processingTime;
    long sentTime = -1;
    final long requestTime;
    public long mTimeout = -1;

    public ESPRequest(ESPPacket req, ResponseHandler handler) {
        this(req, handler, 0);
    }

    public ESPRequest(ESPPacket req, ResponseHandler handler, long processingTime) {
        BTUtil.nullCheck(req, "ESPPackets cannot be null!!!!!!!");
        this.packet = req;
        this.respHandler = handler;
        this.processingTime = processingTime;
        this.requestTime = System.currentTimeMillis();
    }

    /**
     * Set's the timeout in milliseconds for the ESP request
     *
     * @param timeout Number of milliseconds before the request times out.
     */
    public void setTimeout(long timeout) {
        if(timeout < 0) {
            mTimeout = -1;
        }
        mTimeout = timeout;
    }

    /**
     * Indicates if the request has a timeout.
     *
     * @return True if has timeout
     */
    public boolean hasTimeout() {
        return 0 < mTimeout;
    }
}
