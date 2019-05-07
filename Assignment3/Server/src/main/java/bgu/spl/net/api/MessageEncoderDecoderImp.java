package bgu.spl.net.api;

import bgu.spl.net.messages.*;
import bgu.spl.net.messages.Error;

import java.util.ArrayList;

public class MessageEncoderDecoderImp implements MessageEncoderDecoder<Message>{
    private ArrayList<Byte> bytes;//saves all the bytes we got so far
    private short currOpcode;//Opcode
    private int zeroCounter;//in case the message the client sent  has 0 in it
    private short numOfUsers;//in case the client sent a list of users

    public MessageEncoderDecoderImp(){
        bytes = new ArrayList<>();
    }

    public Message decodeNextByte(byte nextByte){
        if(bytes.size() == 0 ) {//the first byte we get
            bytes.add(nextByte);
        }
        else if(bytes.size() == 1){//the second byte we get
            bytes.add(nextByte);
            currOpcode=bytesToShort(bytes,0,1);
            //-----------------------------opcode3-logout--------------------------------------------------
            if(currOpcode == 3){
                this.reset();
                return new Logout(this.currOpcode);
            }
            //-----------------------------opcode7-userList--------------------------------------------------
            else if(currOpcode == 7){
                ArrayList<Byte> format = this.bytes;
                this.reset();
                return new UserList(format, this.currOpcode);
            }
        }
        else{//now we know the kind of message it is according to "currOpcode"
            //-----------------------------opcode1-register--------------------------------------------------
            if(currOpcode == 1){
                if(zeroCounter == 0 || (zeroCounter == 1 && nextByte != 0)) { //while reading the message
                    bytes.add(nextByte);
                    if(nextByte == 0)
                        zeroCounter++;
                }
                else{                                                       //next byte is the final byte
                    bytes.add(nextByte);
                    ArrayList<Byte> format = this.bytes;
                    this.reset();
                    return new Register(format,this.currOpcode);
                }
            }
            //-----------------------------opcode2-login--------------------------------------------------
            else if(currOpcode == 2){
                if(zeroCounter == 0 || (zeroCounter == 1 && nextByte != 0)) { //while reading the message
                    bytes.add(nextByte);
                    if(nextByte == 0)
                        zeroCounter++;
                }
                else{                                                       //next byte is the final byte
                    bytes.add(nextByte);
                    ArrayList<Byte> format = this.bytes;
                    this.reset();
                    return new Login(format,this.currOpcode);
                }
            }
            //-----------------------------opcode4-follow--------------------------------------------------
            else if(currOpcode == 4) {
                if(bytes.size() == 2)
                    bytes.add(nextByte);
                else if(bytes.size() == 3){//starting to set the "numOfUsers"
                    bytes.add(nextByte);
                }
                else if(bytes.size() == 4){//finishing to set the "numOfUsers"
                    bytes.add(nextByte);
                    ArrayList<Byte> temp = new ArrayList<>(2);
                    this.numOfUsers = bytesToShort(this.bytes,3,4);
                }
                else if(zeroCounter < numOfUsers) {//the amount of 0 and users in the message is equal.
                    bytes.add(nextByte);
                    if (nextByte == 0)
                        zeroCounter++;
                    if (zeroCounter == numOfUsers) {//we read all the users
                        ArrayList<Byte> format = this.bytes;
                        this.reset();
                        return new Follow(format, this.currOpcode);
                    }
                }
            }
            //-----------------------------opcode5-post--------------------------------------------------
            else if(currOpcode == 5){
                bytes.add(nextByte);
                if(nextByte == 0){
                    ArrayList<Byte> format = this.bytes;
                    this.reset();
                    return new Post(format, this.currOpcode);
                }
            }
            //-----------------------------opcode6-PM--------------------------------------------------
            else if(currOpcode == 6){
                bytes.add(nextByte);
                if(nextByte == 0) {
                    zeroCounter++;
                    if (zeroCounter == 2) {
                        ArrayList<Byte> format = this.bytes;
                        this.reset();
                        return new PM(format, this.currOpcode);
                    }
                }
            }
            //-----------------------------opcode8-stat--------------------------------------------------
            else if(currOpcode == 8){
                bytes.add(nextByte);
                if(nextByte == 0){
                    ArrayList<Byte> format = this.bytes;
                    this.reset();
                    return new Stat(format, this.currOpcode);
                }
            }
        }


        return null;
    }

    public byte[] encode(Message message){
        if(message instanceof Error){
            Error error=(Error)message;
            byte[] array=new byte[4];
            byte[] array1 = this.shortToBytes((short)11);//Error opcode
            byte[] array2 = this.shortToBytes(error.getMessageOpcode());//type of error
            array[0] = array1[0];
            array[1] = array1[1];
            array[2] = array2[0];
            array[3] = array2[1];
            return array;
        }
        else if(message instanceof Notification){
            Notification notification=(Notification)message;
            byte[] postingUser=(notification.getPostingUser()).getBytes();
            byte[] content=(notification.getContent()).getBytes();
            byte[] notificationArray= new byte[3+postingUser.length+content.length+2];
            byte[] opCode = shortToBytes((short) 9);
            notificationArray[0]= opCode[0];
            notificationArray[1]= opCode[1];
            notificationArray[2]=(byte)notification.getNotificationType();
            for(int i=0;i<postingUser.length;i++)
                notificationArray[i+3]=postingUser[i];
            notificationArray[3+postingUser.length] = '\0';
            for(int i=0;i<content.length;i++)
                notificationArray[i+4+postingUser.length]=content[i];
            notificationArray[notificationArray.length-1] = '\0';
            return notificationArray;
        }
        else if(message instanceof RegisterACK){
            RegisterACK registerACK=(RegisterACK)message;
            return handleSimpleAck(registerACK);
        }
        else if(message instanceof LoginACK){
            LoginACK loginACK=(LoginACK)message;
           return handleSimpleAck(loginACK);
        }
        else if(message instanceof LogoutACK){
            LogoutACK logoutACK=(LogoutACK)message;
            return handleSimpleAck(logoutACK);
        }
        else if(message instanceof FollowACK){
            FollowACK followACK = (FollowACK)message;
            return handleComplicatedAck(followACK);
        }
        else if(message instanceof PostACK){
            PostACK postACK=(PostACK)message;
            return handleSimpleAck(postACK);
        }
        else if(message instanceof PMACK){
            PMACK pmACK = (PMACK)message;
            return handleSimpleAck(pmACK);
        }
        else if(message instanceof UserListACK){
            UserListACK userListACK=(UserListACK)message;
            return handleComplicatedAck(userListACK);
        }
        else if(message instanceof StatACK){
            StatACK statACK = (StatACK)message;
            ArrayList<Byte> array = new ArrayList<>();
            byte[] array1 = this.shortToBytes((short)10);
            byte[] array2 = this.shortToBytes((short)8);
            byte[] array3 = this.shortToBytes((short)statACK.getNumPosts());
            byte[] array4 = this.shortToBytes((short)statACK.getNumFollowers());
            byte[] array5 = this.shortToBytes((short)statACK.getNumFollowing());
            array.add(array1[0]);
            array.add(array1[1]);
            array.add(array2[0]);
            array.add(array2[1]);
            array.add(array3[0]);
            array.add(array3[1]);
            array.add(array4[0]);
            array.add(array4[1]);
            array.add(array5[0]);
            array.add(array5[1]);
            return statACK.toArray(array);
        }
        return null;
    }

    private void reset() {//creating an array from arrayList
        bytes = new ArrayList<>();
        zeroCounter = 0;
        numOfUsers = 0;
    }

    private byte[] handleSimpleAck(SimpleACK simpleACK){
        byte[] array=new byte[4];
        byte[] array1 = this.shortToBytes(simpleACK.getACKopcode());
        byte[] array2 = this.shortToBytes(simpleACK.getOpcode());
        array[0] = array1[0];
        array[1] = array1[1];
        array[2] = array2[0];
        array[3] = array2[1];
        return array;
    }
    private byte[] handleComplicatedAck(ComplicatedACK complicatedACK){
        ArrayList<Byte> array = new ArrayList<>();
        byte[] array1 = this.shortToBytes(complicatedACK.getACKopcode());
        byte[] array2 = this.shortToBytes(complicatedACK.getOpcode());
        byte[] array3 = this.shortToBytes(complicatedACK.getNumOfUsers());
        array.add(array1[0]);
        array.add(array1[1]);
        array.add(array2[0]);
        array.add(array2[1]);
        array.add(array3[0]);
        array.add(array3[1]);
        int j = 6;
        for (int i = 0; i < complicatedACK.getUsers().size() ; i++) {
            byte[] user = (complicatedACK.getUsers().get(i) + '\0').getBytes();
            for (int k = 0; k < user.length; k++) {
                array.add(j,user[k]);
                j++;
            }
        }
        return complicatedACK.toArray(array);
    }
    private short bytesToShort(ArrayList<Byte> bytes,int left,int right) {
        short result = (short)((bytes.get(left) & 0xff) << 8);
        result += (short)(bytes.get(right) & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
