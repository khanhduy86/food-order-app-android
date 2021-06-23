package com.learn.kdnn.utils;

import com.learn.kdnn.model.CartItem;

import java.util.Map;
import java.util.Set;

public class AppUtils {

    public static double getTotalSalesPrice(Map<Integer,Object> bag){
        double rs = 0;
        Set<Integer> keys = bag.keySet();
        for (Integer key :
                keys) {
            CartItem item = (CartItem)bag.get(key);
            int quality = item.getQuality();
            double standardPrice = item.getProduct().getPrice();
            double per = item.getProduct().getDiscountPer();
            rs+= quality*(standardPrice - (standardPrice * per / 100));

        }
        return rs;
    }
    public static double getDefailtPrice(Map<Integer, Object> bag) {
        double rs = 0;
        Set<Integer> keys = bag.keySet();
        for (Integer key :
                keys) {
            CartItem item = (CartItem)bag.get(key);
            int quality = item.getQuality();
            double standardPrice = item.getProduct().getPrice();
            rs+=quality*standardPrice;

        }
        return rs;
    }
}
