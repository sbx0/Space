$(document).ready(function () {

    $("a[name='zh_CN']").click(function () {
        i18N("zh_CN")
    })

    $("a[name='zh_TW']").click(function () {
        i18N("zh_TW")
    })

    $("a[name='en_US']").click(function () {
        i18N("en_US")
    })

    function i18N(request_locale) {
        $.ajax({
            type: "post",
            url: "user-i18N?request_locale=" + request_locale,
            dataType: "json",
            success: function (data) {
                if (data.status == 0) {
                    location.replace(location.href)
                } else {
                    alert(i18N.server_error)
                }
            }
        })
    }

})