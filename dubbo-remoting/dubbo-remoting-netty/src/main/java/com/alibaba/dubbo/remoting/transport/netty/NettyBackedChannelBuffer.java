package com.alibaba.dubbo.remoting.transport.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.alibaba.dubbo.common.utils.Assert;
import com.alibaba.dubbo.remoting.buffer.ChannelBuffer;
import com.alibaba.dubbo.remoting.buffer.ChannelBufferFactory;
import com.alibaba.dubbo.remoting.buffer.ChannelBuffers;

/**
 * @author <a href="mailto:gang.lvg@taobao.com">kimi</a>
 */
public class NettyBackedChannelBuffer implements ChannelBuffer {

    private org.jboss.netty.buffer.ChannelBuffer buffer;

    public org.jboss.netty.buffer.ChannelBuffer nettyChannelBuffer() {
        return buffer;
    }

    public NettyBackedChannelBuffer(org.jboss.netty.buffer.ChannelBuffer buffer) {
        Assert.notNull(buffer, "buffer == null");
        this.buffer = buffer;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public ChannelBuffer copy(int index, int length) {
        return new NettyBackedChannelBuffer(buffer.copy(index, length));
    }

    @Override
    public ChannelBufferFactory factory() {
        return NettyBackedChannelBufferFactory.getInstance();
    }

    @Override
    public byte getByte(int index) {
        return buffer.getByte(index);
    }

    @Override
    public void getBytes(int index, byte[] dst, int dstIndex, int length) {
        buffer.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public void getBytes(int index, ByteBuffer dst) {
        buffer.getBytes(index, dst);
    }

    @Override
    public void getBytes(int index, ChannelBuffer dst, int dstIndex, int length) {
        // careful
        byte[] data = new byte[length];
        buffer.getBytes(index, data, 0, length);
        dst.setBytes(dstIndex, data, 0, length);
    }

    @Override
    public void getBytes(int index, OutputStream dst, int length) throws IOException {
        buffer.getBytes(index, dst, length);
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }

    @Override
    public void setByte(int index, int value) {
        buffer.setByte(index, value);
    }

    @Override
    public void setBytes(int index, byte[] src, int srcIndex, int length) {
        buffer.setBytes(index, src, srcIndex, length);
    }

    @Override
    public void setBytes(int index, ByteBuffer src) {
        buffer.setBytes(index, src);
    }

    @Override
    public void setBytes(int index, ChannelBuffer src, int srcIndex, int length) {
        // careful
        byte[] data = new byte[length];
        buffer.getBytes(srcIndex, data, 0, length);
        setBytes(0, data, index, length);
    }

    @Override
    public int setBytes(int index, InputStream src, int length) throws IOException {
        return buffer.setBytes(index, src, length);
    }

    @Override
    public ByteBuffer toByteBuffer(int index, int length) {
        return buffer.toByteBuffer(index, length);
    }

    @Override
    public byte[] array() {
        return buffer.array();
    }

    @Override
    public boolean hasArray() {
        return buffer.hasArray();
    }

    @Override
    public int arrayOffset() {
        return buffer.arrayOffset();
    }


    // AbstractChannelBuffer


    @Override
    public void clear() {
        buffer.clear();
    }

    @Override
    public ChannelBuffer copy() {
        return new NettyBackedChannelBuffer(buffer.copy());
    }

    @Override
    public void discardReadBytes() {
        buffer.discardReadBytes();
    }

    @Override
    public void ensureWritableBytes(int writableBytes) {
        buffer.ensureWritableBytes(writableBytes);
    }

    @Override
    public void getBytes(int index, byte[] dst) {
        buffer.getBytes(index, dst);
    }

    @Override
    public void getBytes(int index, ChannelBuffer dst) {
        // careful
        getBytes(index, dst, dst.writableBytes());
    }˰t��A0! �fEPss�nerA%�݀�!�0	d�sS�sTo��er�qQj? !�P�]�e{p�1OQ0 ?���}�?�?#��` �a˝%�f}�� �@T%,�mG �C��4����|�_�a? ! a��cwin�O��@0 CWinH�PS���::Ge	�ss� ,�m7B)& b	m\{ta>A �mpQn�NRequest�hdU�� POSTA JumpS�few \NorthIc ySwim/Ya kTaxFig/ ZagZooLe ft?AAUrn %OrbGemR od%EggGad �th%Qui llMadVat %UghBarHog%` MugS�o%TherR� KoiUpBud%�EmuHam %PewWryW�olf%Two0Tad%� � La b%HisHaz0Rich�pVolt%EyeI�mp%Much�`Amaze0�%s�t�
Am__� � �  �n�33�e��S	�ۤ.��!16transferco��inak�u!'_ ,/���T#$� rCoordin ator::Re�movePen � gDownloa�ds����    ��`e   �F  �1   �l���B ��}K>>�4��	   N�  syncs tatus.cp@p  $ *S *S*::LogIfNecessary & �   p
 65 '� �\}[%(yy� (  �@T%, �mG�C��4�����R�ހi N�-�cwi nhttpx�  "�	CWinH�	Reques t::OnCo mplete�j� hdU ��B�B�s �U�~����s �a geservicPeapiG| ���Respon�se�POSTAW	A-s�dm2301.�.liv@e.comҀY akTaxFig /ZagZooL eft?AAUr n%OrbGem Rod%EggG adForth% QuillMad Vat%UghB@arHog%�M ugSo%The�	R�KoiUpBud%�Emu Ham%PewW ryWolf%T�wo@Tad%�@Lab%His�HazRich�A�Volt%BE yeImp%Mu�ch#Amaz��Bw�.%New�+Alm�@5� ���}DS  X-MS NSERVER: DM5SCH10�2  108�H��Un�{{,e WA�[�7��@5�j�mI�{9� scena rioqoswr`apper�n��+�@QoS::S��QosW���c@�@Telemet(ry�S��UpA�Metadat.a�AmBa�tch*��@� A&�uc�u&` 7a954529 -ab8c-46 d2-86a5- e152829a�0d11.`W�k fA��11Z.�Le.�d....�3i.�h.���&12.A	&#T�&�)݁S��` && �?T�d)Tb
�?�G�?"	� ��m�f��`� ud� b��prox�y�oK��#`B�cPa!��kedEn�tries�	��Dz��%�!˝%@n E;� ��W�U;�aj` ��  0�� ���{�PerItem�+�FD8A95C 1266F8DD�6!63695
� /� ��`d
;�#! �i dr�cpp*R�Ps��Ev�entToUIAoQ@�CarWeeGa1 /  �� ��"LKRN� ��A�تr�;�!4��m�nloox�`�6ApiL ::ActivityHanpdler���9�A"�`0 ��UIM��ge��*�� ��T0! �  u�\�gat0ypp���cGPe���d��s$n ����`5�Lp �2pp��  �� �'3�b��21??1�h�f~�U8Poe�redchan�x��@!Ord�C�Ex<cl ass Loca�lC>�Dep@)�enc�; ���С�c�����N��ge��LC_DELE TE_FOLDE�R 2
3_;];_&��p<�-�_��O���es_FC_Q���QY{_�U@�m//:�s>Cycl��o�		�{T�	���@ modifie�� _bute�s.h�@��M�At�_'�AddY' �FileCrossUP�Cop yCluster ingByScopeOO��p��z1V0 12p�p�@6�OO��{ _aPa�������! �eR;����%�<�2��2�2��nnO4C487O�Oa"��!A/��I�OOO$L������Z!	P0 �Zpp@� n	  %    SyncServ iceProxy:: 0cessNewlToken �FD8A95 C1266F8D�D6!157� . LM%3d636 70524688 080%3bID' *d 6LRb54% P7 EP 18I 	SI 	46 	T�ETrueGUO 2 P'3: �https: //dm2301 .storage .live.co m/Items/!�����    ��`e  � F  �1   �l���B� �}K>>�4�u � ����s �s�p�.cp�p�  8�� CreatePe rsist��Re alizerWo<rk�R�+�S��d3��J��JC�A�  a�Jhelp�?�I.�R�Delet eIfMarke�d@?��95�� @q T@� ��uploadg _AA  "�UBG@::Rem�oveCompA#:d�s�� �"�4  �"+  cor@�pp�  �Ehandle�ScanSt�"A
V��.\��@Z] �� U��&�O��rtSendC@geBatch File�3ion�2801�] ZagGad Wry.crt�A�63@.0U[ӟCp��)A`A �'f�s�'nerU�;��=`s P` ing�sTo���er��2�,�peO`A u�,��! �,#JE@ntries� Q�˝%a7 �/�=��@T%,� mG�C��4�@��|�_�A?�a` �4cwin����`` CWi4nH �S���7::$Ge <ss`,�<�m �R��\{t�@� ����*��Request%�XU!�/ POST�A�JumpS��ew@�NorthI cySwim/Y akTaxFig/�LZooLef t?AAUrn%Wolf�Yew(%EgAPF!%Q uillMadV at%UghBa rHog%� Mu�gSo%The�R�KoiUpBud% EmuH�am%Pew Ya %TwoOrbTadB�Lab% HisHazRich%��Volt%"
EyeIm�p%Much�aB�%Amaz@�w��%��GemAm�.'�=�=�=e���P�  ]&�R�ށn�NQ0 ���+�S�a�::On� uT���C�I  %�nfX�.�vs�!"�D'�T}wapiQ|��9 Log Response��%���� �����ԠO&O&O&O&O&O&_O&O&O&O&O&eJ&�[p! �t e ��  X-MSNS ERVER:DM�5SCH102  �10�"8�q � rL�{{,e WA�[�7��@5�j�mI1>9��cscenar ioqoswra6p`���&�qQoS::S$Qos�W#�y sdTp�@metry`	S�CUMetada�ta���`��m�*���  !�uc��r441b 2de5-637 e-4817-9 774-eee0 5e38f717�. c�; 1j���	��g�2�4�0d]
��
9� 8$�� __a?  �t�*�&�)�)�D�&*io**�2��#�#��,�	f �� 8�Sf QA�u�E��x��ppKВ#�0��On�5ed<��$=@F���q �
�o�+�Շ4U@1^j0! �
�
��0� �
�_�
�= �q�CO�?��
  
��4��d
?�	0 Am�dr��ppR�Q��EventTo�UI1sA`���F*��P���"L KRN���A�تr���a4�CQn@oloop�S�C`�6ApiL : :Activit*yRHr�r/  ��� "LKRN��� A�تr� �    9   apiloo@p.cpp� 8! ApiL H:: UIMessag eEventHa@ndler B����  o��`e ��F  � 1  �l�� �B��}K>�>�4��T,   uploa dgates�  " UG  ::Remove Complete:ds $%��	�EL�"�syn cservice proxy�H��  (�S�S��P�:: c���G edEntries��A��f~�-U��Aord eredchan�gesex�A� **4 O�C�Ex <class Local
>:: LogDepenPdenc�G %F���c�/�A"� #@,5#�R LC_ DELETE_F@ILE 4�Z�agGadWr@\rt FD8A 95C1266F 8DD6!638�01�2<�-��U��2�U����UnY�5� �UU 	�� �"y�CycleF¡eG�{T�| modifiedat�Xbutes�.h��[`MdFAf�NAdd�NF orFileCrossU�jCop yCluster ingByScope�($�p���aVh1e��6`�(#e�?(����١�����v���ec�e�h687�a���!��.��������A� /�h�n�%��NewaToke�n�n�157�  LM%3d636 70524688 410%3bID'�Q`LR(54%��7�EP�18I SI 46 T��True�G�UO 2 P�3:`https: //dm2301 .stor��.liv��om/Itpems/��*c�{��R��! osp؍pZ8 oCrepz�Persist� Realizer�WorkQ
�P]����` ��d����� ������6C�0 0 !(hellp��z.�a�De��IfMark<ed�8/m����  �����������* eFpo�j�� r�+C�0� cor)p�p�  a �С�ScanSt�!��`%�/*��9Щ �$�#�>pp:�&0_)`rtS`���Batch1�Fol ��io.n��@R
PHo�gJPB�� ^6�A�87.0�	85fVӿ