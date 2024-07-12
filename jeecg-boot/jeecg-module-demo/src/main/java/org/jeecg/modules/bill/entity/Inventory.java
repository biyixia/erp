package org.jeecg.modules.bill.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dbc
 * @create 2023-05-07 22:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private String inWarehouseId;
    private String materialCode;
    private String sumCount;
}
