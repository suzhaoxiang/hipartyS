/**
 * @author 何宇寰
 *
 * @requires jQuery,EasyUI
 *
 * 扩展validatebox，添加验证正数功能
 */
$.extend($.fn.validatebox.defaults.rules, {
    isPositive: {
        validator: function (value) {
            return value > 0;
        },
        message: '请输入正数！'
    }
});
/**
 * @author 何宇寰
 *
 * @requires jQuery,EasyUI
 *
 * 扩展validatebox，添加验证物流费用功能
 */
$.extend($.fn.validatebox.defaults.rules, {
    isFee: {
        validator: function (value) {
            return parseFloat(value) >= 0;
        },
        message: '请输入正确的物流费用'
    }
});
/**
 * @author 何宇寰
 *
 * @requires jQuery,EasyUI
 *
 * 扩展validatebox，添加验证姓名功能
 */
$.extend($.fn.validatebox.defaults.rules, {
    isName: {
        validator: function (value) {
            var regName = /^[\u4e00-\u9fa5]{2,4}$/;
            if (regName.test(value)) {
                return true;
            } else {
                return false;
            }
        },
        message: '请输入正确的姓名'
    }
});
/**
 * @author 何宇寰
 *
 * @requires jQuery,EasyUI
 *
 * 扩展validatebox，添加验证手机号码功能
 */
$.extend($.fn.validatebox.defaults.rules, {
    isPhone: {
        validator: function (value, param) {
            var mobileRegex = /^(((1[3456789][0-9]{1})|(15[0-9]{1}))+\d{8})$/;
            if (mobileRegex.test(value)) {
                return true;
            } else {
                return false;
            }
        },
        message: '请输入正确的手机号码'
    }
});