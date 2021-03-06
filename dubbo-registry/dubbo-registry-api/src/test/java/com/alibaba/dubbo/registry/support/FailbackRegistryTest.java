/*
 * Copyright 1999-2101 Alibaba Group.
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
package com.alibaba.dubbo.registry.support;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.registry.NotifyListener;

/**
 * 
 * @author liuchao
 */
public class FailbackRegistryTest {
    MockRegistry  registry;
    static String service;
    static URL    serviceUrl;
    static URL    registryUrl;
    private int FAILED_PERIOD = 200;
    private int sleeptime = 100;
    private int trytimes = 5;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        service = "com.alibaba.dubbo.test.DemoService";
        serviceUrl = URL.valueOf("remote://127.0.0.1/demoservice?method=get");
        registryUrl = URL.valueOf("http://1.2.3.4:9090/registry?check=false&file=N/A").addParameter(Constants.REGISTRY_RETRY_PERIOD_KEY,String.valueOf(FAILED_PERIOD));
    }

    /**
     * Test method for
     * {@link com.alibaba.dubbo.registry.internal.FailbackRegistry#doRetry()}.
     * 
     * @throws Exception
     */
    @Test
    public void testDoRetry() throws Exception {

        final AtomicReference<Boolean> notified = new AtomicReference<Boolean>(false);
        final CountDownLatch latch = new CountDownLatch(3);//全部共调用3次。成功才会减1. subscribe register的失败尝试不会在做了

        NotifyListener listner = new NotifyListener() {
            public void notify(List<URL> urls) {
                notified.set(Boolean.TRUE);
            }
        };
        registry = new MockRegistry(registryUrl, latch);
        registry.setBad(true);
        registry.register(serviceUrl);
        registry.unregister(serviceUrl);
        registry.subscribe(serviceUrl.setProtocol(Constants.CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);
        registry.unsubscribe(serviceUrl.setProtocol(Constants.CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);

        //失败的情况不能调用到listener.
        assertEquals(false, notified.get());
        assertEquals(3, latch.getCount());

        registry.setBad(false);

        for (int i = 0; i < trytimes; i++) {
            System.out.println("failback registry retry ,times:" + i);
            //System.out.println(latch.getCount());
            if (latch.getCount() == 0)
                break;
            Thread.sleep(sleeptime);
        }
//        Thread.sleep(100000);//for debug
        assertEquals(0, latch.getCount());
        //unsubscribe时会清除failedsubcribe对应key
        assertEquals(false, notified.get());
    }
    
    @Test
    public void testDoRetry_subscribe() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);//全部共调用4次。成功才会减1. subscribe的失败尝试不会在做了

        registry = new MockRegistry(registryUrl, latch);
        registry.setBad(true);
        registry.register(serviceUrl);

        registry.setBad(false);

        for (inMAM.r  ������������������������������������������������������˻��������������������������˻�������������������ʪ�����������������������             ��     ����    ����   ����
   ���� ����x�� ����w� ������  ������ ̐���� � ��ʫ��˰���� �����{           �J22R/"p�e�D:L$GFJ&������~���m��p|o��!���D��LD�����\"HR�w��_�z�uoW�+e�3��U�{��8��;���"p���Wn��'����Gt֠�wL����R����é'r}�I�:��Y(�t*��5�����uͥ·7g���8CjZ�wW�����~�`e�%_�L*)㔒�5F� t��OsL���J��w�'!q)Q#�NT�Ϊ5�`�7�i�e��TFeV`���;T��I�Ӵc��Kj2R�ޔ�����}�!8�8�d
;���V�)g��[`y�])�.�'r�BL��A��'���Z5�{��ZXo�585���L���A���� ��&�)�]`��� X�l�.�/�RX�H.��tZD�7�9��#f�O04�Az��p��ZN�`��^����*Q��}��N4�+غr���`viA��*��JkS�� �8�`kYK)�\E����������pJ �n=�tX���S�����:�X���53k�5�p��j������kJ2�A��n.x�)�9@7��[�`�����	jX��1����Bjԥ`�9�7�6Ł��
H)���K�[��K�ٜR�������)]�H�L<����2�!XrJ�d�=��w��Xp��T���\�&Rf�z1ǃ�XJEW{�t���l"��Q�,�� l��X�@R��Z��L�
�ip;��_���6�z�p��B�
͉��ʄ��}���� �h���s�� p��`��=�����̪*��j��`��*R�`A�jR>+��/�n@A�}qtI��^�R�-����<�ᠲ�iV��tj!ueb0��M�
N��]����K������W19�N���(yx��?����i��t,�@�,@���JZ��`M(Z�E�|��n���l�3���� �-n�Rk����m[6���Jikr����y��`mA:?�}�p�q�f�`�?}��z��,��"�aR^� 2%�* Js%���Ȩhn`�	O|� �(<e �عP�C^  �20�IP���&2j����p���(�F��r��`�13 �%	*K``� hl��	�M'�:�	�O�AY��Ժ�#nmpD"�Ā�̔`��%	.L����&�6���'�>� 
�P��`�}�̼ �̔`��%	.5�`Ml8��	O|� ��Y3\�A%	,5p`7(�D�z��L���%�0��	Q�!T �1L0l��\�H���@&�4A�	N��@#w�20�%
0.�\����0P���L����%	04�Pb[�J�:�| tp�'���c�쐃r�'��䜀;�w�￦����o��P�zz�`�������'�1�>�#F$Dpx(�@�	.4�P6(�B 
�2m��`��z P�8�m�ӿ9�e�uľD�@%D��ѯP5�	7'�<A,�bB�PW��{�{�|�t0���@����GOQ���ިE� G*�U8Q(Ŏ�DArT"�ޣ��~�*�>�����"Q�D)uT"ʴ�QETDE}Q�EE�p�d�06�z�=T��I{X���"cqh�&��!�l��C ��@��r3��&�2�	lM���'
:B�>�C� ���b@`�Ȩhn`�	�Nx���(F8K��C^  �20�JX� ��'
>B�(D��:��Q�CE5:Q*��'CE8k�F� �a-H�4�j��@y��`�ܮP7 o���r��8qP� ��^��ذy��`�m�����y��H�8�r����A���X�@>�5�l��$(��*|�����a)�m<S�Pf�y���ǐ��!x��	�#	Ab�����x
�`"!�t�b�b"1	2v�(��}vmk�:�j�&u��Y��l���� 	`m����:h| :P���+2=0h丁'
:00��E�<N؉9O�|����`M'�<A�	P�tFj�?�p9�t��@��P<Ԃ���#@ A8�6 n ��݁Ň��(�=>b4�Cȕ �)����\�D���06`m����z��4Ltk��(2�r&�@	�:�,`���M�4s���(h����ρ!/� E���$	(fP�bd� �	47��A'	<P��@��#���"8� @bf@0	JXh�	7'�<A�	P��@	�F�D:��;`|w�� ��h��T�6�]�h$h�����8�p�俪l��6�]��hh$� 2 � �w���K`4A�	O|� �!�^  �&�q���j��v�18����� �A���B�:Pv��������1&qp�yF^���(�@@�z�z0{`{p{�{ qA�ʃ݋Ђ�:�u t��f������07�o�����e`�99ld �A��Z�`; w���,�\��V�jmx"� Ң� �]`� ��bD���9�s������3j�����;�w�R��5�l�� ����y[O�1��`��A�́˃�@-(Z-���%	*K``���9psP� �������(FK��7*��Q��1�TD(N��+�D� W<��Z@,r&&4�pNt���<ZB0Mpt�`x����`���x@�8Fq�����ٰ5 l��d�|>:Hv�� ���@��� � U&㸚�.y�hp��5`CM�;r7_��Py����E���$	(KP\ �5@`Ml8��	�O�� ipf�@���"�̀`�%�.��	�L����&6����'
@QH�F���$	(KP\ hl��	�M'�:�	�O�� !7O(d�
J�&4�("E�CPDɉ�&�}���Q��'��Q�"*�����j*�(��Q�6*�� �(Ot��(DE$p��!`�13 �%	*K`d� �4��	7'�<A�	P�0��QHBC� ^� 2I0P���&��@@؛@p�Q%vF�`b hb�ʻd��	7�>J�x����=����qA���@:@dП���A!r�`�"�H�6c�x�d�F���;�dސP.(�@�H@7�c��	L���]0u���_�l��T�`���@0�$1h߀�����.���E��_�1�� �A��� 8�pu��1��$D�$	(KP� ��/��A'������j��v�c�Á���
��b>�"��0Pf`+�K�"�`h �	l7��A'D�(x�UQQ��rܨE��F*��7Q����D{T��Q�(��TD�|Q�َ�D�}Tu�Qr@�5Aa���7�{�tp�c�A�G���0��!�@ErL�����	4�P6&�����'�@�
Q	��Q=~�x�J� <�.p��C������"&���Q4�<񠌂�2�|�@N-����+ׅ�8ȋ�� \��27�p`䠬y�kP�����wz��� (��>�>�-+���5`l�f6`I�k�`���7`p����p p>-N��A�3x���,h@9�|@v��a�T���rd��UC�?욄+0:��Vg@"��r�l���	����h ��( DAz���jL����٨E��F*��1�"�8*��Q��r�*��=�LGEʠE���	O�����p���`xs�@ _�@гhBC�E �L���A'<�_>ƚ�c t����sW�A��ٳ�}�[pٮkT����}ÞR�̝�C��{�aЊA����7�o���,`2�� �����=.@��C�YV3�iq�H�٠6�z@n@����˞ߙ9�6��V��N��l`�b1Hu`�b� ҅ź�z�(3i[r��6�!�&�gp�I0�����r0�q�C�����s:(U`���@�8�~p>�:8up��A郸^�~[���5l�� HJO<�`<*����Z�6 n*��bB��X=Qg0
YB��L��C����U-Ba��1�Q+`	�=f�*� � j�� |�v��@��� ��)ғG.]��\�.2'N�x�����Ձ!/� 1J X hl��	�.C�s�� 3%�*`	�K`<A
Q���4 *��`��%��F79�!��Cҏ�{?t��HЮ�`y�<v7��x	:O�$�U��I�s$�@.��[�ׁ �{\�L���BB�䂘�.x�u�p�zf���k6&���}bP�b�C�6P�	sLp���&	6O��� X��v1*���z����8��/ ����܈�{�n����8X9�rц!P"\`���6J����A3���S f