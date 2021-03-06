                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
            assertEquals(serviceUrls, preview);
            
            // no route, ghost methods
            preview = RouteUtils.previewRoute("hello.HelloService", "1.2.3.4", "application=morgan&methods=getPort,say,ghostMethod", serviceUrls, route, clusters, null);
            assertEquals(serviceUrls_ghostMethods, preview);
        }
        
        // with cluster
        {
            route.setMatchRule("consumer.cluster = cluster1");
            
            // no method
            Map<String, String> preview = RouteUtils.previewRoute("hello.HelloService", "7.7.7.7:20880", "application=morgan", serviceUrls, route, clusters, null);
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.4:20880/hello.HelloService", "dubbo=2.0.0&version=1.0.0&revision=1.1.1&methods=*&application=morgan");
            assertEquals(expected, preview);
            
            // 2 methods
            preview = RouteUtils.previewRoute("hello.HelloService", "7.7.7.7", "application=morgan&methods=getPort,say", serviceUrls, route, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.4:20880/hello.HelloService", "dubbo=2.0.0&version=1.0.0&revision=1.1.1&methods=getPort,say&application=morgan");
            assertEquals(expected, preview);
            
            // ghost method
            preview = RouteUtils.previewRoute("hello.HelloService", "7.7.7.7", "application=morgan&methods=getPort,say,ghostMethod", serviceUrls, route, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.4:20880/hello.HelloService", "dubbo=2.0.0&version=1.0.0&revision=1.1.1&methods=getPort,say,ghostMethod&application=morgan");
            assertEquals(expected, preview);
            
            // no route, no methods
            preview = RouteUtils.previewRoute("hello.HelloService", "1.2.3.4", "application=morgan", serviceUrls, route, clusters, null);
            assertEquals(serviceUrls_starMethods, preview);
            
            // no route, 2 methods
            preview = RouteUtils.previewRoute("hello.HelloService", "1.2.3.4", "application=morgan&methods=getPort,say", serviceUrls, route, clusters, null);
            assertEquals(serviceUrls, preview);
            
            preview = RouteUtils.previewRoute("hello.HelloService", "1.2.3.4", "application=morgan&methods=getPort,say,ghostMethod", serviceUrls, route, clusters, null);
            assertEquals(serviceUrls_ghostMethods, preview);
        }
    }
    
    @Test
    public void testRoute() throws Exception {
        {
            // no method
            Map<String, String> result = RouteUtils.route("hello.HelloService:1.0.0", "1.1.2.2:20880", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&application=kylin", serviceUrls, routes, clusters, null);
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=*&application=bops");
            assertEquals(expected, result);
            
            // 2 methods
            result = RouteUtils.route("cn/hello.HelloService", "1.1.2.2", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say&application=kylin", serviceUrls, routes, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=getPort,say&application=bops");
            assertEquals(expected, result);
            
            // ghost method
            result = RouteUtils.route("cn/hello.HelloService:2.0.0", "1.1.2.2", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say,ghostMethod&application=kylin", serviceUrls, routes, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=getPort,say,ghostMethod&application=bops");
            assertEquals(expected, result);
            
            // no route, no method
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls_starMethods, result);
            
            // no route, 2 methods
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls, result);
            
            // no route, ghost method
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say,ghostMethod&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls_ghostMethods, result);
        }
        
        // with cluster
        {
            routes.get(0).setMatchRule("consumer.cluster = cluster1");
            
            // no method
            Map<String, String> result = RouteUtils.route("hello.HelloService", "7.7.7.7:20880", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&application=kylin", serviceUrls, routes, clusters, null);
            Map<String, String> expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=*&application=bops");
            assertEquals(expected, result);
            
            // 2 methods
            result = RouteUtils.route("hello.HelloService", "7.7.7.7", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say&application=kylin", serviceUrls, routes, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=getPort,say&application=bops");
            assertEquals(expected, result);
            
            // ghost method
            result = RouteUtils.route("hello.HelloService", "7.7.7.7", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say,ghostMethod&application=kylin", serviceUrls, routes, clusters, null);
            expected = new HashMap<String, String>();
            expected.put("dubbo://3.3.4.6:20883/hello.HelloService", "dubbo=2.0.0&version=3.0.0&revision=3.1.1&methods=getPort,say,ghostMethod&application=bops");
            assertEquals(expected, result);
            
            // no route, no method
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls_starMethods, result);
            
            // no route, 2 methods
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls, result);
            
            // no route, ghost method
            result = RouteUtils.route("hello.HelloService", "1.2.3.4", "dubbo=2.0.0&version=3.0.0&revision=3.0.0&methods=getPort,say,ghostMethod&application=kylin", serviceUrls, routes, clusters, null);
            assertEquals(serviceUrls_ghostMethods, result);
        }
    }

    @Test
    public void test_isSerivceNameMatched() throws Exception {
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService", "com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService", "cn/com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService", "cn/com.alibaba.morgan.MemberService"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService", "com.alibaba.morgan.MemberService"));
        
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*", "com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*", "cn/com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*", "cn/com.alibaba.morgan.MemberService"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*", "com.alibaba.morgan.MemberService"));
        
        assertFalse(RouteUtils.isSerivceNameMatched("cn/com.alibaba.morgan.Member*", "com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("cn/com.alibaba.morgan.MemberService", "cn/com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("cn/com.alibaba.morgan.Member*", "cn/com.alibaba.morgan.MemberService"));
        assertFalse(RouteUtils.isSerivceNameMatched("cn/com.alibaba.morgan.Member*", "intl/com.alibaba.morgan.MemberService"));
        assertFalse(RouteUtils.isSerivceNameMatched("cn/com.alibaba.morgan.Member*", "com.alibaba.morgan.MemberService"));
        
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*:1.0.0", "com.alibaba.morgan.MemberService:1.0.0"));
        assertTrue(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService:1.0.0", "cn/com.alibaba.morgan.MemberService:1.0.0"));
        assertFalse(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.MemberService:1.0.0", "cn/com.alibaba.morgan.MemberService:2.0.0"));
        assertFalse(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*:1.0.0", "cn/com.alibaba.morgan.MemberService"));
        assertFalse(RouteUtils.isSerivceNameMatched("com.alibaba.morgan.Member*:1.0.0", "com.alibaba.morgan.MemberService"));
    }
}

