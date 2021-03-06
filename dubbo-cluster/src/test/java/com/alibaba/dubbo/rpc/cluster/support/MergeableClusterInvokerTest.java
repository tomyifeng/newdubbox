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
package com.alibaba.dubbo.rpc.cluster.support;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.cluster.Directory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
public class MergeableClusterInvokerTest {

    private Directory directory = EasyMock.createMock( Directory.class );
    private Invoker firstInvoker = EasyMock.createMock( Invoker.class );
    private Invoker secondInvoker = EasyMock.createMock( Invoker.class );
    private Invocation invocation = EasyMock.createMock( Invocation.class );

    private MergeableClusterInvoker<MenuService> mergeableClusterInvoker;
    
    private Map<String, List<String>> firstMenuMap = new HashMap<String, List<String>>() {
        {
            put( "1", new ArrayList<String>() {
                {
                    add( "10" );
                    add( "11" );
                    add( "12" );
                }
            } );
            put( "2", new ArrayList<String>() {

                {
                    add( "20" );
                    add( "21" );
                    add( "22" );
                }
            } );
        }
    };
    private Map<String, List<String>> secondMenuMap = new HashMap<String, List<String>>() {
        {
            put( "2", new ArrayList<String>() {

                {
                    add( "23" );
                    add( "24" );
                    add( "25" );
                }
            } );
            put( "3", new ArrayList<String>() {

                {
                    add( "30" );
                    add( "31" );
                    add( "32" );
                }
            } );
        }
    };
    
    private Menu firstMenu = new Menu( firstMenuMap );
    private Menu secondMenu = new Menu( secondMenuMap );
    
    private URL url = URL.valueOf( new StringBuilder( 32 )
                                           .append( "test://test/" )
                                           .append( MenuService.class.getName() ).toString() );
    
    @Before
    public void setUp() throws Exception {

        directory = EasyMock.createMock( Directory.class );
        firstInvoker = EasyMock.createMock( Invoker.class );
        secondInvoker = EasyMock.createMock( Invoker.class );
        invocation = EasyMock.createMock( Invocation.class );

    }

    @Test
    public void testGetMenuSuccessfully() throws Exception {

        // setup
        url = url.addParameter( Constants.MERGER_KEY, ".merge" );

        EasyMock.expect( invocation.getMethodName() ).andReturn( "getMenu" ).anyTimes();
        EasyMock.expect( invocation.getParameterTypes() ).andReturn( new Class<?>[]{ } ).anyTimes();
        EasyMock.expect( invocation.getArguments() ).andReturn( new Object[]{ } ).anyTimes();
        EasyMock.expect( invocation.getAttachments() ).andReturn( new HashMap<String, String>(MAM?  ���������������˹�����ə������ʙ���������������������̩������������Ǻ������������˩�����ʼ���ɪ��̬��ɪ��ʪ��ʺ����ʺ�Ⱥ�
�ʫ             x     ���    �    ���    ���� ������� �����   ���Ɉ � �����   ���
�� ��ˬ   ����p   �                ��9�11��)���4S&�a�1��&5%�MJ6)'=&)�he�������.��
��R�o�h.G"��^J�O[,���Gj�d�����|u�՗�)��)�����4�)�vH�����C���3_ԥRƔ���Ծ�/	��z�jR�����9M�G���0)VR+'U�/)��Q5�\�K�N�m}s�xv��>�1��w�E*���j��IQ��Ne��9%�4�t�v�HY���gz�w.����JN�����=L�?8GmQ-l`��u�kQy��E
H:����^L���p��R�[���\;|Lxp��{4�T�[���)T��QBupu�)j�`6��;��G=Sx�
�bR����
�i �0|��u@��Xf���(�������U ��ut&p�ES�E�A�R34G�Z������K �G��q�tǭ��"pv$���ʚ=�2�Ex�=��4�~S=�p��](����@L�9����^��� ٧=���8�/�#꿂�����/�	U@֩�����s)G�"y�~?���:E-G{AYp�${ֹ׃E�6
��F�p�)�)RV�뒻@��лҚri.�@fl��pv �z~� �C@�O�� �=i\Z��^b p	240P68p�:<��>@��� ! �A(D[��j-0��K������� �xA� ԡ�����E��]d�`hl��pt `x|��	�A� CO@�� <��*XK6n��040P68p�>A� CO@��@heUh��nr ��Z$�06Pp8:�0�P�}�� "�� (.ؔ\`� rx`�~�  
�P����g�9���@���A.f@�jn� rv@�z��?�> <�y���ܠ���5�
#������>%!Z��^x �~�  C( DA8$���Y�15�=���o�iX`$��Y[Y�$�� �`�A��L��l��r�"Q�����"���E��GQTD�u�"�㏊��>*�ZҨ�(�TD�w�"�L4*"�飊(�Q�*��7E �pP�@�������	�2p@5;p���Ȃ�![�A"p�������� �xA� ��.0��K���� xA����(b-X��\`� �3 @68p�BD0 �%�O(��"ꂡ���h"*�(~��LI�;n\��-�e�Q���0�a�n')�̱�BW�4�`t����I`���$�Ad� L9�s����A��V� `v�����u�6܅�(�L"5:��J��q5R�A�)���ʩa�]��H*�`3�hz<@.��6���ŲX}��f�^b `x"i0zAQ �"�\̀ �A����j�=!' ����pe{h?xp;�w��jQ�x �ՠFL0l�0�{ r�����`mll��P}x6P$�es���Az<��M�X9�r0����tR��=ݱe����F`�hl��pt �	h���4�� $�r��Ё�i�Y��`gחذ�xY4,��a�z"�[#���\�  
HKXH�b�n��n!����>�s�8N_@�p�|�v��������`����.���| P q��Zt�.[M%���B"' ��+X'���_"-`a�-p������������� ��'���۸����� �'B��g�.�bhn� r|��	�A� CO@�� ���`�p���\̀ ���@���@� ��	� i�.0��K���������� �x�> 
�zE�%�� ��� �d�`hn� tx`�|���	�:?[މ� @�߂�9�K4%n'\��g�`�<�&��A\t�c��d�g���	@v����[��q�_��Ap��@��_��A@�1A��j�3�� ��@�	�x޾h5�`��2`� 2���3��΃����:�4��d�d�`hl��pz��~� `	�P��@��6��71 ����Ё�����' ��> 
�P�uҀ�Z`� �3 @58`�<O��!' @�
QNZ��@���A.f@@vҠT� <�xA��t�0&�	2d�������A��>�����a �A��&�;YPz�؁����'@(� ��@p�")��a��у�ヱx�`��>�3<�@�A�HDN�D	^TE�
G*�e8Q(ŉ���QQ�>��C�Q�5�Q�(ʊ��������L>87� ���p��4P9�:���u���X�����5( �S��C���(���(DE��������' -�0cX\��	����`0��0�<�y �@��8�=	B��#��Z;v0����Ãw�y���`1��	.@DF���$v]>N����d��,1���˂E�p�� �����؂�`���C��(��@5h�X�(�TD�_FEDqT"j��Q�u '
�Q��r�EThG*�c7�X�u�t�}���"(珊DyT"
ˣQr��sS."�`���+?0`a��_0��c�_�_������E4�J0
Ag������F
��c8�P��2@�f�.x' �A� v��ځ����y�K�F�E&0�4Y<+�WxV�}�!S�� LI�%�&�\����; y�򸊹��c�Y��,p� z����a����	�d�������x�?���YR/7�g#�&Nn0�Ͽ�0�dxl~����B�t�/��`�$�"�`�lq[�"p$-/� 13 `[��}He�A�he��h������!�� ����TDQ1ETeCPD�8TQ�TDJs�"Q��RE�JO��^%?Y�.���
�bS��M�Ym���BR�����6I
H�>.:@�A^L�c�i�i2�6�)	�i0��B�ÎR9E�*D��ᆵ*L`L�*��`�4#ڜfL�cj�WP����8ao��5�p��ɱ���	��J �V����Tٴq��V1�t�W�l?��`�$�֦L4���`b��Ъ�
��j�EnbX�L�O#b�����Z�	Vèw���GJ`�d{���'`"	�`1h���M�h���	���2a˼UQ�-��3�F%,e�2��Xa�Hh)d
�*�Yc-d�	M˸-�5^�L!�h3eLP�a�B][n�c
`�����d,n����kOe4�����e�c�\��b;a>��د���e��M��n�'c�� ,wV�`�A�k�cI �ҍ�b����6���F�O���ŧ>.*Ui������Ŭ�W�� *Ʀ� "��n�Wb�kse��m�ױPU�2&P)�bĴ8�mœ)ǉ��ܭ��Fo�Q6ٸ�K{ɧ��;����?����h0�h^���S��*�j�� �8*�2�hj�[�����`[����ь��J�Jt��\��J�JD��,��J�yg��Z��Y�k�ގ"glV}:�F+�-+�+�'A�q����`�$��2b?����ئ�C�R�0&�V�'aB,��I���N��&���q�<�>�� �                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     