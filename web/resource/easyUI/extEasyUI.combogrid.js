$.extend($.fn.datagrid.defaults.editors, {
combogrid: {
init: function (container, options) {
var input = $('<input type="text" class="datagrid-editable-input">').appendTo(container);
input.combogrid(options);
return input;
},
destroy: function (target) {
$(target).combogrid('destroy');
},
getValue: function (target) {
return $(target).combogrid('getValue');
},
setValue: function (target, value) {
$(target).combogrid('setValue', value);
},
resize: function (target, width) {
$(target).combogrid('resize', width);
}
}
});