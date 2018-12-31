var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        url_text: '',
        url_badge: '',
        url_url: '',
        url_title: '',
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components,
        },
        'nav_list_components': {
            props: ['nav_list'],
            template:
                '<a :href="nav_list.path" class="list-group-item d-flex justify-content-between align-items-center">' +
                '   {{nav_list.text}}' +
                '   <span class="badge badge-primary badge-pill">{{nav_list.badge}}</span>' +
                '   <input type="text" :id="nav_list.id" :value="nav_list.top" hidden>' +
                '</a>',
        },
    },
    created: function () {

    },
});

// 提交链接
$("#add_nav_button").click(function () {
    $(this).toggle();
    $.ajax({
        url: '../url/saveOrUpdate',
        type: 'POST',
        async: false,
        data: $("#add_nav_form").serialize(),
        dataType: "json",
        success: function (json) {
            var status = json.status;
            if (statusCodeToBool(status)) {
            }
            alert(statusCodeToAlert(status));
        },
        error: function () {
            alert("网络异常");
        }
    });
    $(this).toggle();
    return false;
});