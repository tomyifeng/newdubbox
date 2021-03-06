/*
 * Copyright 1999-2012 Alibaba Group.
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
package com.alibaba.dubbo.rpc.cluster.router.condition;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;

/**
 * ConditionRouter
 * 
 * @author william.liangf
 */
public class ConditionRouter implements Router, Comparable<Router> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionRouter.class);

    private final URL url;
    
    private final int priority;

    private final boolean force;

    private final Map<String, MatchPair> whenCondition;
    
    private final Map<String, MatchPair> thenCondition;

    public ConditionRouter(URL url) {
        this.url = url;
        this.priority = url.getParameter(Constants.PRIORITY_KEY, 0);
        this.force = url.getParameter(Constants.FORCE_KEY, false);
        try {
            String rule = url.getParameterAndDecoded(Constants.RULE_KEY);
            if (rule == null || rule.trim().length() == 0) {
                throw new IllegalArgumentException("Illegal route rule!");
            }
            rule = rule.replace("consumer.", "").replace("provider.", "");
            int i = rule.indexOf("=>");
            String whenRule = i < 0 ? null : rule.substring(0, i).trim();
            String thenRule = i < 0 ? rule.trim() : rule.substring(i + 2).trim();
            Map<String, MatchPair> when = StringUtils.isBlank(whenRule) || "true".equals(whenRule) ? new HashMap<String, MatchPair>() : parseRule(whenRule);
            Map<String, MatchPair> then = StringUtils.isBlank(thenRule) || "false".equals(thenRule) ? null : parseRule(thenRule);
            // NOTE: When条件是允许为空的，外部业务来保证类似的约束条件
            this.whenCondition = when;
            this.thenCondition = then;
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation)
            throws RpcException {
        if (invokers == null || invokers.size() == 0) {
            return invokers;
        }
        try {
            if (! matchWhen(url)) {
                return invokers;
            }
            List<Invoker<T>> result = new ArrayList<Invoker<T>>();
            if (thenCondition == null) {
            	logger.warn("The current consumer in the service blacklist. consumer: " + NetUtils.getLocalHost() + ", service: " + url.getServiceKey());
                return result;
            }
            for (Invoker<T> invoker : invokers) {
                if (matchThen(invoker.getUrl(), url)) {
                    result.add(invoker);
                }
            }
            if (result.size() > 0) {
                returMAM*H  ���������������������������������������������������ʚ�ʩ�˼���������Ȫ�����������˺������������������ɪ������ʪ���������˪�ʫ            �y �     ��   ���    ����    ���� ������������� ����x ������  ������  ���� ����
    � �{    �        ��%�kv�����#�=R]$*��a�
Sa����$�.�����O>΄)\�Dd��;2&"<o/f����u����.��.�����'��s�{b��}��3���)��]��5m�>�'ox��L���b��.'��%}`� v�6�DJ/������now�49E�׎N����jǔ�y��v!g�L�����2���]0��>;;��1��x�������Tݸ�jS{���G��BM���i����i��|��:�t2��洺�\Pܒ6���[t᪀j{s�M[�u|QH��|�ʻ@��o0K�FG�ڈ&>�J�79��Q���>
B�Z�����|�F���=C�5�ԩ�����/��>��w���������%�����t�OtT����.ȁO���*Q�
�1R0��~t��ez���@eu��
|�7:2��L8�|����� �6���ZP�S|��c����_J����+�2  ��?7��7/:�� ���6 ]tiV�U��I )���%-��otB��h��&x�|�ϟ�|��󉏀-~b�%�@W������
|��7��1f�|=Cԛ#`����|�z
�����\jJ=Q2������,_��сӧ������?��ǁ�N9��g; in�k�F�$�iNt�V�}=(f���$5Z���J4��������L�A(���&	2M�p���Pp� �@\�����@`^b @f��PJ%�,Ap	Ld�@�A'	<7� ��{48j-/� 13 PJ%�,Ap	Ld�@�A'	<7� ͼ���*�a�.�X�LA(����%	0M�l��'��� �9��"#��/40��P*��	�Mxn� 
�P(�ʀaaP�P��	*N�ȁB)�^�0	5�T�n� n��S�Ѐ�� �Yp)�^`��&�2�	Nx� @��i� ��*�`	8��F�@`^b @f��PJ%�,Ap	Ld?�x���zԺ�1�cݞw��y|��z> >�-�0rz�=@/�H��1z˪�����[��\/� 13 @6%�.��	�Lh�`��'>����.E䀁9�t� ��h� <Ag�k[�q���F0���rv�P�Z�Z��q�0q�S�C���|�t����p����">����GE�8*��Q"J壊���*��9Q��GE�J:*��Q��ŎTD�v�"�8;*��Q��s��(}��>jX�E��m�����@XSL�|�TV4X����-b�Ћfj�@	6%�.��	Mp����*�`�`� 	dL`d@�'�<A�	7( �@AD���-E�x���%�� �������z����Q�CEE@Q*�02��<Դ���bj(`-���#�����4�"DW{ě0�����h��_��.�0� ���p�"D2��Ё.��p@3����<�m�tk0����SH�a�(zX�Q⩍Q�\�R�U�d�ԁ>�6�$��A4����~�d�R$x�A�ŃRtX{i�5���"@�� �.�	�Lt��C���%�<A�	7 ��@�	f~0B!�u�O(�B���;������A���V�P�fC��@/��8b0��A�プ2t8@�5�Ƴ�j�n��t� ��r��F{c� ����=��t���p���3I@�� %ؖ�JA��Td`��0Ae�@����	0u�����1�a�x�t�A�AX��h��A�ze��x'h> 
@EXH�`X�I�P�k(��9�t6�#n�/��@胰��l�����d�Ї�l��r�n������8ta� 9bb� &�+@Rc�A�%��  �HU��Z����Y��<���.�B1�Co��2�Cz2��@*���3ȵ�Qۤ4P�m9@��:�e��`�`VNs��1���á2�;�j�.<P]h3�	LM�p��� �(�^E��W�5p������`	jK@d`�'����P� ��k@�4���f��	jLPh`�'��� �@QIۊ|&�$CEU*Q*��%DE����Q*�"DiNT��ꆢ"*���`� �JP\ �&�8��	�O�p  �1T`!�"p�� �̀	&6@��%	0M�l�����m૰�`�ءQ��1O�X: v�M`8����_��hP1��2x�@*�l	 ����7�]@{�����֐��/���Hw�݀�-C^`~�xA$�41�����(�����cd0`h��`Ld��pP�� @6֡�^` bf@��6P��%	.L�h�`�'�>�����(X��\`� dh`0	5J`l���Pp� �@��,.�X�����%	.f����;�x@����&n`�#� ?`>H9Pm������.��`�������A�3I��� %�p ���O�|4�@ A[���8�r��@��������A'T����`H肳�7�o���ā��>��[Dm�"��*�J.�"��>*���Q���Q�����1Ш�\��=�LTu�"ʱ�.V Vt|$���3�����b��7�o�����X� с������KE� ��" ��^9������ �a���9�V0P2I0��xn�~˱5�$?77馆�\��w � �A�\���s����[6ޯ��c�޺!����y���_�,��1A��D9�e`�,�H 3���o��07]0`^[�8������$@8�v��K��q���Z\�AEA=(������v��@�
��R�Tȃ�`�*z4�1����He0�AH��Af���x��	� �-8���XŬ~� ��FB�>H>8~ >����T��� ��@c��C)6�,�Y=�<�x ����GF�h���1�A[��q�o�� �8�1�A����b�Љ7= P`��Qt�2J`X��Mm�Qi�.X&j/|"*x�Q�����GE�4>*�j�E�LG*��:Q��GE�d>*���E�~G*�%<Q(㏊D%)T��@X�qW���2� ��7��G�뎏�<��9�s�� ����
S����p��/Bo��")�E�A�I�(+'�D��"xpp  +��;�w�Q��DA8A|�?�j@`�-OT�Oz"4h�/V VPjL>"2�;@vAѐ�L]P	�r��@}�fz��ƻ�z<�x0рYa�A���6��0�6��=�xk(j#�L��YE�a0��q��(`��5�F� �^���T�ی)f�c��ұX�T�QR��	
��EH�U�R��\/� 250`�K�TY�A�Md�:he�|h�L(A��&��@ �Z@~�`L=QAT�D�~Q�&U~��3Q��TDe|Q�(��T��O
��U�B�ӣ��
ؔsΘ�4!N�.Nw=�o��p��)�%)/}���I/Nt7��Kw�����8&ƒ��	�K���x�I'��k�0�Xx�D�0	uI<?o/�TI��J�-VYFY�u��X��c�A�`H2���(�ٹab��j�����r��V���H�ibZ������Y퇘X�+$ d�S����`La:h᪫#؀�mat��#�h
��e~>�a�b0a����F!�	�Tu�zv!�
`ĢD�a$�ZiP(do���Z�oL^��]�~uf=��٪*)`̰"2�E�`!��a����d�%�k�^���`�~IBs���d�ו枰C�e RXĿ��au]�d����s=�abFƬ�k��@�c�a=�`��VM�d��X�a�(�,$�Ah^`��T���d�@��/e$I���7�`H��k��6�bɯ����A��a[�Ob�ܟ�҇�0w<���]'�/�2�D������hq�!aG��/#��I��7ʯ������qG�?��C�c�1�q�g��%v�GK�7���d��D�3ܕ���J���H��92n�u>1��D#A"�q}���"?�D u��@�"ؑ3�lC�@"����}"�A�'�B�@�"��"��D�< �Ȅf@"���k���2R�&�痩CXGɺ{��B�PB)=�XBL�IZ&�#�`���������П                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       