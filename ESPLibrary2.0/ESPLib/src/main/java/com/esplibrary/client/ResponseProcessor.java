package com.esplibrary.client;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.ESPPacket;
import com.esplibrary.utilities.ESPLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor for satisfying ESP requests.
 */
public class ResponseProcessor {

    private final static String LOG_TAG = "ResponseProcessor";
    public static final String REQUEST_TIMED_OUT = "Request timed out";
    public static final String REQUEST_TIMED_NOT_SENT = "Request timed out before it could be sent";
    public static final String REQUEST_NOT_PROCESSED_DISCONNECTED = "Request not processed, device disconnected";

    List<ResponseHandler> _responseHandlers;
    /**
     * Holds timed out response handlers for ESP requests in flight
     */
    List<ResponseHandler> _timedOutResponseHandlers;
    /**
     * Holds timed out response handlers for ESP packet yet to be sent.
     */
    List<ResponseHandler> _timedOutQueueRequest;
    /**
     * Timeout used to expire ResponseHandlers
     */
    long _timeout;

    /**
     * Constructs a resp. processor using the provided timeout in milliseconds
     *
     * @param timeout Max age of a packet
     */
    public ResponseProcessor(long timeout) {
        _timeout = timeout;
        _responseHandlers = new ArrayList<>(8);
        _timedOutResponseHandlers = new ArrayList<>(8);
        _timedOutQueueRequest = new ArrayList<>();
    }

    /**
     * Adds a {@link ResponseHandler} to be processed by this processor.
     * @param respHandler Handler to process
     */
    public void addResponse(ResponseHandler respHandler) {
        // Add an null response respHandler is incorrect and should be ignored
        if (respHandler == null) {
            return;
        }
        synchronized (_responseHandlers) {
            // Prevent duplicates
            if (!_responseHandlers.contains(respHandler)) {
                _responseHandlers.add(respHandler);
            }
        }
    }

    /**
     * Removes the {@link ResponseHandler} that is related to data.
     *
     * @return Removed ResponseHandler; null if no handler was found
     */
    public ResponseHandler removeResponseHandlerForData(byte [] data) {
        if (data != null) {
            synchronized (_responseHandlers) {
                for (int i = _responseHandlers.size() - 1; i >= 0; i--) {
                    final ResponseHandler resphndlr = _responseHandlers.get(i);
                    if(resphndlr.hasRequestMatchingData(data)) {
                        _responseHandlers.remove(i);
                        return resphndlr;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes the {@link ResponseHandler} that is related to packet.
     *
     * @return Removed ResponseHandler; null if no handler was found
     */
    public ResponseHandler removeResponseHandlerForPacket(ESPPacket packet) {
        if (packet != null) {
            synchronized (_responseHandlers) {
                for (int i = _responseHandlers.size() - 1; i >= 0; i--) {
                    final ResponseHandler resphndlr = _responseHandlers.get(i);
                    if(resphndlr.hasRequestMatchingPacket(packet)) {
                        _responseHandlers.remove(i);
                        return resphndlr;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Checks for ESP packet that have expired.
     * @param pendingRequests Queued ESP packet that have yet to be sent.
     */
    public void checkForExpiry(List<ESPRequest> pendingRequests) {
        synchronized (_responseHandlers) {
            // If we have no response handlers and no pending requests, do nothing
            if (_responseHandlers.isEmpty()) {
                // It's fine that we don't synchronize on the pending request because checkForExpiry
                // should get called very frequently so if a request gets adding the the list, we
                // will delay execution by whatever interval (as if now 80 milliseconds) this
                // function is called at
                if(pendingRequests.isEmpty()) {
                    return;
                }
            }

            final long now = System.currentTimeMillis();
            // Go through all response handlers and check to see if they are expired.
            for (int i = _responseHandlers.size() - 1; i >= 0; i--) {
                ResponseHandler responseHandler = _responseHandlers.get(i);
                if(responseHandler.hasExpired(_timeout, now)){
                    _timedOutResponseHandlers.add(responseHandler);
                    _responseHandlers.remove(i);
                }
            }
            // Go through the queue packet(packets not yet sent) and determine if it's been enough
            // time to reasonably try to send a packet.
            synchronized (pendingRequests) {
                for (int i = pendingRequests.size() - 1; i >= 0; i--) {
                    ESPRequest request = pendingRequests.get(i);
                    // Check if the request has timed out
                    if(request.requestTime + request.processingTime + _timeout <= now) {
                        final ResponseHandler rspHandler = request.respHandler;
                        if (rspHandler != null) {
                            // Check to see if the packet's response respHandler is currently
                            // awaiting a response. In this situation we want the ResponseHandler to
                            // time out instead. This happens when the same response respHandler is
                            // used for multiple packet. Such as writing sweep definitions.
                            if (_responseHandlers.indexOf(rspHandler) == -1) {
                                // The packet doesn't have any waiting response respHandler so it's
                                // safe to cancel the packet... Make sure we haven't already queue
                                // the packet to be cancelled
                                if(!_timedOutResponseHandlers.contains(rspHandler) && !_timedOutQueueRequest.contains(rspHandler)) {
                                    _timedOutQueueRequest.add(rspHandler);
                                }
                                // Remove the packet... it'll get properly cancelled below.
                                pendingRequests.remove(i);
                            }
                        }
                        else {
                            // No ResponseHandler so simply remove it from the queue.
                            pendingRequests.remove(i);
                        }
                    }
                }
                // If a pending request has a resp. handler in either the timed out resp. queue or
                // the the timed out req. queue remove the request
                for (int i = pendingRequests.size() - 1; i >= 0; i--) {
                    ESPRequest request = pendingRequests.get(i);
                    ResponseHandler responseHandler = request.respHandler;
                    if(_timedOutResponseHandlers.contains(responseHandler)
                            || _timedOutQueueRequest.contains(responseHandler)) {
                        pendingRequests.remove(i);
                    }
                }
            }
            // Indicate the response handlers that they've timed out.
            for (int i = 0; i < _timedOutResponseHandlers.size(); i++) {
                _timedOutResponseHandlers.get(i).failureCallback.onFailure(REQUEST_TIMED_OUT);
            }
            for (int i = 0; i < _timedOutQueueRequest.size(); i++) {
                _timedOutQueueRequest.get(i).failureCallback.onFailure(REQUEST_TIMED_NOT_SENT);
            }

            _timedOutResponseHandlers.clear();
            _timedOutQueueRequest.clear();
        }
    }

    /**
     * Expires all packet awaiting a response because we've disconnected
     */
    public void expireRequestsForDisconnection() {
        synchronized(_responseHandlers) {
            for(int i = 0, size = _responseHandlers.size(); i < size; i++) {
                ResponseHandler responseHandler = _responseHandlers.get(i);
                if (responseHandler.failureCallback != null) {
                    responseHandler.failureCallback.onFailure(REQUEST_NOT_PROCESSED_DISCONNECTED);
                }
            }
            _responseHandlers.clear();
        }
    }

    /**
     * Perform a failure callback for the 'failure' packet.
     * @param packet    Failure {@link ESPPacket}
     */
    public void onFailurePacket(ESPPacket packet) {
        ESPLogger.i(LOG_TAG, String.format("Received packet not processed from %s", packet.getOrigin().toString()));
        int badPacketID = packet.getPayloadData()[0] & 0xFF;
        DeviceId originID = packet.getOrigin();
        // Find the last response responseHandler this packet ID belongs too and remove it.
        ResponseHandler foundRespHandler = null;
        synchronized (_responseHandlers) {
            for (int i = _responseHandlers.size() - 1; i >= 0; i--) {
                if(_responseHandlers.get(i).hasRequestTo(badPacketID, originID)) {
                    foundRespHandler = _responseHandlers.remove(i);
                    break;
                }
            }
        }
        // Perform the failure callback on the responseHandler.
        if (foundRespHandler != null) {
            String error;

            int packetID = packet.getPacketID();
            switch (packetID) {
                case PacketId.RESPDATAERROR:
                    error = String.format("Data error for packet Id = %02X", badPacketID);
                    break;
                case PacketId.RESPUNSUPPORTEDPACKET:
                    error = String.format("Request ID = %02X is not supported by destination = %02X", badPacketID, originID.toByte());
                    break;
                default:
                    error = String.format("Request ID = %d could not be processed by destination = %02X", badPacketID, originID.toByte());
            }
            foundRespHandler.failureCallback.onFailure(error);
        }
    }

    /**
     * Perform the success callback on any {@link ResponseHandler handler} that responds to the packet
     * @param packet            Received {@link ESPPacket} to perform success callback
     * @param busyPacketIDs     Current list of busy packet IDs
     */
    public void onPacketReceivedBlocking(ESPPacket packet, List<Integer> busyPacketIDs) {
        synchronized(_responseHandlers) {
            for (int i = 0; i < _responseHandlers.size(); i++) {
                ResponseHandler resp = _responseHandlers.get(i);
                // If the response respHandler doesn't have any response it's waiting on, we should directly as the respHandler if it is done
                // and its safe to remove.
                if(resp.responseCount() == 0) {
                    // Since this response respHandler doesn't respond to a particular response ID, we
                    // want to ask it if it's ready to be fulfilled(removed) but we need to make sure
                    // that it's packet has been sent and not waiting in the busy queue.
                    if(!checkBusyPackets(busyPacketIDs, resp)) {
                        // If the packet callback is null or packetCallback returns true, we want to
                        // remove the response respHandler
                        if(resp.successCallback == null || resp.successCallback.onPacketReceived(null)) {
                            synchronized(_responseHandlers) {
                                _responseHandlers.remove(i);
                            }
                            // update i so we skip resp. handler after removing the current index.
                            i =- 1;
                        }
                    }
                }
                else {
                    // Check to see if the response respHandler responds to the packet.
                    if(resp.respondsTo(packet.getPacketID()) && resp.hasRequestFrom(packet.getOrigin())) {
                        // If the packet callback is null or packetCallback returns true, we want to
                        // remove the response respHandler
                        if(resp.successCallback == null || resp.successCallback.onPacketReceived(packet)) {
                            synchronized(_responseHandlers) {
                                _responseHandlers.remove(i);
                            }
                            // update i so we skip resp. handler after removing the current index.
                            i =- 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * Check to see if any {@link ResponseHandler ResponseHandlers} have packet that are currently
     * busy.
     * @param busyPacketIDs Current list of busy packet ID's
     * @param handler       {@link ResponseHandler} to check if any packet are in the busy queue
     * @return  True if the handler has packet in the list of busy packet IDs
     */
    private boolean checkBusyPackets(List<Integer> busyPacketIDs, ResponseHandler handler) {
        for (int i = 0; i < busyPacketIDs.size(); i++) {
            Integer packetID = busyPacketIDs.get(i);
            if(handler.hasRequest(packetID.intValue())) {
                return true;
            }
        }
        return false;
    }
}
