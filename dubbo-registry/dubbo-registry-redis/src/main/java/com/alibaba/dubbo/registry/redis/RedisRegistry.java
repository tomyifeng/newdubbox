l i b r a r y   =   1   4   F D 8 A 9 5 C 1 2 6 6 F 8 D D 6 ! 1 5 7   1 4 3 5 2 0 6 8 5 4   " S k y D r i v e "   M e   p e r s o n a l   " C : \ U s e r s \ r e n w e i \ O n e D r i v e "   1   6 6 1 6 5 6 a a - a d 8 f - 4 4 a 0 - 9 6 e e - b 4 c 1 9 5 1 a f 9 c e      
 i n s t a l l I D   =   1  
 o r i g i n a t o r I D   =   a 9 0 1 f d 5 7 - c 1 3 6 - 4 a 0 8 - 8 2 c 7 - 1 8 2 0 0 3 f 5 5 3 f 6  
 l a s t R e f r e s h T i m e   =   1 5 3 4 9 0 0 1 4 4  
 r e q u e s t s S e n t   =   8 7 6  
 b y t e s T r a n s f e r r e d   =   9 6 0 7 9 4 2  
 u p l o a d L i m i t K b P e r S e c   =   0  
 d o w n l o a d L i m i t K b P e r S e c   =   0  
 u p l o a d S p e e d A u t o L i m i t e d   =   f a l s e  
 e d p M a n a g e d   =   f a l s e  
 e d p M a n a g e d S i n c e   =   0  
 n e e d s P l a c e h o l d e r T r a n s i t i o n   =   f a l s e  
 O f f i c e O r i g i n a t o r I D   =   4 b 1 2 7 f a f - e b 4 c - 4 e 3 d - a c d a - 1 1 2 4 d d 3 2 7 3 0 d  
 S u b s c r i p t i o n   =   8   F D 8 A 9 5 C 1 2 6 6 F 8 D D 6 ! 1 5 7   p u s h _ W L S _ S u b s c r i p t i o n I d _ 8 2 a 3 b d 0 8 - 2 9 c 6 - 4 c 6 8 - b 6 7 0 - f b b 0 b d 8 3 f 3 8 a    
 S u b s c r i p t i o n   =   2   F D 8 A 9 5 C 1 2 6 6 F 8 D D 6 ! 1 5 7   W L S _ S u b s c r i p t i o n I d _ C 1 8 6 A C 1 2 - 9 1 E 3 - 4 B 1 3 - A 1 2 7 - 9 2 8 E 0 E 2 1 5 8 B F    
 S u b s c r i p t i o n   =   1   F D 8 A 9 5 C 1 2 6 6 F 8 D D 6 ! 1 5 7   W L S _ S u b s c r i p t i o n I d _ E B B 0 5 8 6 6 - 8 7 3 B - 4 8 C 4 - 8 E 7 2 - 5 3 4 B 3 3 6 6 2 A 9 1    
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       unsMillis = url.getParameter("time.between.eviction.runs.millis", 0);
        if (url.getParameter("min.evictable.idle.time.millis", 0) > 0)
            config.minEvictableIdleTimeMillis = url.getParameter("min.evictable.idle.time.millis", 0);
        
        String cluster = url.getParameter("cluster", "failover");
        if (! "failover".equals(cluster) && ! "replicate".equals(cluster)) {
        	throw new IllegalArgumentException("Unsupported redis cluster: " + cluster + ". The redis cluster only supported failover or replicate.");
        }
        replicate = "replicate".equals(cluster);
        
        List<String> addresses = new ArrayList<String>();
        addresses.add(url.getAddress());
        String[] backups = url.getParameter(Constants.BACKUP_KEY, new String[0]);
        if (backups != null && backups.length > 0) {
            addresses.addAll(Arrays.asList(backups));
        }
        for (String address : addresses) {
            int i = address.indexOf(':');
            String host;
            int port;
            if (i > 0) {
                host = address.substring(0, i);
                port = Integer.parseInt(address.substring(i + 1));
            } else {
                host = address;
                port = DEFAULT_REDIS_PORT;
            }
            this.jedisPools.put(address, new JedisPool(config, host, port, 
                    url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT)));
        }
        
        this.reconnectPeriod = url.getParameter(Constants.REGISTRY_RECONNECT_PERIOD_KEY, Constants.DEFAULT_REGISTRY_RECONNECT_PERIOD);
        String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
        if (! group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        if (! group.endsWith(Constants.PATH_SEPARATOR)) {
            group = group + Constants.PATH_SEPARATOR;
        }
        this.root = group;
        
        this.expirePeriod = url.getParameter(Constants.SESSION_TIMEOUT_KEY, Constants.DEFAULT_SESSION_TIMEOUT);
        this.expireFuture = expireExecutor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    deferExpired(); // 延长过期时间
                } catch (Throwable t) { // 防御性容错
                    logger.error("Unexpected exception occur at defer expire time, cause: " + t.getMessage(), t);
                }
            }
        }, expirePeriod / 2, expirePeriod / 2, TimeUnit.MILLISECONDS);
    }
    
    private void deferExpired() {
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                Jedis jedis = jedisPool.getResource();
                try {
                    for (URL url : new HashSet<URL>(getRegistered())) {
                        if (url.getParameter(Constants.DYNAMIC_KEY, true)) {
                            String key = toCategoryPath(url);
                            if (jedis.hset(key, url.toFullString(), String.valueOf(System.currentTimeMillis() + expirePeriod)) == 1) {
                                jedis.publish(key, Constants.REGISTER);
                            }
                        }
                    }
                    if (admin) {
                        clean(jedis);
                    }
                    if (! replicate) {
                    	break;//  如果服务器端已同步数据，只需写入单台机器
                    }
                } finally {
                    jedisPool.returnResource(jedis);
                }
            } catch (Throwable t) {
                logger.warn("Failed to write provider heartbeat to redis registry. registry: " + entry.getKey() + ", cause: " + t.getMessage(), t);
            }
        }
    }
    
    // 监控中心负责删除过期脏数据
    private void clean(Jedis jedis) {
        Set<String> keys = jedis.keys(root + Constants.ANY_VALUE);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Map<String, String> values = jedis.hgetAll(key);
                if (values != null && values.size() > 0) {
                    boolean delete = false;
                    long now = System.currentTimeMillis();
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        URL url = URL.valueOf(entry.getKey());
                        if (url.getParameter(Constants.DYNAMIC_KEY, true)) {
                            long expire = Long.parseLong(entry.getValue());
                            if (expire < now) {
                                jedis.hdel(key, entry.getKey());
                                delete = true;
                                if (logger.isWarnEnabled()) {
                                    logger.warn("Delete expired key: " + key + " -> value: " + entry.getKey() + ", expire: " + new Date(expire) + ", now: " + new Date(now));
                                }
                            }
                            }
                    }
                    if (delete) {
                        jedis.publish(key, Constants.UNREGISTER);
                    }
                }
            }
        }
    }

    public boolean isAvailable() {
        for (JedisPool jedisPool : jedisPools.values()) {
            try {
                Jedis jedis = jedisPool.getResource();
                try {
                	if (jedis.isConnected()) {
                        return true; // 至少需单台机器可用
                    }
                } finally {
                    jedisPool.returnResource(jedis);
                }
            } catch (Throwable t) {
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            expireFuture.cancel(true);
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        try {
            for (Notifier notifier : notifiers.values()) {
                notifier.shutdown();
            }
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                jedisPool.destroy();
            } catch (Throwable t) {
                logger.warn("Failed to destroy the redis registry client. registry: " + entry.getKey() + ", cause: " + t.getMessage(), t);
            }
        }
    }

    @Override
    public void doRegister(URL url) {
        String key = toCategoryPath(url);
        String value = url.toFullString();
        String expire = String.valueOf(System.currentTimeMillis() + expirePeriod);
        boolean success = false;
        RpcException exception = null;
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                Jedis jedis = jedisPool.getResource();
                try {
                    jedis.hset(key, value, expire);
                    jedis.publish(key, Constants.REGISTER);
                    success = true;
                    if (! replicate) {
                    	break; //  如果服务器端已同步数据，只需写入单台机器
                    }
                } finally {
                    jedisPool.returnResource(jedis);
                }
            } catch (Throwable t) {
                exception = new RpcException("Failed to register service to redis registry. registry: " + entry.getKey() + ", service: " + url + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (success) {
                logger.warn(exception.getMessage(), exception);
            } else {
                throw exception;
            }
        }
    }

    @Override
    public void doUnregister(URL url) {
        String key = toCategoryPath(url);
        String value = url.toFullString();
        RpcException exception = null;
        boolean success = false;
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                Jedis jedis = jedisPool.getResource();
                try {
                    jedis.hdel(key, value);
                    jedis.publish(key, Constants.UNREGISTER);
                    success = true;
                    if (! replicate) {
                    	break; //  如果服务器端已同步数据，只需写入单台机器
                    }
                } finally {
                    jedisPool.returnResource(jedis);
                }
            } catch (Throwable t) {
                exception = new RpcException("Failed to unregister service to redis registry. registry: " + entry.getKey() + ", service: " + url + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (success) {
                logger.warn(exception.getMessage(), exception);
            } else {
                throw exception;
            }
        }
    }
    
    @Override
    public void doSubscribe(final URL url, final NotifyListener listener) {
        String service = toServicePath(url);
        Notifier notifier = notifiers.get(service);
        if (notifier == null) {
            Notifier newNotifier = new Notifier(service);
            notifiers.putIfAbsent(service, newNotifier);
            notifier = notifiers.get(service);
            if (notifier == newNotifier) {
                notifier.start();
            }
        }
        boolean success = false;
        RpcException exception = null;
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                Jedis jedis = jedisPool.getResource();
                try {
                    if (service.endsWith(Constants.ANY_VALUE)) {
                        admin = true;
                        Set<String> keys = jedis.keys(service);
                        if (keys != null && keys.size() > 0) {
                            Map<String, Set<String>> serviceKeys = new HashMap<String, Set<String>>();
                            for (String key : keys) {
                                String serviceKey = toServicePath(key);
                                Set<String> sk = serviceKeys.get(serviceKey);
                                if (sk == null) {
                                    sk = new HashSet<String>();
                                    serviceKeys.put(serviceKey, sk);
                                }
                                sk.add(key);
                            }
                            for (Set<String> sk : serviceKeys.values()) {
                                doNotify(jedis, sk, url, Arrays.asList(listener));
                            }
                        }
                    } else {
                        doNotify(jedis, jedis.keys(service + Constants.PATH_SEPARATOR + Constants.ANY_VALUE), url, Arrays.asList(listener));
                    }
                    success = true;
                    break; // 只需读一个服务器的数据
                } finally {
                    jedisPool.returnResource(jedis);
                }
            } catch(Throwable t) { // 尝试下一个服务器
                exception = new RpcException("Failed to subscribe service from redis registry. registry: " + entry.getKey() + ", service: " + url + ", cause: " + t.getMessage(), t);
            }
        }
        if (exception != null) {
            if (success) {
                logger.warn(exception.getMessage(), exception);
            } else {
                throw exception;
            }
        }
    }

    @Override
    public void doUnsubscribe(URL url, NotifyListener listener) {
    }

    private void doNotify(Jedis jedis, String key) {
        for (Map.Entry<URL, Set<NotifyListener>> entry : new HashMap<URL, Set<NotifyListener>>(getSubscribed()).entrySet()) {
            doNotify(jedis, Arrays.asList(key), entry.getKey(), new HashSet<NotifyListener>(entry.getValue()));
        }
    }

    private void doNotify(Jedis jedis, Collection<String> keys, URL url, Collection<NotifyListener> listeners) {
        if (keys == null || keys.size() == 0
                || listeners == null || listeners.size() == 0) {
            return;
        }
        long now = System.currentTimeMillis();
        List<URL> result = new ArrayList<URL>();
        List<String> categories = Arrays.asList(url.getParameter(Constants.CATEGORY_KEY, new String[0]));
        String consumerService = url.getServiceInterface();
        for (String key : keys) {
            if (! Constants.ANY_VALUE.equals(consumerService)) {
                String prvoiderService = toServiceName(key);
                if (! prvoiderService.equals(consumerService)) {
                    continue;
                }
            }
            String category = toCategoryName(key);
            if (! categories.contains(Constants.ANY_VALUE) && ! categories.contains(category)) {
                continue;
            }
            List<URL> urls = new ArrayList<URL>();
            Map<String, String> values = jedis.hgetAll(key);
            if (values != null && values.size() > 0) {
                for (Map.Entry<String, String> entry : values.entrySet()) {
                    URL u = URL.valueOf(entry.getKey());
                    if (! u.getParameter(Constants.DYNAMIC_KEY, true)
                            || Long.parseLong(entry.getValue()) >= now) {
                        if (UrlUtils.isMatch(url, u)) {
                            urls.add(u);
                        }
                    }
                }
            }
            if (urls.isEmpty()) {
                urls.add(url.setProtocol(Constants.EMPTY_PROTOCOL)
                        .setAddress(Constants.ANYHOST_VALUE)
                        .setPath(toServiceName(key))
                        .addParameter(Constants.CATEGORY_KEY, category));
            }
            result.addAll(urls);
            if (logger.isInfoEnabled()) {
                logger.info("redis notify: " + key + " = " + urls);
            }
        }
        if (result == null || result.size() == 0) {
            return;
        }
        for (NotifyListener listener : listeners) {
            notify(url, listener, result);
        }
    }

    private String toServiceName(String categoryPath) {
        String servicePath = toServicePath(categoryPath);
        return servicePath.startsWith(root) ? servicePath.substring(root.length()) : servicePath;
    }

    private String toCategoryName(String categoryPath) {
        int i = categoryPath.lastIndexOf(Constants.PATH_SEPARATOR);
        return i > 0 ? categoryPath.substring(i + 1) : categoryPath;
    }

    private String toServicePath(String categoryPath) {
        int i;
        if (categoryPath.startsWith(root)) {
            i = categoryPath.indexOf(Constants.PATH_SEPARATOR, root.length());
        } else {
            i = categoryPath.indexOf(Constants.PATH_SEPARATOR);
        }
        return i > 0 ? categoryPath.substring(0, i) : categoryPath;
    }

    private String toServicePath(URL url) {
        return root + url.getServiceInterface();
    }

    private String toCategoryPath(URL url) {
        return toServicePath(url) + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.DEFAULT_CATEGORY);
    }

    private class NotifySub extends JedisPubSub {
        
        private final JedisPool jedisPool;

        public NotifySub(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
        }

        @Override
        public void onMessage(String key, String msg) {
            if (loggeMAM?  ����������������������ə��������������������������ʩ�̩��������Ǻ���ʛ�����Ǚ����ʩ����������ɩ��̬��ʪ��
����˘���ɺ�ȹ�ʸ��             x �    ����     �
    ���    ���� ������� �����   ���Ɉ � �����   ���
�� ��̬   ����p   �                h�1�))��h���QL�6JDI'Ƙ(�ԕL6)أ����=��?#���k�^r+�%͌�(�~<Ql�&D�r��H��bQ	'�o�������׋H�4�勇R�Dz?�E�ŧ��P���A�iy��4�׋i�:a�^���_��S^���"��7�ܠ�0�8XI�x�T��Q�5��5�\��ԧ���F{��V4��bl�)�nk�n�4(�BԎj�4�Z��|�T:ҺB�����hZU.] �Qq`�����#\r���'��� �������R(�#pt���ډ� U���ҥP"5����	�d�#�ƃH��)�X�����BNL�`�D'�W��g�F��"0�� mRxp��:c��j �p���v��1X��S}r������X�WMQ�/Miԁ�H��j�z��&� 4�����i��i��c�@*)�)���n{�i ��f������`��q��i�[R$���&/0���F~U����:��8Mx;����F)����{�t�1�Z�p�����
�@��@m�?�V:�.e��JN��d�ۀ��`��eІOh`�nr @	v=��?A �'<��	P�I+��-/� 13 @57`�9;���L�� &8��	BP0�jն\��bf@�jp� tx`�	=?� A'�:@	�P�R�F�V(а��� ��� ��2�|�� �'�A�	<P��,7��������' :@	�O���-
���@���`�@ۤ� �nr @v�� 
�07@d��!�m����@�2|� 	�O��� iY ��٭\��`d `hl��pt `	x=��`u`��i@��R�����'�<8}�o�=�GpZ�W
�8A%˺��"���0=�@Mt��
>P ˃�F
g��,�<��C��Ⲝr��!�
.l�p� 0�VѾC�f�M���Q����E��G�(яQTD�~����GEQ�D!|TũQQ�����FED�jTE��GQ�*��>��$�] y�����B1���t�`hn� ��ڂ�!Ș�]xA����d��A	6N�|�$b��@������@� 6A�	�N!x����X�p���������d��`����"
�����b*�(j��"��w(���f\��pA�}b`�A6[l��sA��sq��D:0{�m[R��E0�a�4eˁ��8x@:�t�����;�w`� �A�����J��&JGUAQ��xxM�^���E��לi�0a���AZྃ+ 3��8�E���j o��&�j-��� 7/1  5Z��Bqp��)C 40P:N�t��j��n�7A'_�Hc���>����;�p��^<���k��%.A��=�9�{�{�t���61+�����m�;i ށ���� 2��4p>�; w��@Xh�M\�I���jX�nr @v�����MP�G��H(�8t3 ��8H8+:z�,�6�=�B-b��`�����mp�'BA��p���o=Cb �����8P^���p�~p;�|�w�������`�d�f.@��{ Pp{Ah d���W-&����@�!��`,���ȯYha-x�AA��Ё�&��@��`�C ��'ĺ�` ��  z��	Nt� �-�.�pt `z�� �'�A�	<P��$�@���@���@���2|����  �,- �02046Pp8:���L�� &:�@	�O�Ճ
&�@������ ��� ���@2|���� ��eo� |�?"����)�5��sBc(0;�v���7ނ� /�3�� 8�� :�g4<@y`� ��r��-���2�Πh iM� ����0��@�0PW0�]�c�dt�� h�H��� 9b�@fP�Ap��{`t`�x����toR/h`�lp� t|��	�N���!x��x-�� �E�̀ �܀ ��� z~� 	�N�t��� @(F������@���@���A �` C'�@�
�,-��/1  35@��L;�
���8�<pF&�~A%.=�9�~�3p��� ��!�{ aP*�p	�d�N,@�gl���@��Nt��G
�����@@tA�@����X���� �W/�O9�j�t�Q�2��8*�~QQr��T�GE��h
�e0O?|�%�͢R4*"궣Q�m4 {fŉC��}�p`���8�_�h9�s�x�A�م�� �m�OA E���w��	Q=�<�P��7v�jn� t�`��.��]}H���q��X9�u����AZ���#ؑ�tA�H�:�v�� �l��0<�|��ݏ��x�`�DP����{ s���z�q_XN�$�8�e�� ܵ�.�� ���t���������Ev@�������TDjq�"��0*"���Q*��P�Dy��ETbG*�J>�����"8�wZ'J�D=}T�QEԱFTD�kGE��*�߈��1
j���1+���+x+�?�,6x~&8���SE: u���|P|pi���Xt��P�J��l3�΀��|@��H;(v0Ԁ����� P��t��
M���H���4z���r�C����t5`���[��������Xy����,��a�	d`����������x�?Y�/�Q'8��N#(BOp��
)BZ,�`��e����ee`?���ި�Ŷ ?/� 14 P8ѩ32=�C �lJ��lL������@������(��
�"��n�*�(��"*ҡ�����"(ߎ�D�uT'��+=e�[W�f����)��M��4�cef��A7.�DK��8zRn����[%��f�(��L�K����sL�{+���԰�cfN&�8���a�
�5�J�4�y&�3y�	5&�1���+�I�P�L�Zi��e�XJ��FE�X+%z~LT^7�m_�V�Y�Y*�+�6�`�	kb�kjSZ`e�Q)��Vh�-�"T1�Q/XQv%���UZr-�f��V�a���N0�x%dA�=
�̏�`f��hB��K��dhGY��ya�ed����ֽD�2���I,��Ma�A�2,A�¨d�S���I�^��%�	m����Sa!�Kh�lAo~L`���mcd����ɔe���f܇��e+q��gv]laۇ���er��n����c�4
���(�`3^�T�>��!�JIb�-մ�� h6֦O6��.1�sQ�j���l��_�V�U�MQ D�L�{)W� ��ʠU���b��*eJ��Q�ŉiq�܊&ӕ����Z��v,�S�o�C�f�m=s��˻�u�G~�LlK��-�z�^���	?�upLu24:{'3r$���N�$AC�rq�d'v'^�uX'u'QR�tL't����a�C't'=��t��$N�$h"�\N�$Nb$P��n6�Y���2�(�o'R����_C�R�0&�V�&aB,	�I��T9N"w'Dn�>޻�� (                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               