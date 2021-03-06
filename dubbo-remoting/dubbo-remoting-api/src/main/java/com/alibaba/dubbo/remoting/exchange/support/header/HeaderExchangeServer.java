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
package com.alibaba.dubbo.remoting.exchange.support.header;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.Server;
import com.alibaba.dubbo.remoting.exchange.ExchangeChannel;
import com.alibaba.dubbo.remoting.exchange.ExchangeServer;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.remoting.exchange.support.DefaultFuture;

/**
 * ExchangeServerImpl
 * 
 * @author william.liangf
 */
public class HeaderExchangeServer implements ExchangeServer {
    
    protected final Logger        logger = LoggerFactory.getLogger(getClass());

    private final ScheduledExecutorService scheduled                 = Executors.newScheduledThreadPool(1,
                                                                                                        new NamedThreadFactory(
                                                                                                                               "dubbo-remoting-server-heartbeat",
                                                                                                                               true));

    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;

    // 心跳超时，毫秒。缺省0，不会执行心跳。
    private int                            heartbeat;

    private int                            heartbeatTimeout;
    
    private final Server server;

    private volatile boolean closed = false;

    public HeaderExchangeServer(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("server == null");
        }
        this.server = server;
        this.heartbeat = server.getUrl().getParameter(Constants.HEARTBEAT_KEY, 0);
        this.heartbeatTimeout = server.getUrl().getParameter(Constants.HEARTBEAT_TIMEOUT_KEY, heartbeat * 3);
        if (heartbeatTimeout < heartbeat * 2) {
            throw new IllegalStateException("heartbeatTimeout < heartbeatInterval * 2");
        }
        startHeatbeatTimer();
    }
    
    public Server getServer() {
        return server;
    }

    public boolean isClosed() {
        return server.isClosed();
    }

    private boolean isRunning() {
        Collection<Channel> channels = getChannels();
        for (Channel channel : channels) {
            if (DefaultFuture.hasFuture(channel)) {
                return true;
            }
        }
        return false;
    }

    public void close() {
        doClose();
        server.close();
    }

    public void close(final int timeout) {
        if (timeout > 0) {
            final long max = (long) timeout;
            final long start = System.currentTimeMillis();
            if (getUrlMAM�L  ���̨��������������˪��ə��������������������������ʪ�ʼɸʫ������������������������ʸ����˪ʸ˩���
���ʫ�����ʪ��ʪ
��������Ⱥ�             �x�     ��  ��
    �� 
  ����������� �����w� ����x�� ���������
�� ���� 
 ��
   ���{             ��)��;I܏TI$�b��R	���ov�7��'=�f��D��Ξ��>�{N�����'J���<4�>���ٷ����+8v^^��C���r�w]�1]Ǽ�1;ﭻ��S�}I��g�=���N�^��y^��պ�a���}���E�Q���Sn�ѭ�3;��N�E���A-���O��
�������u Ul�eۙQ�'���� U.ީ��)���x���'Bw��)\� t&F`ex/��'�L¾�;-�U�0�=э'm\Ln�jX��`Q�i�[0kS]X�u�Q�I_��.����H``�Qw��4:X�
Ft�U@���ҕ߀�j�&:VFYh��V��=:Y��:�
�C�����/���a䶿�X�5]�� ��Pv�kXr����H�ԙ�Ŭ�DJdv$��C��WTUetм�G�u�X1�7^���
���^0V�kqtI���˽�QOqX�膲J�oZ2\ �@����+`n �I���B��@�;���@ְ���լ���S�X�
�(m�	�c^ T'��t� k2����ҖE�<h�J�+�����Km2��,�2��`ٺj��h���A��	2,��
�N`��{*����2��� �w VN�`�t-st+�Vg� R'+��C�Vj�u~T!�}��J���qS�*�3�@5%����ܫ�;�M�l��'�@�
Q���E_ȋ@�/1 hH��A	,6ppL�h�`�'�>� 
�P��`(*"�� `� �I �%�.��	n8��Ml����'
@Q��T��r����^`@�40PK\܀ p� �'	:O��� �)J�FE@�$h`���� &�@��'
@Q��e�x�`A	,6p�Mp�� �":&��Xl��� ��(413 pN�)QN�x\�0r �	Nx�`(!"�q���"���� �/1 hH��A	,6ppL���V�;�f������d\���u��`�?oN��58}1��o".�����2T����l�s����Ag�ܠ �@��'
>P ���N��>>8>H>X;�w`�`�(|D){T�lQ+^{ ��DasT"�࣊���*�:>��*��"�厊D�}T�Q�(�TD�w�"(돊��?*Dy[T:��`m���y����Af@��������Q����[���^� �����%.n��	9'�:�	�O�X��/1 h�`� �`��'
>P ��`�"(,@�/� 1hH�ԗ�`7'�@�
�QD!o���^�*����"�����Q����q4Y�k6yYȪ m����#�ā�ڌJ�qA��x�h��8���h�Hx���� ?HL9x|Ps ��A����� |pv��@�AzH	C���S n_�)t^���n�2o[��q�qz(0�A�Z#�+0	c�/����
�HL"xCm@��,��fp�#��@@fj�`	�K`�@;d�v�����Vݱ��F�πA�q��@�`� ����=}�AQ��[� ^F�.�CMl����	@�
Q���C�.��0F���$�� ��7`�&�6��	�Nx���(
FR@!���"P����d� 	35@��%�6�	O|� �(HbJ�^FnH�	88��g�|���Kځ�kl��<�xA�ٳO.H�`�Az��H8Pp��r@�@��с����`7@o��	�a ��,��u����/� 1hH��,Ap	6Lp�A'	<P������a�`��'J�P�"��@��k����>����qC����_ ���������������b,�&�	�g�2М���D�&:�M�0�@�5�� &�`[�2�0M�s �50`8&�:��	�O��`(5"ŤdhP�������h��U���Xp  �'�:
Q�N�"B!�.��0F���$�.��9���&	8P��z�"�@M��r�A5��$/h��<@ 2���	&x��`�"� �s�2`� X��&@� `n��w�H�ʃ�D3�� �<L0�wh������A$&1`���L��9;w�f�w��@��,Ap	Q��� �3�C���@7`] zAH��9`|�zL�+�"J}��*kKNQ76NI�/� F�$|�^%��A��z,H��$@���@�r��	O|C}��	dK ���h�`��B��%�6��	P��@��r ��8�h�<���(je7Q(����ڿ��(�QQ���Q���TDjq�"Q�����C�(�B 
R�{����Aw�p�>Q�"�p�QUQ����De��u��*�u:Q��GEQ���>*����E�xG*�<QQ���Q��,���0��`�-�4�#�$A@08�~�=�8�{�r�*�3(� �x27�υb�d�`��PL0,��`:@uA�׃�����P��n�.XӀx�A5�"�l`*�8A���m�e�t@��� ��:OX�=�@i�:
,�[.� ��P�^�� 1(� ��dP(5�C�H^��!��i�6pC�L�p0�/2����(��*�E9QQ�����=QHy���
�����8A�W�W�L�>���	�8q'�gAQ��B6�ADh,�� �d�`h\b7~'�D	=��z� �:�~�;�x�ot��P	@p��A�v�+�L"�)�F�����b�l�q��TW}����I��~���4��$��p�#�S���h����G�}ȡ�����7D�E���Ѱ�dܵ��ƻ��7*E�Q��ʋ��
��"D=aTE�D���ƌ���l�.Gh��Q�QT�FEE�[�]�+�%&?l�8��>�;@w0� �`�������b�������9,��H�e��-!h`n� [�L`h`�( DA0
XE<X3�΀˃���A��0s �������D  s���¼^F ���t$�U;L���l�@8@A i,�� �%fQA�zT�}��!6��r0�����z9�z -(����.M�n�b��&�Ѣ��쉂Q���q|1A�x�@#�f@�	j6`pL5�����	8O�| �(H������4$�`	hR�*���`��	j|`u`� ��b���wA�Qq�5�TD�J��D	{T��Q�����Dר��=88�{�r���d
�?Гe�{���u��ȃ�e	�u��p��R��&hx�@�^�p&/���s�(=л=̝v���D;��h�P�'ⷰ�"��Ǟ�$b�
ä�$	rKwp�״M	]_�t8%�7鸿��7�� �/�}	#М c��8�K�Z�<JD	=���UHy-/F��U��Q�K��yJT���4�0VL�H�+$+@�����`�$����a��G�}�V�a��j�X?�Z��^N;�X�Ig��z�|a�h����h���&-h@[��,�԰&ƕ�������aR� ���ڕlRVOĞ=#s^��j�c�jX[Ifx\+`3�FK�sd��1(VLa;.���e�<��,���`p��z��'	>�L�5j�C�4º�����IP?k��I�!%�&gX�X=lQc�T�c����ػx��a��H��[�'tj�7�bqj�.Ҧ���^.|��0c�Tw=�)+�h�$-a�M(L��	eŲ��fE�:G���m_����[5LVo�lN�LfV�N���z�Ű&`/>$�F��9�|4���`�P�Z�U�dlו�M��f�+�A&�;X�Ea��yUbgb'��b��d�2Dx�F��`ĨM¿5&�뿂Xa��`�����a�bb�`�:d��wH8f�4t�4�X�7�ZA5�"Q�c+	�[��� aތ&J��B�`�~���{J�������ɇ�xG=������e3��H�s����d��1������N�B2��?ެ�՗���!�?a�C�#q)���h�0�!�ц5aC��a�����O�0�!!�'0a	C"F��0!z�����J�0D!\��`��x��W�#c�N�2�E+�Md���O�&{�D��1�:�g?RS!b�&R���6�Y�l���2���� W�c����,�w
#���_5$e����4�1DI�1Q )d���DDDDDDD�D �                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  