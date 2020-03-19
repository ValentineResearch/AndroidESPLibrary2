package com.esplibrary.client;

import androidx.annotation.Nullable;

import com.esplibrary.constants.DeviceId;
import com.esplibrary.packets.ESPPacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Object used for completing ESP response.
 *
 * @param <T> The type of {@link ESPPacket} the handler will complete for
 */
public class ResponseHandler<T extends ESPPacket> {

    /**
     * List of packet ID's this handler responds to
     */
    private List<Integer> _responseIds = new ArrayList<>();
    /**
     * List of {@link ESPRequest} that correspond to this handler
     */
    List<ESPRequest> _requests = new ArrayList<>();

    /**
     * Callback that will be invoked when a response has been received
     */
    @Nullable
    public ESPCallback<T> successCallback;
    /**
     * Callback that will be invoked when there is an error
     */
    @Nullable
    public FailureCallback failureCallback;

    public ResponseHandler() {
        this(null, null);
    }

    /**
     * Constructs a response handler using the provided callbacks
     * @param responseCallback The {@link ESPCallback callback} that will be invoked when a response is recieved
     * @param failureCallback The {@link FailureCallback callback} that will be invoked when there is an error
     */
    private ResponseHandler(ESPCallback<T> responseCallback, FailureCallback failureCallback) {
        this.successCallback = responseCallback;
        this.failureCallback = failureCallback;
    }

    /**
     * Return's the number of responses (packet IDs) this handler responds to.
     * @return Count of Response Packet ID
     */
    public int responseCount() {
        synchronized (_responseIds) {
            return _responseIds.size();
        }
    }

    /**
     * Indicates if this handler responds to the provided packet ID.
     *
     * @param packetID Packet ID to check for
     *
     * @return True if the handler responds to the packet ID.
     */
    public boolean respondsTo(int packetID) {
        synchronized(_requests) {
            // Manually iterate over the list and call '.intValue()' to prevent auto-boxing
            for(int i = 0, size = _responseIds.size(); i < size; i++) {
                if(_responseIds.get(i).intValue() == packetID){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Indicates if this handler has a packet destined for the provided {{@link DeviceId destinationID}}.
     *
     * @param destinationID The {@link DeviceId destination} of the packet to check for
     *
     * @return True if the handler has a packet destined for destinationID
     */
    public boolean hasRequestFrom(DeviceId destinationID) {
        synchronized(_requests) {
            // Manually iterate over the list and call '.intValue()' to prevent auto-boxing
            for(int i = 0, size = _requests.size(); i < size; i++) {
                ESPPacket packet = _requests.get(i).packet;
                if(packet.getDestination() == destinationID){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Indicates if this handler has a {@link ESPRequest} with packetID that's destined for
     * {@link DeviceId destinationID}.
     *
     * @param packetID      The packet ID of the packet to check for
     * @param destinationID The {@link DeviceId destinationID} of the packet
     *
     * @return True if the handler has a {@link ESPRequest packetID} to {@link DeviceId destinationID}
     */
    public boolean hasRequestTo(int packetID, DeviceId destinationID) {
        synchronized(_requests) {
            // Manually iterate over the list and call '.intValue()' to prevent auto-boxing
            for(int i = 0, size = _requests.size(); i < size; i++) {
                ESPPacket packet = _requests.get(i).packet;
                if(packet.getPacketID() == packetID &&
                        packet.getDestination() == destinationID) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Indicate if this handler has a {@link ESPRequest packet} with packetID.
     *
     * @param packetId The packet ID to check for
     *
     * @return  True if the handler has a packet with packetID.
     */
    public boolean hasRequest(int packetId) {
        synchronized (_requests) {
            for (int i = 0, size = _requests.size(); i < size; i++) {
                ESPRequest request = _requests.get(i);
                if(request.packet.getPacketID() == packetId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds the provided {@link ESPRequest} to this handler and set's it sent time.
     *
     * @param request   The packet to be handled.
     */
    public void addSentRequest(ESPRequest request) {
        if (request == null) {
            return;
        }
        synchronized (_requests) {
            request.sentTime = System.currentTimeMillis();
            _requests.add(request);
        }
    }

    /**
     * Registers a packet ID of the packet that this handler should respond to.
     *
     * @param responseId    Packet ID of the packet to respond to
     *
     * Note: The generic type applied to the {@link #successCallback} must correspond to the
     *                      registered packet ID.
     */
    public void addResponseID(int responseId) {
        synchronized (_responseIds) {
            if (!_responseIds.contains(responseId)) {
                _responseIds.add(responseId);
            }
        }
    }

    /**
     * Indicates if the response handler has expired based on the provided timeout.
     *
     * @param timeout Timeout to check for expiration against
     *
     * @return True if all of the handlers packet have expired.
     */
    public boolean hasExpired(long timeout, long now) {
        synchronized (_requests) {
            // A ResponseHandler isn't considered expired until all of it's packet have timed out.
            // If there is at least one packet not timed out, return false.
            for (int i = 0, size = _requests.size(); i < size; i++) {
                ESPRequest request = _requests.get(i);

                if (request.hasTimeout()) { // If the request has a time out check if we've
                    // exceeded the allowed time period
                    if(request.sentTime + request.mTimeout < now) {
                        return true;
                    }
                    return false;
                }
                // If the request is still below the timeout or the minimum processing time, the
                // response handler hasn't expired
                if((request.sentTime + timeout) > now ||
                        (request.sentTime + request.processingTime) > now) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Indicates if has a request that matches data.
     *
     * @param data byte data
     *
     * @return True if there request matching data
     */
    public boolean hasRequestMatchingData(byte [] data) {
        synchronized (_requests) {
            // A ResponseHandler isn't considered expired until all of it's packet have timed out.
            // If there is at least one packet not timed out, return false.
            for (int i = 0; i < _requests.size(); i++) {
                ESPRequest request = _requests.get(i);
                final byte[] packetData = request.packet.getPacketData();
                return Arrays.equals(packetData, data);
            }
        }
        return false;
    }


    /**
     * Indicates if has a request that matches packet.
     *
     * @param packet ESPPacket
     *
     * @return True if there request matching paket
     */
    public boolean hasRequestMatchingPacket(ESPPacket packet) {
        synchronized (_requests) {
            // A ResponseHandler isn't considered expired until all of it's packet have timed out.
            // If there is at least one packet not timed out, return false.
            for (int i = 0; i < _requests.size(); i++) {
                ESPRequest request = _requests.get(i);
                return packet.equals(request.packet);
            }
        }
        return false;
    }
}
