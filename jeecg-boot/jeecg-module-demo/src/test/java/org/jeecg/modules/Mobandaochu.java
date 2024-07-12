package org.jeecg.modules;

import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.TemplateExportParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板导出
 */
public class Mobandaochu {

    public static TemplateExportParams getTemplateParams(String name){
        return new TemplateExportParams("D:\\1MyProject\\111\\"+name+".xlsx");
    }

    public static Workbook putong() {
        TemplateExportParams params = getTemplateParams("purchaseOrder");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "员工个人信息");
        map.put("paymentStyle", "北京机器猫科技有限公司");
        map.put("billDate", "2020-07-13");
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        return workbook;
    }

    public static void main(String[] args) throws IOException {
        Workbook workbook = putong();
        File savefile = new File("D:\\1MyProject\\111");
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        FileOutputStream fos = new FileOutputStream("D:\\1MyProject\\111\\result.xlsx");
        workbook.write(fos);
        fos.close();
    }

}