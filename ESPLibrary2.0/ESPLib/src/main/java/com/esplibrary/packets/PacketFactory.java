/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.packets;

import com.esplibrary.constants.PacketId;
import com.esplibrary.packets.request.RequestAllSweepDefinitions;
import com.esplibrary.packets.request.RequestBatteryVoltage;
import com.esplibrary.packets.request.RequestChangeMode;
import com.esplibrary.packets.request.RequestCurrentVolume;
import com.esplibrary.packets.request.RequestDefaultSweepDefinitions;
import com.esplibrary.packets.request.RequestDefaultSweeps;
import com.esplibrary.packets.request.RequestFactoryDefault;
import com.esplibrary.packets.request.RequestMaxSweepIndex;
import com.esplibrary.packets.request.RequestMuteOff;
import com.esplibrary.packets.request.RequestMuteOn;
import com.esplibrary.packets.request.RequestOverrideThumbwheel;
import com.esplibrary.packets.request.RequestSAVVYStatus;
import com.esplibrary.packets.request.RequestSavvyUnmuteEnable;
import com.esplibrary.packets.request.RequestSerialNumber;
import com.esplibrary.packets.request.RequestStartAlertData;
import com.esplibrary.packets.request.RequestStopAlertData;
import com.esplibrary.packets.request.RequestSweepSections;
import com.esplibrary.packets.request.RequestTurnOffMainDisplay;
import com.esplibrary.packets.request.RequestTurnOnMainDisplay;
import com.esplibrary.packets.request.RequestUserBytes;
import com.esplibrary.packets.request.RequestVehicleSpeed;
import com.esplibrary.packets.request.RequestVersion;
import com.esplibrary.packets.request.RequestWriteSweepDefinition;
import com.esplibrary.packets.request.RequestWriteUserBytes;
import com.esplibrary.packets.request.RequestWriteVolume;
import com.esplibrary.packets.request.RequestAbortAudioDelay;
import com.esplibrary.packets.request.RequestDisplayCurrentVolume;
import com.esplibrary.packets.response.ResponseAlertData;
import com.esplibrary.packets.response.ResponseBatteryVoltage;
import com.esplibrary.packets.response.ResponseCurrentVolume;
import com.esplibrary.packets.response.ResponseDataError;
import com.esplibrary.packets.response.ResponseDataReceived;
import com.esplibrary.packets.response.ResponseDefaultSweepDefinition;
import com.esplibrary.packets.response.ResponseMaxSweepIndex;
import com.esplibrary.packets.response.ResponseRequestNotProcessed;
import com.esplibrary.packets.response.ResponseSAVVYStatus;
import com.esplibrary.packets.response.ResponseSerialNumber;
import com.esplibrary.packets.response.ResponseSweepDefinition;
import com.esplibrary.packets.response.ResponseSweepSections;
import com.esplibrary.packets.response.ResponseSweepWriteResult;
import com.esplibrary.packets.response.ResponseUnsupported;
import com.esplibrary.packets.response.ResponseUserBytes;
import com.esplibrary.packets.response.ResponseVehicleSpeed;
import com.esplibrary.packets.response.ResponseVersion;

/**
 * Created by JDavis on 3/6/2016.
 */
public class PacketFactory {

    /**
     * Creates a type ESP based on the specified packet ID and packet leghth.
     *
     * @param packetId      A PacketIds that represents a ESPPacket.
     * @return              An ESPPacket that corresponds to the packetId constant.
     */
    public ESPPacket getPacketForId(int packetId, int packetLength) {
        switch (packetId){
            case PacketId.REQVERSION:
                return new RequestVersion(packetLength);
            case PacketId.RESPVERSION:
                return new ResponseVersion(packetLength);
            case PacketId.REQSERIALNUMBER:
                return new RequestSerialNumber(packetLength);
            case PacketId.RESPSERIALNUMBER:
                return new ResponseSerialNumber(packetLength);
            case PacketId.REQUSERBYTES:
                return new RequestUserBytes(packetLength);
            case PacketId.RESPUSERBYTES:
                return new ResponseUserBytes(packetLength);
            case PacketId.REQWRITEUSERBYTES:
                return new RequestWriteUserBytes(packetLength);
            case PacketId.REQFACTORYDEFAULT:
                return new RequestFactoryDefault(packetLength);
            case PacketId.REQWRITESWEEPDEFINITION:
                return new RequestWriteSweepDefinition(packetLength);
            case PacketId.REQALLSWEEPDEFINITIONS:
                return new RequestAllSweepDefinitions(packetLength);
            case PacketId.RESPSWEEPDEFINITION:
                return new ResponseSweepDefinition(packetLength);
            case PacketId.REQDEFAULTSWEEPDEFINITIONS:
                return new RequestDefaultSweepDefinitions(packetLength);
            case PacketId.RESPDEFAULTSWEEPDEFINITIONS:
                return new ResponseDefaultSweepDefinition(packetLength);
            case PacketId.REQDEFAULTSWEEPS:
                return new RequestDefaultSweeps(packetLength);
            case PacketId.REQMAXSWEEPINDEX:
                return new RequestMaxSweepIndex(packetLength);
            case PacketId.RESPMAXSWEEPINDEX:
                return new ResponseMaxSweepIndex(packetLength);
            case PacketId.RESPSWEEPWRITERESULT:
                return new ResponseSweepWriteResult(packetLength);
            case PacketId.REQSWEEPSECTIONS:
                return new RequestSweepSections(packetLength);
            case PacketId.RESPSWEEPSECTIONS:
                return new ResponseSweepSections(packetLength);
            case PacketId.INFDISPLAYDATA:
                return new InfDisplayData(packetLength);
            case PacketId.REQTURNOFFMAINDISPLAY:
                return new RequestTurnOffMainDisplay(packetLength);
            case PacketId.REQTURNONMAINDISPLAY:
                return new RequestTurnOnMainDisplay(packetLength);
            case PacketId.REQMUTEON:
                return new RequestMuteOn(packetLength);
            case PacketId.REQMUTEOFF:
                return new RequestMuteOff(packetLength);
            case PacketId.REQCHANGEMODE:
                return new RequestChangeMode(packetLength);
            case PacketId.REQCURRENTVOLUME:
                return new RequestCurrentVolume(packetLength);
            case PacketId.RESPCURRENTVOLUME:
                return new ResponseCurrentVolume(packetLength);
            case PacketId.REQWRITEVOLUME:
                return new RequestWriteVolume(packetLength);
            case PacketId.REQABORTAUDIODELAY:
                return new RequestAbortAudioDelay(packetLength);
            case PacketId.REQDISPLAYCURRENTVOLUME:
                return new RequestDisplayCurrentVolume(packetLength);
            case PacketId.REQSTARTALERTDATA:
                return new RequestStartAlertData(packetLength);
            case PacketId.REQSTOPALERTDATA:
                return new RequestStopAlertData(packetLength);
            case PacketId.RESPALERTDATA:
                return new ResponseAlertData(packetLength);
            case PacketId.RESPDATARECEIVED:
                return new ResponseDataReceived(packetLength);
            case PacketId.REQBATTERYVOLTAGE:
                return new RequestBatteryVoltage(packetLength);
            case PacketId.RESPBATTERYVOLTAGE:
                return new ResponseBatteryVoltage(packetLength);
            case PacketId.RESPUNSUPPORTEDPACKET:
                return new ResponseUnsupported(packetLength);
            case PacketId.RESPREQUESTNOTPROCESSED:
                return new ResponseRequestNotProcessed(packetLength);
            case PacketId.INFV1BUSY:
                return new InfV1Busy(packetLength);
            case PacketId.RESPDATAERROR:
                return new ResponseDataError(packetLength);
            case PacketId.REQSAVVYSTATUS:
                return new RequestSAVVYStatus(packetLength);
            case PacketId.RESPSAVVYSTATUS:
                return new ResponseSAVVYStatus(packetLength);
            case PacketId.REQVEHICLESPEED:
                return new RequestVehicleSpeed(packetLength);
            case PacketId.RESPVEHICLESPEED:
                return new ResponseVehicleSpeed(packetLength);
            case PacketId.REQOVERRIDETHUMBWHEEL:
                return new RequestOverrideThumbwheel(packetLength);
            case PacketId.REQSETSAVVYUNMUTEENABLE:
                return new RequestSavvyUnmuteEnable(packetLength);
            default:
                return new UnknownPacket(packetLength);
        }
    }
}
