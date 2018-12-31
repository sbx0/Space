var main_data = {
    show: {},
    hide: {},
};

var select_page = 'tools';

var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
        tool_list_data: '',
        tool_hide_data: '',
        hide_show: false,
        first_get: true,
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
        'tool_list_components': {
            props: ['tool_list'],
            template:
                '<a :href="tool_list.path" class="list-group-item d-flex justify-content-between align-items-center">\n    ' +
                '{{tool_list.text}}\n ' +
                '   <span class="badge badge-primary badge-pill">{{tool_list.badge}}</span>\n    ' +
                '<input type="text" :id="tool_list.id" :value="tool_list.top" hidden>\n' +
                '</a>',
        },
    },
    created: function () {
        getUrl();
    },
});

$("#select_page").change(function () {
    select_page = $("#select_page").val();
    main.first_get = true;
    main.tool_list_data = '';
    main.tool_hide_data = '';
    main.hide_show = false;
    getUrl();
});

// 获取未被隐藏的链接
function getUrl() {
    $.ajax({
        url: '../url/get?page=' + select_page,
        type: 'GET',
        async: true,
        success: function (json) {
            if (json.urls != null && json.urls.length > 0) {
                main.tool_list_data = json.urls;
            } else {
                getHiddenUrl();
                main.hide_show = true;
            }
            // 拖拽列表
            var tools_list_div = document.getElementById('tool_list_div');
            Sortable.create(tools_list_div, {
                group: "tools",
                onStart: function () {
                    getHiddenUrl();
                    main.hide_show = true;
                },
                onEnd: function () {

                },
                onUpdate: function () {
                    updateSort();
                },
                onAdd: function () {
                    updateSort();
                },
                onRemove: function () {
                    updateSort();
                },
            });
        },
        error: function () {
            alert("网络异常")
        }
    });
}

// 获取未被隐藏的链接
function getHiddenUrl() {
    if (main.first_get) {
        $.ajax({
            url: '../url/getHidden?page=' + select_page,
            type: 'GET',
            async: true,
            success: function (json) {
                if (json.urls != null && json.urls.length > 0) {
                    main.tool_hide_data = json.urls;
                    main.hide_show = true;
                }
                var tool_hide_div = document.getElementById('tool_hide_div');
                Sortable.create(tool_hide_div, {
                    group: "tools",
                });
            },
            error: function () {
                alert("网络异常")
            }
        });
    }
    main.first_get = false;
}

function updateSort() {
    main_data.show = {};
    main_data.hide = {};
    var tools_list_div = document.getElementById('tool_list_div');
    if (tools_list_div.childNodes.length > 0) {
        var tool_list = tools_list_div.childNodes;
        for (var i = 0; i < tool_list.length; i++) {
            var url = tool_list.item(i);
            var item = url.childNodes;
            item[3].value = i + 1;
            main_data.show[item[3].id] = item[3].value;
        }
    }
    var tool_hide_div = document.getElementById('tool_hide_div');
    var tool_hide = tool_hide_div.childNodes;
    if (tool_hide_div.childNodes.length > 0) {
        for (var i = 0; i < tool_hide.length; i++) {
            var url = tool_hide.item(i);
            var item = url.childNodes;
            item[3].value = -(i + 1);
            main_data.hide[item[3].id] = item[3].value;
        }
    }
    // main_data.replace('#');
    $.ajax({
        url: '../url/update',
        type: 'POST',
        data: JSON.stringify(main_data),
        dataType: 'json',
        contentType: "application/json",
        traditional: true,
        success: function (data) {

        },
    })
}

