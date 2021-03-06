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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.cluster.directory.StaticDirectory;
import com.alibaba.dubbo.rpc.protocol.AbstractInvoker;

/**
 * FailoverClusterInvokerTest
 * @author liuchao
 *
 */
@SuppressWarnings("unchecked")
public class FailoverClusterInvokerTest {
    List<Invoker<FailoverClusterInvokerTest>> invokers = new ArrayList<Invoker<FailoverClusterInvokerTest>>();
    int retries = 5;
    URL url = URL.valueOf("test://test:11/test?retries="+retries);
    Invoker<FailoverClusterInvokerTest> invoker1 = EasyMock.createMock(Invoker.class);
    Invoker<FailoverClusterInvokerTest> invoker2 = EasyMock.createMock(Invoker.class);
    RpcInvocation invocation = new RpcInvocation();
    Directory<FailoverClusterInvokerTest> dic ;
    Result result = new RpcResult();
    /**
     * @throws java.lang.Exception
     */
    
    @Before
    public void setUp() throws Exception {
        
        dic = EasyMock.createMock(Directory.class);
        
        EasyMock.expect(dic.getUrl()).andReturn(url).anyTimes();
        EasyMock.expect(dic.list(invocation)).andReturn(invokers).anyTimes();
        EasyMock.expect(dic.getInterface()).andReturn(FailoverClusterInvokerTest.class).anyTimes();
        invocation.setMethodName("method1");
        EasyMock.replay(dic);
        
        invokers.add(invoker1);
        invokers.add(invoker2);
    }

    
    @Test
    public void testInvokeWithRuntimeException() {
        EasyMock.reset(invoker1);
        EasyMock.expect(invoker1.invoke(invocation)).andThrow(new RuntimeException()).anyTimes();
        EasyMock.expect(invoker1.isAvailable()).andReturn(true).anyTimes();
        EasyMock.expect(invoker1.getUrl()).andReturn(url).anyTimes();
        EasyMock.expect(invoker1.getInterface()).andReturn(FailoverClusterInvokerTest.class).anyTimes();
        EasyMock.replay(invoker1);
        
        EasyMock.reset(invoker2);
        EasyMock.expect(invoker2.invoke(invocation)).andThrow(new RuntimeException()).anyTimes();
        EasyMock.expect(invoker2.isAvailable()).andReturn(true).anyTimes();
        EasyMock.expect(invoker2.getUrl()).andReturn(url).anyTimes();
        EasyMock.expect(invoker2.getInterface()).andReturn(FailoverClusterInvokerTest.class).anyTimes();
        EasyMock.replay(invoker2);
        
        FailoverClusterInvoker<FailoverClusterInvokerTest> invoker = new FailoverClusterInvoker<FailoverClusterInvokerTest>(dic);
        try {
            invoker.invoke(invocation);
            fail();
        } catch (RpcException expected) {
            assertEquals(0,expected.getCode());
            assertFalse(expected.getCause() instanceof RpcException);
  MAM�~  �����������̺����������ɹ�����������������������ʷ��ٷ ������ǻ�ɷ���׺�����ʧ˪ɧ���Ǻ���ڼ
����ڪ�����׻����˺�ʼ��ۻ�����Ȼ�             �� �    x��   ����   ��ڧ
������н������� �������͐������������������ ͐��� �����Э��}   �        �񗉪���6"'�����T�����t��1Л?_��Q8�	�qOD`1z#*��=D�S�©e���M�P��,��|��߾p	�xտ�|����T����s��K���#5~���5;,���տ`�c?}��>�� ���ǃ���D�^�ߙ�B�������
 м����o�`����l�y,�ds��k��h��-�����br`M��}����*^i�<d�pu۬�~A�~U��0�7z �d7bk̖�@#�'�љ�=�XU��j��D�JI�� ��Ae��hrG�D�{�,05ӊ����P��fXP�����8�AR�缢ֳ5#Y����U���J��A�amn�H�J<k|�
*����P���r���A�Жy��|0�?��Nrx.Qͼr��f��X�R<T:[��<� �\>p޺����UhB�=e�̃��AR�k�]8$�u@�n�μTU�hA������N\Pb�r�@�����)M?oP��=�����|�PbT+��ƋtHp�� xPA�{^�~wѧ��[PWzg�xg� ���MO�^:̒�ѧ���.�v;�D���0թ����JH.?<ի=ph�1!���>m�ǃ����Ϣ��Ph�f��) k����I�W�*��6�L� Z�ߠU�y'�H�����t;=���Q�s 7���Tw���E��x��ʶ$��L蕀52��~����{@�l�wJ;J�p ��H3!�J����'����m��P��0�]u=��U�T��*�����:0<��ΫP��\�t7��TU��y�x�=�6�e:�@pT�-�}\=t�N@���I��~P��g�^�I�����{�l9^��U�R"�����U߳��O<���.���g�a̫�wq�wO�4a��ST���y*H��|Z�ZP�`
R�u�E7ѥ�m=�*{�x��ƍK����0Pʩ*A���)k�,�0�bʃ@��t킕���T�K��>Ƞo�敐&h�xP�in�T�0}�&ɀ\Pc�b<�'��d����f�0-e{iA��� *�
^��pZv�_�&�a0�inT�]RC�'T*�My<�ڜ��DC��V���� '@�K��@�� ��� ��

�BD0PGI��V e'ˀ�� ����@� �A�A ����	�l5:`�>A  CE@`G1����P6:p�<>��AC @EG`�J+`�@���27`�9<��@FPpHJ�h���#�:p�D`R�l8:��<G�`�4t7D�C.�
�P���@����(A�HYX`u��+Ex����0_.0�240P68p�:<��@6��8�������A�A��q:i��22h� �@���>}a����@���@���@���@ �@��l����������@�� >Z76��7�o�������,@q���t�t6�
�"%0�Y�3+ ��b��  -�y��y��z0�~0�=��2=��b9���~0�Z4�`���ݚE��X�����B���:x��Ɩ��<r�� �.�k?b��A@�`r��̘��*�z~@�KĚ캁J�@3����@TP{�Ab�`B��T���9 t�����P�J�T��
�u�E�Va���P&j�L��0�R��`I-�&�R��2`R-˂I1�������L,!�_�>�`n��0�%6.�Ċ#,���{����2P���.�)@���@� �A�A �A(�@�EY�`���@���@���@���@� A�A� (A��0��^b @fn� rv@�z��T~Ԋ��	��@lӱ|��WS`8&�+`R�d&R�j`&��`R�n&Rr`&�+`R��&R��`&��7Q�.���=k�$%Hf�
\f �j��@����.���n� ���@	�����Ԁ���AZ+�pa����������Ё�������>0g�U�{{�IO~�qx���,p���U����a3S��A�e�������]�Z�,0�240P69��;>�BD0PF�PB+Z����05@�Hb� ��Vf\PwC�>@R���j��&f@�jn� tx`��ʇ�AC @EG`�IV��Wa�S'Le�4�0#;I�/��ӠV�| }{�A�ϖ�A'����~�|� [߿pW_��Ԟ�Wz�{�b	G�PŰ:�|��ħ��8����T�G8.�>߀�����,����e�J�*��]@���Pq��@�
��:<��>A� �^K/�N���A���ѰjH�nr @x���� ���)N&�pfx��Ew#APV��D�X��
�,\h�`d `hl��pt <<��3����|5�Q�(K�X���.0��"��X�]�dB&�L`3����*h4Ȁ,�[���t	��0�����������~� Z!6 ��"��G���:�~3x�A����q����-�:J�xG��9�]�
WЀ[[""� [�u���%����Yn� X�G��[@"-PȈ.e���
X,�(�\`� dh`�nt `x|���� `����
��J+`�@��p���u -��d[;G`��I�a��P�ෆCպծ�LT��EpI�d�5�܀ ���A����� �
C�!r @G�!`n�����:@G�k���"��(V�1c���MM|�240�<Z`�I���6r@@v�����������Z��@� �HֆbЁ�����TJ̬C{@~P�|8���=��� ����ENICm��|7!����\-�E�	�� )A�@� �d��Q?A  C{@p|�������%��@n��L^\.�;�����kI��o,�G*P���9�Zpe�4��B���&��[B�!P ��"Lb�{������iA�bb�Z(u@5�h\Eh�����A2��� ! ��"#0@��$��`�����Xn�-u��<�0�������Dte6��Zc��\8j�#tPߨ� �� !e� ������׃���EHA柕���P7/(����������"a�@	�� +�h`�z������ 	�g��FYQAF��^S��<�y�q`� ��U����@�*�"��B���
�����D
�V�e�/� 1a ����2���t��A��� dDՀ �\��V���b @f��� �^a �jJx��1���Ѕ��ph
"HtQ.4 ��A��A(������=�9`~�?@(5�?:��#�E$a�mY@qp���l�J�;���6����A�HQpµ�ma�u���_e���&v��z~� ,��_J�0�E�'���XU��`pu�u[�xI�]���B��}	z2Դ���_��h�`j���C@B0DH�#H@��$+PZ�:m�^� ���`̠Zox�D��@�������,\^���A�� Ee��l@2aah����� B�0G�@�ʘ���ꈅu�4�?��F�q=(=�>�>?A�{ap�ξ C~w��Ԁ�.�C�=��w(���?�Ip_�@���@������ 3�	�a@@q�� ���@�l�Rs��! @��$���Ƶp�8:��<>���@��@�i���@	�hN-&�SA������ ��`lj�`��*�^�N�`R���>#
O-��VX�;���a_���@���@���@���@� A�A� (A��̸����@���@ �� ���A� Y��x���P����pt `z~� ��`����"f�RX8��c@����  ��!" 7��u@�$+PZ�&p������~�%>O�eø!���� !"#(v 
^� `�3�P�0��O9 C�U<��ب� ����Px��������v���a��@
�wЃ<,̐@���@� �AA� ��W�Dٴ?+��HS_�X�r�� �@sj��$���̴|32@��(�(�u��±�(�"$8V�P�`$ I�G�'	��@hn��\��`g N,Ё��Lҵ�"��b��ܢ����(�%艨 A#j(+��FhU`�	�7��� #z@�@�~um�%�Kw�d��'�o���t���>n�$�?�܅�Xa,��e�B�/���8f@�A����Ar�� ��yA��C�h�>���@��o@���E������sA�A�>H:|�n��1Re@���4cЂf���wP@8@pm�B��%J��ؐ�*&�����C:��5%��;�����kFk���R���B���j�J6��[���},^�W�������
����c��We�!����5 f��Ƞ���@���@�����?���D@pa� ��r@�|�@�X�M�"^5�,.������P+��Ԃ[�k��SG&�.&��
0�q`R�ɃI�(�>6lE;K�iA:⨁� ŏ��� 	��>l�*�����`SGǢ��<  q@���M�F\]�)l �A,3�@7���Q�@Ba� � ��"#0P���n� r�@������`ʁ�+5�>���&O*����i�5sA��2�G��ɆD P�켛3֎J�E�"���T���"m���QS⤆Ґ�4o�D�H`�J+`�@a��u���@5TP�� ! �"#0�蠐IV��������!�"��8p�����T)���p㷨뭺+b�,��e��-�����%��˷�������:d����ޮ?�U��wv�s!Wַe�~�3�����E�{�󿮋0���XZ�h5^`(�@���@� A�A� (A���� ���������������� �� �B�]zl@�gq���®�ܦ�bܦ�bצ>F�Dhx���5�lv.l�K3������=��04A�k�QQ��Q0�T��@F��<�J�>X-H6�A�Ԥ 5º�%#©�� �0u#T���zP>�H	��K�B���`��pt `0y#T>���� �Qs@������
�u@�`�x���
f.Z.X.����@���������@��(n�5�^(2�q��`��FE(N���҄Pѥ�	!n�P��EE��[BT��#6�n	CQH�(�K��r�����Ϛ�TX � ?�|�g	v~�f����\� ��bl�5K�����z��~� F�mGB�I�=P�@;��=����4`
~��"c�U���^�5�����
[�U��P�"ݥ����X�u�@,�����������7�~<-�0c��� �������������oxT�������ۋG��Ag���<L9�g����F�@�: |�i�_��醷śi�X��bn���`Q7