/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.remoting.transport.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.Codec2;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.buffer.ChannelBuffer;
import com.alibaba.dubbo.remoting.buffer.ChannelBuffers;
import com.alibaba.dubbo.remoting.buffer.DynamicChannelBuffer;

/**
 * GrizzlyCodecAdapter
 * 
 * @author william.liangf
 */
public class GrizzlyCodecAdapter extends BaseFilter {

    private final Codec2          codec;

    private final URL             url;
    
    private final ChannelHandler  handler;

    private final int             bufferSize;

    private ChannelBuffer previousData = ChannelBuffers.EMPTY_BUFFER;
    
    public GrizzlyCodecAdapter(Codec2 codec, URL url, ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
        int b = url.getPositiveParameter(Constants.BUFFER_KEY, Constants.DEFAULT_BUFFER_SIZE);
        this.bufferSize = b >= Constants.MIN_BUFFER_SIZE && b <= Constants.MAX_BUFFER_SIZE ? b : Constants.DEFAULT_BUFFER_SIZE;
    }

    @Override
    public NextAction handleWrite(FilterChainContext context) throws IOException {
        Connection<?> connection = context.getConnection();
        GrizzlyChannel channel = GrizzlyChannel.getOrAddChannel(connection, url, handler);
        try {
            ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer(1024); // 不需要关闭
            
            Object msg = context.getMessage();
            codec.encode(channel, channelBuffer, msg);
            
            GrizzlyChannel.removeChannelIfDisconnectd(connection);
            Buffer buffer = connection.getTransport().getMemoryManager().allocate(channelBuffer.readableBytes());
            buffer.put(channelBuffer.toByteBuffer());
            buffer.flip();
            buffer.allowBufferDispose(true);
            context.setMessage(buffer);
        } finally {
            GrizzlyChannel.removeChannelIfDisconnectd(connection);
        }
        return context.getInvokeAction();
    }

    @Override
    public NextAction handleRead(FilterChainContext context) throws IOException {
        Object message = context.getMessage();
        Connection<?> connection = context.getConnection();
        Channel channel = GrizzlyChannel.getOrAddChannel(connection, url, handler);
        try {
            if (message instanceof Buffer) { // 收到新的数据包
                Buffer grizzlyBuffer = (Buffer) message; // 缓存

                ChannelBuffer frame;

                if (previousData.readable()) {
                    if (previousData instanceof DynamicChannelBuffer) {
                        previousData.writeBytes(grizzlyBuffer.toByteBuffer());
                        frame = previousData;
                    } else {
                        int size = previousData.readableBytes() + grizzlyBuffer.remaining();
                        frame = ChannelBuffers.dynamicBuffer(size > bufferSize ? MAM�:  �������ɩ�������ʧ����������������������������������ʫɷ�������ǻ˩�ʜ�����Ǫ�����ɷ������ʷ��������ɺ���ɗ��˨�˺��ɪ�ɻʸ��             ���    ���     ��    ����   �����   ���� � ����w  ���ʈ ����	� ����ʹ ����˰   � �{   �                ��D����2u�}2BQd�L�b���U3fE/���y�Ӫ_�0����
<C�"Űb�(j|e%j���||��?��7�q���ҧn��d��U�|�u���@v|����$�U��r��v�.�@�~�����|�䷣<�`���`>�̉������ը��������I/p!�靍���٪��H��Xj�ԫS}��2�����A�_��J{��Db\k'�\�s��Ր�Ū$Z4~U������n���N�&!�;]@E��XA�xӦV���~t���UP� ,��|� ���%]��h�C�Aۓ0{B@U��Y�2AjA����@��f����k�|4Ł%��YJ��σ�N�P�,0�4֠�Tu%�~[O-P�J��2P�U �����OP51V�oN��=(R��N���(Yg��+�Z-JJ�Y��c���5=hI0J�*�,�d��ke�s���tq1���'���e�S	��@y����vN0P�*8*��ɟ�Xl*~DM`��Ӭ��[&�.P�=*f=���� ���볢���(pZ�@��]jip��p҆�a���� �!"(*.�G��*@�@��/" 13 @�N������ !")(A�
P/�� �`f ��6Pp9;��=?� AC @E#(8�)ߺ��Ƞ@���@���@� �A�AJ��`
�S�] po��^l`�pt `x� ��G`0�)Rd��~p�9;��AT@}	�C`80�:<��F@�]�47P�C/pc�p+�̀�������H�]_� ��z)� �̀:l��pt `x|����?�`x���7�$up>�5 �A�.�E�6�Ƌ
�Q�ŏ�!��<� z���85S��������z���� �
�GPH^)�U���ჴ�nXU9�'̱)��H)���~X�r�a��X�Ju�ER�=,���a�ԎX��v�E�njX����H8�R��xR�ዔ�Q��N=P&@3<P��j��p�@�`mH&�LP��C�r	@������@���@�A
J.p������@'���� A�JA�
�S`�� r]^� bf@��6Pp9;��>#(8�)I�,�����E"u��|�;;cł�|�n� 8�q� ��~9��c@���� 9~a�ð�,d��A$�h=p9@~�9 tA��A������p�`|`v ���ކ����MU���c�aM�p����Oi��$�%`cpm(vD��3�>؅[�
QP����b!7�E�`���j��nr `x|�����P
FTp���b `�H�X� �D/�4�v����������@ 1�����(4���s������H�1A��B��̖/3�f��� 10I!`������j�L.]x��6���9;��=?� AC @