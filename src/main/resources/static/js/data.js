var main = new Vue({
    el: '#main',
    data: {
        space: i18N.space,
        nav_bar_data: i18N.nav_bar_data,
        nav_scroller_data: i18N.nav_scroller_data,
    },
    components: {
        'nav_bar_components': {
            props: ['nav_bar'],
            template: i18N.nav_bar_components,
        },
        'nav_scroller_components': {
            props: ['nav_scroller'],
            template: i18N.nav_scroller_components
        },
    },
    created: function () {

    },
});

var view_7_data = [];
var view_7 = new G2.Chart({
    id: 'view_7',
    forceFit: true,
    height: 400,
});
G2.track(false);
$.ajax({
    url: "../log/data/views?day=7",
    type: "GET",
    async: false,
    success: function (d) {
        view_7_data = d;
        view_7.source(view_7_data, {
            time: {
                alias: '日期',
            },
            number: {
                alias: '访问数',
            }
        });
        view_7.line().position('time*number').size(3);
        view_7.render();
    },
});

var view_30_data = [];
var view_30 = new G2.Chart({
    id: 'view_30',
    forceFit: true,
    height: 400,
});
G2.track(false);
$.ajax({
    url: "../log/data/views?day=30",
    type: "GET",
    async: false,
    success: function (d) {
        view_30_data = d;
        view_30.source(view_30_data, {
            time: {
                alias: '日期',
            },
            number: {
                alias: '访问数',
            }
        });
        view_30.line().position('time*number').size(3);
        view_30.render();
    },
});

// var view_90_data = [];
// var view_90 = new G2.Chart({
//     id: 'view_90',
//     forceFit: true,
//     height: 400,
// });
// G2.track(false);
// $.ajax({
//     url: "../log/data/views?day=90",
//     type: "GET",
//     async: false,
//     success: function (d) {
//         view_90_data = d;
//         view_90.source(view_90_data, {
//             time: {
//                 alias: '日期',
//             },
//             number: {
//                 alias: '访问数',
//             }
//         });
//         view_90.line().position('time*number').size(3);
//         view_90.render();
//     },
// });
//
// var view_360_data = [];
// var view_360 = new G2.Chart({
//     id: 'view_360',
//     forceFit: true,
//     height: 400,
// });
// G2.track(false);
// $.ajax({
//     url: "../log/data/views?day=360",
//     type: "GET",
//     async: false,
//     success: function (d) {
//         view_360_data = d;
//         view_360.source(view_360_data, {
//             time: {
//                 alias: '日期',
//             },
//             number: {
//                 alias: '访问数',
//             }
//         });
//         view_360.line().position('time*number').size(3);
//         view_360.render();
//     },
// });