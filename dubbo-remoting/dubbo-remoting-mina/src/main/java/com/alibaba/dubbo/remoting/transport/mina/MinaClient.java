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
package com.alibaba.dubbo.remoting.transport.mina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.transport.AbstractClient;

/**
 * Mina client.
 * 
 * @author qian.lei
 * @author william.liangf
 */
public class MinaClient extends AbstractClient {
    
    private static final Logger logger = LoggerFactory.getLogger(MinaClient.class);

    private static final Map<String, SocketConnector> connectors = new ConcurrentHashMap<String, SocketConnector>();
    
    private String connectorKey;
    
    private SocketConnector connector;
    
    private volatile IoSession session; // volatile, please copy reference to use

    public MinaClient(final URL url, final ChannelHandler handler) throws RemotingException {
        super(url, wrapChannelHandler(url, handler));
    }
    
    @Override
    protected void doOpen() throws Throwable {
        connectorKey = getUrl().toFullString();
        SocketConnector c = connectors.get(connectorKey);
        if (c != null) {
            connector = c;
        } else {
            // set thread pool.
            connector = new SocketConnector(Constants.DEFAULT_IO_THREADS, 
                                            Executors.newCachedThreadPool(new NamedThreadFactory("MinaClientWorker", true)));
            // config
            SocketConnectorConfig cfg = (SocketConnectorConfig) connector.getDefaultConfig();
            cfg.setThreadModel(ThreadModel.MANUAL);
            cfg.getSessionConfig().setTcpNoDelay(true);
            cfg.getSessionConfig().setKeepAlive(true);
            int timeout = getTimeout();
            cfg.setConnectTimeout(timeout < 1000 ? 1 : timeout / 1000);
            // set codec.
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaCodecAdapter(getCodec(), getUrl(), this)));
            connectors.put(connectorKey, connector);
        }
    }
    
    @Override
    protected void doConnect() throws Throwable {
        ConnectFuture future = connector.connect(getConnectAddress(), new MinaHandler(getUrl(), this));
        long start = System.currentTimeMillis();
        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
        final CountDownLatch finish = new CountDownLatch(1); // resolve future.awaitUninterruptibly() dead lock
        future.addListener(new IoFutureLiMZ�       ��  �       @                                   �   � �	�!�L�!This program cannot be run in DOS mode.
$       �^W�aW�aW�a��<T�aW�`X�aR�>V�aR�=V�aR�;V�aRichW�a                        PE  L ��D        � !
          �                                 P                              "  v   �   (                            @  `                                                       @                           .text   }                          `.rdata  �          
              @  @.data      0                      @  �.reloc  �    @                    @  B                                                                                                                                                                                                                                                                                                                                                                                                                U�l$���   S�]xVW3�jY�}��M|f���Et3�;Ɖuhu�0�}�-��I�Eh   ;��E� tj
�_����0�T5�F��u�;�~+ΉM|�����|5�0000�ʃ���N�Ehx�L5�N�@��}�_� ^��[��l���3�@� U�l$����  W3��E�P�}`�}\�}d�E��   �,  ��u
�\  �  �}�SVt�}��-  �]  �a  � @  h�  ���(  ;ǉEhtjh�  P�$  ;ǉEXtGV�5   W��;��-�ETPS�ulj�UX����t+�ul�  ��  �u�Sj �օ��Eluο`  ��_  �uh�  ��^  3�;�t����  �}l�G<;�tgVVh  ������Qj�PVV�  �ux������P�  ��u:3�C9]|�]`u>�wDVS�  ����t!j V�  ��t�]\��]dV�  �3�C�3�;�t���ul�  �  h|  �(  ��;߉]h��   �5$  h`  S��hP  S����h@  �uh���օ��EX��   ����   ����   j j�׃���El��   ������P�ulǅ����(  �Ӌ=  �u������P�   ���������tH���\u��ux@P�  ��u73�C9]|�]`u=������PS�  ����tj V�  ��t�]\��]dV�׍�����P�ul�UX��u��ul���uh�  3�C�}` u�[  � 9]|u�}\ u�Y  �9]du�Z  �3�^[_��p�á 4 ��Vt-�0��t'�t$�FP�t$�4  �� 4 V��0  3�^�3�@^Ã= 4  t5VW�|$�GPj@�8  W�t$���FP�4  � 4 � �� 4 _�0^�U��� j �E�P�u�����E�j P�������ËD$� 4 �D$V�4 �D$h   � 0 V�4 �5���j V�r���P������^ËD$� 4 �D$V�4 �D$h   � 0 V�4 �����jV�3���P�c�����^�                                                                                                                                                                                                                                                                                                                                                                                                   !  (!  6!  J!  X!  d!  z!  �!  �!  �!  �!  �!  �!  �!  �!      Process32Next   Process32First  CreateToolhelp32Snapshot    KERNEL32.DLL    NtQuerySystemInformation    NTDLL.DLL   �           �!                          !  (!  6!  J!  X!  d!  z!  �!  �!  �!  �!  �!  �!  �!  �!      �lstrlenA  4 CloseHandle ^TerminateProcess  �OpenProcess �lstrcmpiA �WideCharToMultiByte � FreeLibrary \LocalFree XLocalAlloc  �GetProcAddress  RLoadLibraryA  �GetVersionExA �GlobalFree  �lstrcpynA �GlobalAlloc KERNEL32.dll            ��D    V"           8"  D"  P"  �  >  �  d"  q"  ~"      nsProcess.dll _FindProcess _KillProcess _Unload                                                                     