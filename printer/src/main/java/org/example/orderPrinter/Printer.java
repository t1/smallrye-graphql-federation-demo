package org.example.orderPrinter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Printer {
    private final Order order;

    public void print() {
        System.out.println();
        System.out.println("Order: " + order.getOrderNumber());
        System.out.println("Date : " + order.getOrderDate());
        System.out.println("Name : " + order.getCustomerName() + "   [" + order.getCustomerNumber() + "]");
        System.out.println();
        System.out.println(" #  n  Product    Price   Total");
        for (var item : order.getOrderItems()) {
            var product = item.getProduct();
            System.out.printf("%2d %2d %8s %5d.%2d %5d.%2d\n", item.getPosition(), item.getAmount(),
                product.getName(), product.getPrice() / 100, product.getPrice() % 100,
                item.getTotalPrice() / 100, item.getTotalPrice() % 100);
        }
        System.out.println();
        System.out.printf("Total: %2d Packages      %5d.%2d\n", order.amountSum(), order.priceSum() / 100, order.priceSum() % 100);
        System.out.println();
        System.out.println();
    }
}
