var purchase_orders_pojo = function () {
    var members = arguments;

    return function () {
        var obj = {}, i = 0, j = members.length;
        for (; i < j; ++i) {
            obj[members[i]] = arguments[i];
        }

        return obj;
    };
};
