package util;

import java.util.*;

public class MergingTwoTreeMap {
    public static void putIntoTreeMap(TreeMap<String,Integer> treeMap, String key, int value){
        if(!key.equals("")){
            treeMap.put(key,value);
        }
    }

    public static TreeMap<String,Integer> merge(TreeMap<String,Integer> map1, TreeMap<String,Integer> map2){
        if(map1==null || map1.size()==0){
            return map2;
        }
        if(map2==null || map2.size()==0){
            return map1;
        }
        return mergeNonEmpty(map1,map2);
    }

    public static TreeMap<String,Integer> mergeNonEmpty(TreeMap<String,Integer> map1, TreeMap<String,Integer> map2){
        TreeMap<String,Integer> result=new TreeMap<String, Integer>();

        Collection<String> keys1 = map1.keySet();
        Iterator<String> itr1 = keys1.iterator();

        Collection<String> keys2 = map2.keySet();
        Iterator<String> itr2 = keys2.iterator();

        boolean firstRunInLoop=true;
        String key1=itr1.next();
        String key2=itr2.next();
        int index1=0;
        int index2=0;
        int lengthMap1=map1.size();
        int lengthMap2=map2.size();
        while(index1<lengthMap1 && index2<lengthMap2){
            if(key1.compareTo(key2)<0){
                putIntoTreeMap(result,key1,map1.get(key1));
                index1++;
                if(itr1.hasNext()==false){
                    break;
                }
                key1=itr1.next();
            }else if(key1.compareTo(key2)>0){
                putIntoTreeMap(result,key2,map2.get(key2));
                index2++;
                if(itr2.hasNext()==false){
                    break;
                }
                key2=itr2.next();
//                System.out.println("putIntoTreeMap:"+key2+","+map2.get(key2));
            }else{
                int value=map1.get(key1)+map2.get(key2);
                putIntoTreeMap(result,key1,value);
                index1++;
                index2++;
                if(itr1.hasNext()==false && itr2.hasNext()==false){
                    break;
                }else if(itr1.hasNext()==false && itr2.hasNext()==true){
                    key2=itr2.next();
                    break;
                }else if(itr1.hasNext()==true && itr2.hasNext()==false){
                    key1=itr1.next();
                }else if(itr1.hasNext()==true && itr2.hasNext()==true){
                    key1=itr1.next();
                    key2=itr2.next();
                }
            }
        }

        while(index1<lengthMap1){//todo
            putIntoTreeMap(result,key1,map1.get(key1));
            index1++;
            if(itr1.hasNext()==false){
                break;
            }
            key1=itr1.next();
        }
        while(index2<lengthMap2){
            putIntoTreeMap(result,key2,map2.get(key2));
            index2++;
            if(itr2.hasNext()==false){
                break;
            }
            key2=itr2.next();
        }


        return result;
    }

    public static void output(TreeMap<String,Integer> map){
        Collection<String> keys = map.keySet();
        Iterator<String> itr = keys.iterator();

        while(itr.hasNext()){
            String key=itr.next();
            System.out.println(key+"\t"+map.get(key));
        }
    }

    public static void main(String[] args){
        TreeMap<String, Integer> map1=new TreeMap<String, Integer>();
        TreeMap<String, Integer> map2=new TreeMap<String, Integer>();

//        map1.put("0",1);
//        map1.put("2",1);
        map1.put("2",1);
        map1.put("4",1);
        map1.put("5",1);
        map1.put("7",1);
        map1.put("9",1);

        map2.put("2",1);
        map2.put("4",1);
        map2.put("6",1);
        map2.put("8",1);
        map2.put("10",1);
//        for(int i=0;i<1000000;i++){
//            map1.put(Integer.toString(2*i),1);
//            map2.put(Integer.toString(6*i),1);
//        }
        long start = System.currentTimeMillis();
        TreeMap<String,Integer> mergedMap=merge(map1,map2);
        long end = System.currentTimeMillis();
        long elapsedMillis=end-start;
        System.out.println("cost "+elapsedMillis + " ms");
        System.out.println("map1");
//        output(map1);
        System.out.println("map2");
//        output(map2);
        System.out.println("mergedMap");
        output(mergedMap);
        System.out.println("size of mergedMap="+mergedMap.size());
//        output(map2);
    }
}
