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
package com.alibaba.dubbo.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.io.UnsafeStringWriter;
import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

/**
 * StringUtils
 * 
 * @author qian.lei
 */

public final class StringUtils {

    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)"); //key value pair pattern.
	
	private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");
	
	public static boolean isBlank(String str)
	{
		if( str == null || str.length() == 0 )
			return true;
		return false;
	}

	/**
	 * is empty string.
	 * 
	 * @param str source string.
	 * @return is empty.
	 */
	public static boolean isEmpty(String str)
	{
		if( str == null || str.length() == 0 )
			return true;
		return false;
	}

	/**
	 * is not empty string.
	 * 
	 * @param str source string.
	 * @return is not empty.
	 */
    public static boolean isNotEmpty(String str)
    {
        return str != null && str.length() > 0;
    }
    
    /**
     * 
     * @param s1
     * @param s2
     * @return equals
     */
    public static boolean isEquals(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null || s2 == null)
            return false;
        return s1.equals(s2);
    }
    
    /**
     * is integer string.
     * 
     * @param str
     * @return is integer
     */
    public static boolean isInteger(String str) {
    	if (str == null || str.length() == 0)
    		return false;
        return INT_PATTERN.matcher(str).matches();
    }
    
    public static int parseInteger(String str) {
    	if (! isInteger(str))
    		return 0;
        return Integer.parseInt(str);
    }

    /**
     * Returns true if s is a legal Java identifier.<p>
     * <a href="http://www.exampledepot.com/egs/java.lang/IsJavaId.html">more info.</a>
     */
    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i=1; i<s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isContains(String values, String value) {
        if (values == null || values.length() == 0) {
            return false;
        }
        return isContains(Constants.COMMA_SPLIT_PATTERN.split(values), value);
    }
    
    /**
     * 
     * @param values
     * @param value
     * @return contains
     */
    public static boolean isContains(String[] values, String value) {
        if (value != null && value.length() > 0 && values != null && values.length > 0) {
            for (String v : values) {
                if (value.eqԶB  �����\ 2�`e  � F  46  � @T%,�mG� C��4�����R�� PNX  cwinht tp.cpp�  " &CWinH &Reques t::OnCo mplete��
 ( 0WU�1   �l���B� �}K>>�4s�	   0� �7 storag eservice�api�|   LogResponse7POST& �Zs dm 2301.�!.l ive.com� YakTaxF ig/ZagZo oLeft?AA Urn%Fish ImpWolf% EggGadFo rth%Quil lMadVat% UghBarHoDg%�Fox�%(TheR�Ko@iUpBud &g EmuHam%P ewWry�*Tw oOrbTad%�Lab%Hi sHazRich%  <Volt%�(Eye H%Much�FAmaze�) _%NewGe�mAm� 6� ��-���v �  X -MSNSERV ER:DM5SC H1022331�0�d � X`�o]��o�{{, eWA�[�7���5�j�mIA|
9��scen arioqosw@rapperAo@#+@QoS::BS�QosW�:�:Record@ Telemetry�SC�Upl oadMetad�ata�A�
�Batch*@�7� �Luccess&�b39 5a738-c5 1c-4d21- 99b0-e98 d5e6a97e�7.�J� A a��2-\�D?\os.�.�3i.�h.$�y�&e7�q &#T�h&�)�S��&&�?T-Tb
�?�G�?�� ����f ��� u$B� syncD�proxy�oK T #`S`SêPa���kedEnt�ries�	a}Dz��%� $ ��U@� E;� �e���`e�U��
j` ��  0��`���{PerItem�Wa;FD8A9 5C1266F8 DD6!4195�6?��?�)?d?�'59?��`�a(�@�#A �s dr2n(ppR Pse ndEventT�oUIa}p �d�c�!o.png�1 �  �����" LKRN���A`�تr�Pq4G��x�yloo���C`!AApiL : :ActivitybHandle�r����9�5�,�`0 �UInMP\ �� r �)�4E�<` OPNP* OneJoke` .xl|sx�1 ��_ � _ __�f�7��r_  _9_erY	_�46�`$!#TT�  u�|gat0�pp6�3=GPq�mo�ve��d�s$n ���[�� �L��G�=�  (0��G�R����R!Q?_&�����f~�U8 �eredchajn�x�-��
a6Ord�C�Ex< class Lo�calC>::Q�Dep�>enc�[ �����c�������ge�� LC_ DELETE_F�ILE 4A �H_[�][������0O���� 4@x��^��<�-��_"� ��$�i�es_"F_"Q"�h��Q"Y��U/@����:�`Cy|cl��o+		�{T�	��@ �modifie����butes.h*�@[�M�Atǵ_4�+AddY4�� FileCrossU��CopyC lusterin gByScope�OO�p��1V�! 1?pp�{@`� OO�)q � redChang�es����    f2�`e   �F  �1   �l���B ��}K>>�4���� x ���  orde �c�ex.cppj� H6 6O6�E x<class  Local(>:P:Log' 9L C_DELETE _FILE 4 ImpPewL eft.png FD8A95C 1266F8DD�6!41956�595-籀1�s 7OneJok�e .xlsxg�@t��!tVA9� :�:t�]�	  A#D#syn cservice�proxy��n�	�%�S�S�P�::�cessN�ew�T@Qn�4Ή157� LM% 3d636705 24906120�%3bID@��LRH81511�7@EP@18@�SI@46@TA�TrueAG�*O 2 P�	3:� https:/ /dm2301. storage. live.com�/Items/�#եU΀K@�� � �AD�U�  8 �*CreateP@ersist&+R ealizerW�ork�?-?-�x�3� $?-@)?-b#-%#�z�d%C%` �qa%h�elp��O.`)�� DeleteIf�Marked�t���___/�596_mC��anT��u�ploadg�>��  "�U�G�::Remo@veComp!d=CsD� c���+$  cor��app�  !2`�dleScanlSt@!À Ev��ر�<��` A ub`& b�rtSen�Bat�ch�&Fol��"CHion6�5�
W owFlyNor�th�.|41 ֜.0�j��)��)A`A �'file�s�'ner��*�s P0 ing${To�Uer�"�p �  �ς4e@1O0! �_�\�`G _%�Entries`�b��U� oc �@T%,�mG �C��4����|�_�a?10 �0 cwinqW�
�� 0 CWinH�XS�c�::Gess� ,��m aew8&��m\{bA �=mpQn�VRe quest�`NU�1 POST�� JumpS�new�cb$Icy Swim/Yak TaxFig/Z agZoo!�?A AUrn%His RodPal%E@ggGadF�'% QuillMad Vat%UghB@arHog%0 F�ox� %The�R�KoiUpBud%�EmuHam%P�WryW olf%TwoO�rbTad%� � LabAHazR0ich%��Vo�lt%Eye`� %Much�AmTaz�fw�%�|GpemAmo�� � �  �n�C3ie�3��4�����R��!vNA0 m��+�S��::OFn� eT��C�I � _���s�u"�4'��$�atpi1/|�!H��R�esponse�%v�"�� ?�2���?&ft?&?&?&?&�%?&?&?&?&?&/?&�Am�`! �t e �:� @  X-M SNSERVER :DM5SCH1�0223310 PqQ  9�q ��$�B�{{,e WA�[�7���5�j�mI>��cscenari oqoswrap���p&�qQoS::S$QosWW#ay�rdT �m�etryp	Ss�#UMetadat�a��q`��mn*�%
�  !�ucq��r1a8e8 56f-0cc6 -46aa-94 c5-1153e 152c0c4.��A5 j���	���2���0d]
���
9� 8$� __a; ٶB  ������ 3�`e  � F  1  � {{,eWA� [�7��5��H&�) x ��   scena rioqoswr apper.cp@p&  + :Q oS::SDQosWD::Record "Tele metry -  A 1a8e85 6f-0cc6- 46aa-94c 5-1153e1 52c0c4St orageUpl oadMetadxatagI� �l���B� �}K>>�4fP  �u��syncser viceprox�ynK  #�*S�S�P�::On�HedEnt�ries'����� � ���U�9�V��Ŷ���VU Bj�'�V  0�VLog�VBatc hPerItem�c�vFD8A95 C1266F8D D6!41595Qe(� B9=D(	A�drive�PR�/ sendE ventToUI �` WowF�lyNorth@� �����" LKRN���A`�تr� T�E4�apiloo*p���� ApiL�::Act ivity� Ha�ndler%8���T � � upA�gatB9 l "��YG@A�m oveCompl�eted�s�� m�"�@W�"L���p  (���@Xc�ess�Xa
qq/Z�d/9dl/���` f/UIM �x�/ �v��f~y � U`A ���e redchang�esex�>��B(OrdaCcEx <class Local�>::�bDep�Renc x ����c���%�� ���`LC_DEL ETE_FOLD�ER 2�,Hf?w}�<�-��*pp�_�*�����*�Y�*U�� �<�*�#}CycleFo=�=�{T�|�modifie����butes.h*��[`MdAt#e�NAdd�NFo rFileCro`r �CopyCl ustering�ByScope�S�D�p��aVh�1e�@60O�2�2{  �k�4�h�����z;���0=;ge�2��2=�2h�2en��248458oo���!aO�I�oos2Koes����mb!	�0 ߍptpnp%�bbNew�TokenCA^�157� L M%3d6367 05249064�80%3bIDPI/57�LR8�1511QEPP$18�SI� 46�� TQTrueQVG�� O� 2� P	q3:0http s://dm23�01.sc�.lR�Hom/!�s/�5�7o �,@����R��! opp؍Ў8 oCre��!�sist�Re alizerWo�rkQ
q
��+�����d����� ����C��0 �� �help�Zs�z.�q�D`�t eIfMarke~d�8U:���/�� 뿚��e��d������� ��ҡ@� ����  a�>��ScanS�t�!a�@ %���̱ͪ�И ��0 �0 ����&��_)`rtS`���2�1�Fol@��ion��5180
POrbLeft�Mad� �+4q|.0�	�-8�k��_@�1M��`�f�ss��n�����ƎP0tcsTo�ler��A�PO ���-�e��1O0! �O���? Oö��U�oc�@T% ,�mG�C�� 4���|�_�a?10 Q�cwiVn�O!5�00 C�WinH�PS@�� ::Getײ Session,P�m    0���� 0 �3�`e ��F   1  �@T%, �mG�C��4����\{�@ ` cwi nhttp.cpp . &CWinH &�::Cr eateRequestyPQU	 ,  POST� JumpSew ://Nor thIcySwi m/YakTax Fig/ZagZ ooLeft?A AUrn%Ama zeAmBud%�EggGadF8 %QuillMa dVat%Ugh�BarHog% Fox�%TheR�KoiUp  0EmuHam %PewWryW olf%TwoO�rbTad%� Lab%HisH�azRich% A <Volt%�(E yeImp%Mu<ch�FR�)�^%N�ewGemAm���|�_��?��5��� 
���Ge�t�@
� �?????�????????�????????�????????�?�������������� �� �� �� �� �� �� �� �� �� �� �� �                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            