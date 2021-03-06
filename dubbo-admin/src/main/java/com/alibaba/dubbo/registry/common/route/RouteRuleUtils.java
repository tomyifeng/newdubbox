/**
 * Project: dubbo.registry.server
 * 
 * File Created at Oct 20, 2010
 * $Id: RouteRuleUtils.java 181192 2012-06-21 05:05:47Z tony.chenl $
 * 
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.dubbo.registry.common.route;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.dubbo.common.utils.StringUtils;

/**
 * @author william.liangf
 * @author ding.lid
 */
public class RouteRuleUtils {
    private RouteRuleUtils() {}
    
    /**
     * 把条件的一个键值展开后，合并到另外指定的键值中。
     * @param <T> 集合类型
     * @param condition 条件
     * @param srcKeyName 要展开的键值
     * @param destKeyName 合并到的键值
     * @param expandName2Set 进行展开的值到值的映射
     */
    public static <T extends Collection<String>> Map<String, RouteRule.MatchPair> expandCondition(
            Map<String, RouteRule.MatchPair> condition, String srcKeyName, String destKeyName,
            Map<String, T> expandName2Set) {
        if(null == condition || StringUtils.isEmpty(srcKeyName) || StringUtils.isEmpty(destKeyName)) {
            return condition;
        }
        
        RouteRule.MatchPair matchPair = condition.get(srcKeyName);
        if(matchPair == null) {
            return condition;
        }
    
        Map<String, RouteRule.MatchPair> ret = new HashMap<String, RouteRule.MatchPair>();
        
        Iterator<Entry<String, RouteRule.MatchPair>> iterator = condition.entrySet().iterator();
        for(; iterator.hasNext();) {
            Entry<String, RouteRule.MatchPair> entry = iterator.next();
            String condName = entry.getKey();
            
            // 即不是源也不目的
            if(!condName.equals(srcKeyName) && !condName.equals(destKeyName)) {
                RouteRule.MatchPair p = entry.getValue();
                if(p != null) ret.put(condName, p);
            }
            // 等于源
            else if(condName.equals(srcKeyName)) {
                RouteRule.MatchPair from = condition.get(srcKeyName);
                RouteRule.MatchPair to = condition.get(destKeyName);
                
                // 没有可Expand条目
                if(from == null || from.getMatches().isEmpty() && from.getUnmatches().isEmpty()) {
                    if(to != null) ret.put(destKeyName, to);
                    continue;
                }
                
                Set<String> matches = new HashSet<String>();
                Set<String> unmatches = new HashSet<String>();
                // 添加上Expand来的条目
                for(String s : from.getMatches()) {
                    if(expandName2Set == null || !expandName2Set.containsKey(s)) continue;
                    
                    matches.addAll(expandName2Set.get(s));
                }
                for(String s : from.getUnmatches()) {
                    if(expandName2Set == null || !expandName2Set.containsKey(s)) continue;
                    
                    unmatches.addAll(expandName2Set.get(s));
                }
                // 添加原来的条目
                if(to != null) {
                    matches.addAll(to.getMatches());
                    unmatches.addAll(to.getUnmatches());
                }
                
                ret.put(destKeyName, new RouteRule.MatchPair(matches, unmatches));
            }
            // else 是 Key == destKeyName 的情况，无操作
        }
        
        return ret;
    }
    ��� �c������mAA  �                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      