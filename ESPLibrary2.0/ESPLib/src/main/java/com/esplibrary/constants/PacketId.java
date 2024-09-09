/*
 * Copyright(c) 2016 Valentine Research, Inc
 * This file is part of the ESP Library, which is licensed under the MIT license.
 * You should have received a copy of the MIT license along with this file. If not, see http://opensource.org/licenses/MIT
 */
package com.esplibrary.constants;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Packet ID constants class that contains all of the Packet ID's declared in the ESP specification Rev. 3.
 */
public class PacketId {
    // Tells the compiler to treat these ints as logical types. Essentially make them typedefs.
    @IntDef({
            REQVERSION,
            RESPVERSION,
            REQSERIALNUMBER,
            RESPSERIALNUMBER,
            REQUSERBYTES,
            RESPUSERBYTES,
            REQWRITEUSERBYTES,
            REQFACTORYDEFAULT,
            REQWRITESWEEPDEFINITION,
            REQALLSWEEPDEFINITIONS,
            RESPSWEEPDEFINITION,
            REQDEFAULTSWEEPS,
            REQMAXSWEEPINDEX,
            RESPMAXSWEEPINDEX,
            RESPSWEEPWRITERESULT,
            REQSWEEPSECTIONS,
            RESPSWEEPSECTIONS,
            REQDEFAULTSWEEPDEFINITIONS,
            RESPDEFAULTSWEEPDEFINITIONS,
            INFDISPLAYDATA,
            REQTURNOFFMAINDISPLAY,
            REQTURNONMAINDISPLAY,
            REQMUTEON,
            REQMUTEOFF,
            REQCHANGEMODE,
            REQCURRENTVOLUME,
            RESPCURRENTVOLUME,
            REQWRITEVOLUME,
            REQABORTAUDIODELAY,
            REQSTARTALERTDATA,
            REQSTOPALERTDATA,
            RESPALERTDATA,
            RESPDATARECEIVED,
            REQBATTERYVOLTAGE,
            RESPBATTERYVOLTAGE,
            RESPUNSUPPORTEDPACKET,
            RESPREQUESTNOTPROCESSED,
            INFV1BUSY,
            RESPDATAERROR,
            REQSAVVYSTATUS,
            RESPSAVVYSTATUS,
            REQVEHICLESPEED,
            RESPVEHICLESPEED,
            REQOVERRIDETHUMBWHEEL,
            REQSETSAVVYUNMUTEENABLE,
            UNKNOWNPACKETTYPE
    })

    // Tells the compiler not to include typedefs in the .class file.
    @Retention(RetentionPolicy.SOURCE)

    /**
     * Valid Packet IDs for receiving ESP data.
     */
    public @interface PacketID {}
    /**Packet Identifier for Version Request**/
    public static final int REQVERSION 					= 0x01;
    /**Packet Identifier for Version Response**/
    public static final int RESPVERSION 				= 0x02;
    /**Packet Identifier for Serial Number Request**/
    public static final int REQSERIALNUMBER 			= 0x03;
    /**Packet Identifier for Serial Number Response**/
    public static final int RESPSERIALNUMBER			= 0x04;
    /**Packet Identifier for UserBytes Request**/
    public static final int REQUSERBYTES 				= 0x11;
    /**Packet Identifier for UserBytes Response**/
    public static final int RESPUSERBYTES 				= 0x12;
    /**Packet Identifier for Write UserBytes Request**/
    public static final int REQWRITEUSERBYTES 			= 0x13;
    /**Packet Identifier for Factory Default Request**/
    public static final int REQFACTORYDEFAULT 			= 0x14;
    /**Packet Identifier for writing a Sweep Definition Request**/
    public static final int REQWRITESWEEPDEFINITION 	= 0x15;
    /**Packet Identifier for all Sweep Definiton Request**/
    public static final int REQALLSWEEPDEFINITIONS 		= 0x16;
    /**Packet Identifier for Sweep Definition Response**/
    public static final int RESPSWEEPDEFINITION 		= 0x17;
    /**Packet Identifier for Default Sweep Request**/
    public static final int REQDEFAULTSWEEPS            = 0x18;
    /**Packet Identifier for Max Sweep Index Request**/
    public static final int REQMAXSWEEPINDEX 			= 0x19;
    /**Packet Identifier for Max Sweep Index Response**/
    public static final int RESPMAXSWEEPINDEX 			= 0x20;
    /**Packet Identifier for Sweep Write Result Response**/
    public static final int RESPSWEEPWRITERESULT		= 0x21;
    /**Packet Identifier for Sweep Sections Request**/
    public static final int REQSWEEPSECTIONS			= 0x22;
    /**Packet Identifier for Sweep Sections Response**/
    public static final int RESPSWEEPSECTIONS 			= 0x23;
    /**Packet Identifier for Default Sweep Definitions Request**/
    public static final int REQDEFAULTSWEEPDEFINITIONS	= 0x24;
    /**Packet Identifier for Default Sweep Definitions  Response**/
    public static final int RESPDEFAULTSWEEPDEFINITIONS = 0x25;
    /**Packet Identifier for InfDisplayData**/
    public static final int INFDISPLAYDATA 				= 0x31;
    /**Packet Identifier for Turn Off Main Display Request**/
    public static final int REQTURNOFFMAINDISPLAY 		= 0x32;
    /**Packet Identifier for Turn On Main Display Request**/
    public static final int REQTURNONMAINDISPLAY 		= 0x33;
    /**Packet Identifier for Mute On Request**/
    public static final int REQMUTEON 					= 0x34;
    /**Packet Identifier for Mute Off Request**/
    public static final int REQMUTEOFF 					= 0x35;
    /**Packet Identifier for Change Mode Request**/
    public static final int REQCHANGEMODE 				= 0x36;
    /**Packet Identifier for the current volume settings request**/
    public static final int REQCURRENTVOLUME            = 0x37;
    /**Packet Identifier for the current volume setting response**/
    public static final int RESPCURRENTVOLUME           = 0x38;
    /**Packet Identifier for updating the current volume settings request**/
    public static final int REQWRITEVOLUME              = 0x39;
    /**Packet Identifier for updating the current volume settings request**/
    public static final int REQABORTAUDIODELAY          = 0x3A;
    /**Packet Identifier for Start Alert Data Request**/
    public static final int REQSTARTALERTDATA 			= 0x41;
    /**Packet Identifier for Stop Alert Data Request*/
    public static final int REQSTOPALERTDATA 			= 0x42;
    /**Packet Identifier for Alert Data Response**/
    public static final int RESPALERTDATA 				= 0x43;
    /**Packet Identifier for Response Data**/
    public static final int RESPDATARECEIVED 			= 0x61;
    /**Packet Identifier for Battery Voltage Request**/
    public static final int REQBATTERYVOLTAGE 			= 0x62;
    /**Packet Identifier for Battery Voltage Response**/
    public static final int RESPBATTERYVOLTAGE 			= 0x63;
    /**Packet Identifier for Unsupported Packet Response**/
    public static final int RESPUNSUPPORTEDPACKET 		= 0x64;
    /**Packet Identifier for Request Not Processed Response**/
    public static final int RESPREQUESTNOTPROCESSED 	= 0x65;
    /**Packet Identifier for InfV1Busy**/
    public static final int INFV1BUSY 					= 0x66;
    /**Packet Identifier for Data Error Response**/
    public static final int RESPDATAERROR 				= 0x67;
    /**Packet Identifier for SAVVYStatus Request**/
    public static final int REQSAVVYSTATUS 				= 0x71;
    /**Packet Identifier for Savvy Status Response**/
    public static final int RESPSAVVYSTATUS 			= 0x72;
    /**Packet Identifier for Vehicle Speed Request**/
    public static final int REQVEHICLESPEED 			= 0x73;
    /**Packet Identifier for Vehicle Speed Response**/
    public static final int RESPVEHICLESPEED 		    = 0x74;
    /**Packet Identifier for Override Thumbwheel Request**/
    public static final int REQOVERRIDETHUMBWHEEL 		= 0x75;
    /**Packet Identifier for Savvy Unmute Enable Request**/
    public static final int REQSETSAVVYUNMUTEENABLE 	= 0x76;
    /**Packet Identifier for Unknown Packet**/
    public static final int UNKNOWNPACKETTYPE 			= 0x100;

    /**
     * Retrieves the String representation of the packetId value.
     *
     * @param packetId  The packetId of the desired packet type String you want returned.
     *                  Possible Values: {
     *                  {@link PacketId#REQVERSION}, {@link PacketId#RESPVERSION}, {@link PacketId#REQSERIALNUMBER},
     *                  {@link PacketId#RESPSERIALNUMBER}, {@link PacketId#REQUSERBYTES}, {@link PacketId#RESPUSERBYTES},
     *                  {@link PacketId#REQWRITEUSERBYTES}, {@link PacketId#REQFACTORYDEFAULT}, {@link PacketId#REQWRITESWEEPDEFINITION},
     *                  {@link PacketId#REQALLSWEEPDEFINITIONS}, {@link PacketId#RESPSWEEPDEFINITION}, {@link PacketId#REQDEFAULTSWEEPS},
     *                  {@link PacketId#REQMAXSWEEPINDEX}, {@link PacketId#RESPMAXSWEEPINDEX}, {@link PacketId#RESPSWEEPWRITERESULT},
     *                  {@link PacketId#REQSWEEPSECTIONS}, {@link PacketId#RESPSWEEPSECTIONS}, {@link PacketId#INFDISPLAYDATA},
     *                  {@link PacketId#REQTURNOFFMAINDISPLAY}, {@link PacketId#REQTURNONMAINDISPLAY}, {@link PacketId#REQMUTEON},
     *                  {@link PacketId#REQMUTEOFF}, {@link PacketId#REQCHANGEMODE}, {@link PacketId#REQSTARTALERTDATA},
     *                  {@link PacketId#REQSTOPALERTDATA}, {@link PacketId#RESPALERTDATA}, {@link PacketId#RESPDATARECEIVED},
     *                  {@link PacketId#REQBATTERYVOLTAGE}, {@link PacketId#RESPBATTERYVOLTAGE}, {@link PacketId#RESPUNSUPPORTEDPACKET},
     *                  {@link PacketId#RESPREQUESTNOTPROCESSED}, {@link PacketId#INFV1BUSY}, {@link PacketId#RESPDATAERROR},
     *                  {@link PacketId#REQSAVVYSTATUS}, {@link PacketId#RESPSAVVYSTATUS}, {@link PacketId#REQVEHICLESPEED},
     *                  {@link PacketId#RESPVEHICLESPEED}, {@link PacketId#REQOVERRIDETHUMBWHEEL}, {@link PacketId#REQSETSAVVYUNMUTEENABLE},
     *                  {@link PacketId#UNKNOWNPACKETTYPE}
     *                  }
     return A String representation for the supplied packetId.
     */
    public static String getNameForPacketIdentifier(@PacketID int packetId) {
        switch (packetId) {
            case REQVERSION: return "REQVERSION";
            case RESPVERSION: return "RESPVERSION";
            case REQSERIALNUMBER: return "REQSERIALNUMBER";
            case RESPSERIALNUMBER: return "RESPSERIALNUMBER";
            case REQUSERBYTES: return "REQUSERBYTES";
            case RESPUSERBYTES: return "RESPUSERBYTES";
            case REQWRITEUSERBYTES: return "REQWRITEUSERBYTES";
            case REQFACTORYDEFAULT: return "REQFACTORYDEFAULT";
            case REQWRITESWEEPDEFINITION: return "REQWRITESWEEPDEFINITION";
            case REQALLSWEEPDEFINITIONS: return "REQALLSWEEPDEFINITIONS";
            case RESPSWEEPDEFINITION: return "RESPSWEEPDEFINITION";
            case REQDEFAULTSWEEPS: return "REQDEFAULTSWEEPS";
            case REQMAXSWEEPINDEX: return "REQMAXSWEEPINDEX";
            case RESPMAXSWEEPINDEX: return "RESPMAXSWEEPINDEX";
            case RESPSWEEPWRITERESULT: return "RESPSWEEPWRITERESULT";
            case REQSWEEPSECTIONS: return "REQSWEEPSECTIONS";
            case RESPSWEEPSECTIONS: return "RESPSWEEPSECTIONS";
            case REQDEFAULTSWEEPDEFINITIONS: return "REQDEFAULTSWEEPDEFINITIONS";
            case RESPDEFAULTSWEEPDEFINITIONS: return "RESPDEFAULTSWEEPDEFINITIONS";
            case INFDISPLAYDATA: return "INFDISPLAYDATA";
            case REQTURNOFFMAINDISPLAY: return "REQTURNOFFMAINDISPLAY";
            case REQTURNONMAINDISPLAY: return "REQTURNONMAINDISPLAY";
            case REQMUTEON: return "REQMUTEON";
            case REQMUTEOFF: return "REQMUTEOFF";
            case REQCHANGEMODE: return "REQCHANGEMODE";
            case REQCURRENTVOLUME: return "REQCURRENTVOLUME";
            case RESPCURRENTVOLUME: return "RESPCURRENTVOLUME";
            case REQWRITEVOLUME: return "REQWRITEVOLUME";
            case REQABORTAUDIODELAY: return "REQABORTAUDIODELAY";
            case REQSTARTALERTDATA: return "REQSTARTALERTDATA";
            case REQSTOPALERTDATA: return "REQSTOPALERTDATA";
            case RESPALERTDATA: return "RESPALERTDATA";
            case RESPDATARECEIVED: return "RESPDATARECEIVED";
            case REQBATTERYVOLTAGE: return "REQBATTERYVOLTAGE";
            case RESPBATTERYVOLTAGE: return "RESPBATTERYVOLTAGE";
            case RESPUNSUPPORTEDPACKET: return "RESPUNSUPPORTEDPACKET";
            case RESPREQUESTNOTPROCESSED: return "RESPREQUESTNOTPROCESSED";
            case INFV1BUSY: return "INFV1BUSY";
            case RESPDATAERROR: return "RESPDATAERROR";
            case REQSAVVYSTATUS: return "REQSAVVYSTATUS";
            case RESPSAVVYSTATUS: return "RESPSAVVYSTATUS";
            case REQVEHICLESPEED: return "REQVEHICLESPEED";
            case RESPVEHICLESPEED: return "RESPVEHICLESPEED";
            case REQOVERRIDETHUMBWHEEL: return "REQOVERRIDETHUMBWHEEL";
            case REQSETSAVVYUNMUTEENABLE: return "REQSETSAVVYUNMUTEENABLE";
            case UNKNOWNPACKETTYPE: return "UNKNOWNPACKETTYPE";
            default:
                return "UNKNOWNPACKETTYPE";
        }
    }

}
