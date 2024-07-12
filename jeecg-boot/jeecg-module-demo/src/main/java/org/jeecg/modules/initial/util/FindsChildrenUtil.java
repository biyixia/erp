package org.jeecg.modules.initial.util;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.initial.entity.MaterialType;
import org.jeecg.modules.initial.entity.MaterialTypeIdModel;
import org.jeecg.modules.initial.entity.MaterialTypeTreeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 * 对应部门的表,处理并查找树级数据
 * <P>
 * 
 * @Author: Steve
 * @Date: 2019-01-22
 */
public class FindsChildrenUtil<I,T,S> {

	//部门树信息-树结构
	//private static List<MaterialTypeTreeModel> MaterialTypeTreeList = new ArrayList<MaterialTypeTreeModel>();
	
	//部门树id-树结构
    //private static List<MaterialTypeIdModel> idList = new ArrayList<>();


    /**
     * queryTreeList的子方法 ====1=====
     * 该方法是s将MaterialType类型的list集合转换成MaterialTypeTreeModel类型的集合
     */
    public static List<MaterialTypeTreeModel> wrapTreeDataToTreeList(List<MaterialType> recordList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
    	List<MaterialTypeIdModel> idList = new ArrayList<>();
        List<MaterialTypeTreeModel> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            MaterialType depart = recordList.get(i);
            records.add(new MaterialTypeTreeModel(depart));
        }
        List<MaterialTypeTreeModel> tree = findChildren(records, idList);
        setEmptyChildrenAsNull(tree);
        return tree;
    }

    /**
     * 获取 MaterialTypeIdModel
     * @param recordList
     * @return
     */
    public static List<MaterialTypeIdModel> wrapTreeDataToDepartIdTreeList(List<MaterialType> recordList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
        List<MaterialTypeIdModel> idList = new ArrayList<MaterialTypeIdModel>();
        List<MaterialTypeTreeModel> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            MaterialType depart = recordList.get(i);
            records.add(new MaterialTypeTreeModel(depart));
        }
        findChildren(records, idList);
        return idList;
    }

    /**
     * queryTreeList的子方法 ====2=====
     * 该方法是找到并封装顶级父类的节点到TreeList集合
     */
    private static List<MaterialTypeTreeModel> findChildren(List<MaterialTypeTreeModel> recordList,
                                                         List<MaterialTypeIdModel> idList) {

        List<MaterialTypeTreeModel> treeList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            MaterialTypeTreeModel branch = recordList.get(i);
            if ("0".equals(branch.getPid())) {
                treeList.add(branch);
                MaterialTypeIdModel MaterialTypeIdModel = new MaterialTypeIdModel().convert(branch);
                idList.add(MaterialTypeIdModel);
            }
        }
        getGrandChildren(treeList,recordList,idList);
        
        //idList = departIdList;
        return treeList;
    }

    /**
     * queryTreeList的子方法====3====
     *该方法是找到顶级父类下的所有子节点集合并封装到TreeList集合
     */
    private static void getGrandChildren(List<MaterialTypeTreeModel> treeList,List<MaterialTypeTreeModel> recordList,List<MaterialTypeIdModel> idList) {

        for (int i = 0; i < treeList.size(); i++) {
            MaterialTypeTreeModel model = treeList.get(i);
            MaterialTypeIdModel idModel = idList.get(i);
            for (int i1 = 0; i1 < recordList.size(); i1++) {
                MaterialTypeTreeModel m = recordList.get(i1);
                if (m.getPid()!=null && m.getPid().equals(model.getId())) {
                    model.getChildren().add(m);
                    MaterialTypeIdModel dim = new MaterialTypeIdModel().convert(m);
                    idModel.getChildren().add(dim);
                }
            }
            getGrandChildren(treeList.get(i).getChildren(), recordList, idList.get(i).getChildren());
        }

    }
    

    /**
     * queryTreeList的子方法 ====4====
     * 该方法是将子节点为空的List集合设置为Null值
     */
    private static void setEmptyChildrenAsNull(List<MaterialTypeTreeModel> treeList) {

        for (int i = 0; i < treeList.size(); i++) {
            MaterialTypeTreeModel model = treeList.get(i);
            if (model.getChildren().size() == 0) {
                model.setChildren(null);
                model.setIsLeaf(true);
            }else{
                setEmptyChildrenAsNull(model.getChildren());
                model.setIsLeaf(false);
            }
        }
        // MaterialTypeTreeList = treeList;
    }
}
