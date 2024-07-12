package org.jeecg.modules;

import org.jeecg.modules.bill.controller.BillController;

/**
 * @author dbc
 * @create 2023-05-18 8:43
 */
public class test {
    public static void main(String[] args) {
        BillController billController = new BillController();
        System.out.println(billController.getClass().getClassLoader().getResource("/"));
    }
}
