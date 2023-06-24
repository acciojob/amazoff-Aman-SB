package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    private Map<String,Order> storeOrder= new HashMap<>();
    private Map<String,DeliveryPartner> storeDeliveryPartner = new HashMap<>();
    private Map<String,String> orderToPartner = new HashMap<>();
    private Map<String, List<String>> partnerWithOrder = new HashMap<>();

    public void addOrder(Order order) {
        storeOrder.put(order.getId(),order);
    }

    public void addPartner(DeliveryPartner deliveryPartner) {
        storeDeliveryPartner.put(deliveryPartner.getId(),deliveryPartner);
    }


    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(storeOrder.containsKey(orderId) && storeDeliveryPartner.containsKey(partnerId)){
            // pair order to partner
            orderToPartner.put(orderId,partnerId);

            List<String> partnerOrders = new ArrayList<>();

            if(partnerWithOrder.containsKey(partnerId)){
                partnerOrders = partnerWithOrder.get(partnerId);
            }

            partnerOrders.add(orderId);
//            partnerWithOrder.remove(partnerId);
            partnerWithOrder.put(partnerId,partnerOrders);
            DeliveryPartner deliveryPartner = storeDeliveryPartner.get(partnerId);
            deliveryPartner.setNumberOfOrders(partnerOrders.size());
        }
    }

    public Order getOrderById(String orderId) {
        return storeOrder.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return storeDeliveryPartner.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return (Integer) storeDeliveryPartner.get(partnerId).getNumberOfOrders();
    }


    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerWithOrder.get(partnerId);
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for(String order : storeOrder.keySet()){
            orders.add(order);
        }
        return orders;
    }


    public Integer getCountOfUnassignedOrders() {
        return storeOrder.size() - orderToPartner.size();
    }


    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId) {
        List<String> partnerOrders= partnerWithOrder.get(partnerId);

        int count = 0;

        for(String ordersId : partnerOrders){
            int deliveryTime = storeOrder.get(ordersId).getDeliveryTime();
            if(deliveryTime > time){
                count++;
            }
        }
        return count;
    }


    public int getLastDeliveryTimeByPartnerId(String partnerId) {
        List<String> partnerOrders= partnerWithOrder.get(partnerId);
        int maxTime = 0 ;
        for(String orderId : partnerOrders){
            int deliveryTime = storeOrder.get(orderId).getDeliveryTime();
            maxTime = Math.max(maxTime,deliveryTime);
        }
        return maxTime;
    }


    public void deletePartnerById(String partnerId) {
        List<String> ordersToUnassigned= new ArrayList<>();

        ordersToUnassigned = partnerWithOrder.get(partnerId);
        for(String orderId : ordersToUnassigned){
            orderToPartner.remove(orderId);
        }

        partnerWithOrder.remove(partnerId);

        storeDeliveryPartner.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        //delete from store Order
        storeOrder.remove(orderId);
        //delete from orderwithpartner
        String partnerId = orderToPartner.get(orderId);
        orderToPartner.remove(orderId);
        //delete from partnerwithorder
        List<String> curr = partnerWithOrder.get(partnerId);
        curr.remove(orderId);
        partnerWithOrder.put(partnerId,curr);
    }
}
