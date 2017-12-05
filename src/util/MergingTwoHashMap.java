package util;

import java.util.HashMap;
import java.util.TreeMap;

public class MergingTwoHashMap {
    public static HashMap<TreeMap<String,Integer>,Double> merge(
            HashMap<TreeMap<String,Integer>,Double> map1,
            HashMap<TreeMap<String,Integer>,Double> map2){
        if(map1==null || map1.size()==0){
            return map2;
        }else if(map2==null || map2.size()==0){
            return map1;
        }else{
            return mergeNonEmptyHashMap(map1,map2);
        }
    }

    public static HashMap<TreeMap<String,Integer>,Double> mergeNonEmptyHashMap(
            HashMap<TreeMap<String,Integer>,Double> map1,
            HashMap<TreeMap<String,Integer>,Double> map2){
        HashMap<TreeMap<String,Integer>,Double> result=new HashMap<TreeMap<String,Integer>,Double>();
        for(TreeMap<String,Integer> l1: map1.keySet()){
            if(map2.containsKey(l1)){
                result.put(l1,map1.get(l1)+map2.get(l1));
                map2.remove(l1);
            }else{
                result.put(l1,map1.get(l1));
            }
        }
        for(TreeMap<String,Integer> l2 : map2.keySet()){
            result.put(l2,map2.get(l2));
        }
        return result;
    }

    public static void main(String[] args){
        System.out.println("hello world");
    }
}
