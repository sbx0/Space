var i18N = {
    nav_bar_data: [
        {id: 1, text: '登陆', path: '../login.html'},
        {id: 2, text: '工具', path: '../tools.html'},
        {id: 3, text: '日志', path: '../log/list'},
        {id: 4, text: '发布', path: '../post.html'},
        {id: 5, text: '回收', path: '../article/trash'},
        {id: 6, text: '地图', path: '../site_map.xml'},
    ],
    'nav_bar_components':
        '<li class="nav-item">\n    ' +
        '<a class="nav-link" :href="nav_bar.path" v-html="nav_bar.text" :title="nav_bar.title"></a>\n' +
        '</li>',
    nav_scroller_data: [
        {id: 1, text: '首页', path: '../index.html'},
        {id: 2, text: '搜索', path: '../article/search'},
        {id: 3, text: '消息', path: '../message.html'},
        {id: 4, text: '反馈', path: '../bugs.html'},
        {id: 5, text: '数据', path: '../data.html'},
        {id: 6, text: '工具', path: '../tools.html'},
        {id: 7, text: '登陆', path: '../login.html'},
        {id: 8, text: '上传', path: 'http://upload.sbx0.cn/'},
        {id: 9, text: '开源', path: 'https://github.com/sbx0'},
    ],
    'nav_scroller_components':
        '<a class="nav-link" :href="nav_scroller.path" v-html="nav_scroller.text" :title="nav_scroller.title"></a>',
    json: {
        article: {
            list: 'article/index',
            one: 'article/',
            top: 'article/top',
            save: 'article/save',
        },
    },
    loadding: '<div class="spinner">\n    <div class="bounce1"></div>\n    <div class="bounce2"></div>\n    <div class="bounce3"></div>\n</div>',
    space: '空间站',
    index: '首页',
    tools: '工具',
    login: '登陆',
    market: '市场',
    message: '消息',
    search: '搜索',
    upload: '上传',
    feedback: '反馈',
    more: '更多',
    loading: '加载中',
    continue: '继续',
    read: '查看',
    query: '查询',
    no: '没有',
    result: '结果',
    null: '为空',
    prev: '上一页',
    prev_article: '上一篇',
    next: '下一页',
    next_article: '下一篇',
    comment: '评论',
    about: '关于',
    top: '置顶',
    login: '登陆',
    success: '成功',
    fail: '失败',
    network: '网络',
    error: '错误',
    please: '请',
    check: '检查',
    username: '用户名',
    and: '和',
    password: '密码',
    is_or_not: '是否',
    right: '正确',
    detail: '详情',
    data_center: '数据',
    about_content: 'GitHub:<a href="https://github.com/sbx0/Space" target="_blank">Space</a><br>' +
        '开发工具:IntelliJ IDEA<br>' +
        '前端框架:Bootstrap<br>' +
        '后端框架:Spring Boot<br>' +
        'JavaScript框架:Vue.js<br>',
    copyright: '<a href="https://github.com/sbx0/" target="_blank">sbx0</a> 个人网站 2017 - 2018',
    back_to_top: '返回顶部',
}