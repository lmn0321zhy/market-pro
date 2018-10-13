package com.lmn.common.utils;

import com.google.common.collect.Lists;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * 系统保留小数位数
 */
public class DecimalUtils {

    //成功率保留位数
    public static double successRate(double d) {
        return decimalScale(d, 2);
    }

    public static String successRateStr(double d) {
        return decimalScaleStr(d, 2) + "%";
    }

    public static float decimalScalef(double d, int scale) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double decimalScale(double d, int scale) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double decimalScale(Object d, int scale) {
        BigDecimal b = new BigDecimal(StringUtils.toDouble(d));
        return b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 转换数值为指定保留位数
     *
     * @param d
     * @param scale
     */
    public static String decimalScaleStr(double d, int scale, String fill) {
        if (Double.compare(d, 0) < 0) return fill;
        String f = "%." + scale + "f";
        return String.format(f, d);
    }

    public static String decimalScaleStr(double d, int scale) {
        return decimalScaleStr(d, scale, "--");
    }

    public static String decimalScaleStr(Object d, int scale) {
        return decimalScaleStr(StringUtils.toDouble(d), scale, "--");
    }

    //	前台统一显示格式，-1、-3显示"**"、-2显示"--"、空显示""do
    public static String formatShowValue(Object obj) {
        String result = "";
        if (obj != null) {
            if (obj instanceof String) {
                result = obj.toString();
            } else if (obj instanceof Double) {
                double td = (Double) obj;
                if (Double.compare(td, -1d) == 0 || Double.compare(td, -3d) == 0) {
                    result = "**";
                } else if (Double.compare(td, -2d) == 0) {
                    result = "--";
                } else {
                    DecimalFormat df = new DecimalFormat("0.0");
                    result = df.format(Double.valueOf(obj.toString()));
                }
            } else {
                result = obj.toString();
            }
        }
        return result;
    }

    //针对Double类型
    public static String formatShowValue(Object obj, int scale) {
        String result = "";
        if (obj == null)
            return result;
        if (obj instanceof Double || obj instanceof BigDecimal) {
            double n = (obj instanceof Double ? ((Double) obj) : ((BigDecimal) obj)).doubleValue();
            if (n == -1.0 || n == -3.0) {
                result = "**";
            } else if (n == -2.0) {
                result = "--";
            } else {
                result = DecimalUtils.decimalScaleStr(obj, scale);
            }
        }
        return result;
    }

    /**
     * 转换数值为指定保留位数
     *
     * @param list   待转换的集合
     * @param fields 需要转换的字段
     * @param scales 保留位数
     */
    public static <T> List<T> listTransform(List<T> list, String[] fields, int[] scales) {
        return listTransform(list, fields, scales, null);
    }


    /**
     * 转换数值为指定保留位数
     *
     * @param list   待转换的集合
     * @param fields 需要转换的字段
     * @param scales 保留位数
     * @param fill   填充字符串 fill为空值的时不处理负值
     */
    public static <T> List<T> listTransform(List<T> list, String[] fields, int[] scales, String fill) {
        if (ArrayUtils.isEmpty(fields)) {
            return list;
        }

        int scale = 2;
        boolean flag = false;
        boolean isCompare = fill == null ? false : true;

        if (ArrayUtils.isEmpty(scales)) {
            flag = true;
        } else {
            if (fields.length != scales.length) {
                flag = true;
            }
        }

        List<String> _fields = Lists.newArrayList();

        for (T element : list) {
            try {
                for (int i = 0, j = fields.length; i < j; i++) {
                    double val;
                    //查找以*结尾的字段，此类字段需要表示已此为前缀的都需要转换
                    if (StringUtils.endsWith(fields[i], "*")) {
                        if (Collections3.isEmpty(_fields)) {
                            if (element instanceof Map) {
                                for (Object o : ((Map) element).keySet()) {
                                    if (o.toString().startsWith(fields[i].replace("*", "")))
                                        _fields.add(o.toString());
                                }
                            } else {
                                Map els = BeanUtils.describe(element);
                                for (Object o : els.keySet()) {
                                    if (o.toString().startsWith(fields[i].replace("*", "")))
                                        _fields.add(o.toString());
                                }
                            }
                        }
                        for (String _f : _fields) {
                            val = decimalScale(StringUtils.toDouble(BeanUtils.getProperty(element, _f)), flag ? scale : scales[i]);
                            //BeanUtils.setProperty(element, _f, isCompare ? (Double.compare(val, 0) < 0 ? fill : val) : val);
                            BeanUtils.setProperty(element, _f, val);
                        }
                    } else {
                        val = decimalScale(StringUtils.toDouble(BeanUtils.getProperty(element, fields[i])), flag ? scale : scales[i]);
                        BeanUtils.setProperty(element, fields[i], val);
                    }
                    continue;
                }

            } catch (Exception e) {
            }
        }
        return list;
    }

    /**
     * 成功率，在线率，功率，电量，负载率，电压，电流、安装覆盖率、功率因数等，需要保留2位小数
     *
     * @param list  待转换列表
     * @param field 指定转换的字段
     */
    public static <T> List<T> listRateTransform(List<T> list, String field) {
        return listRateTransform(list, new String[]{field});
    }

    public static <T> List<T> listRateTransform(List<T> list, String[] fields) {
        return listTransform(list, fields, new int[]{2}, "--");
    }

    public static String rateTransform(double d) {
        return decimalScaleStr(d, 2, "--");
    }

    /**
     * 读数类保留4位小数
     *
     * @param list  待转换列表
     * @param field 指定转换的字段
     */
    public static <T> List<T> listReadingTransform(List<T> list, String field) {
        return listReadingTransform(list, new String[]{field});
    }

    public static <T> List<T> listReadingTransform(List<T> list, String[] fields) {
        return listTransform(list, fields, new int[]{4}, "--");
    }

    public static String readingTransform(double d) {
        return decimalScaleStr(d, 4, "--");
    }
}
