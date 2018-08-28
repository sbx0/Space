function getDate(strDate) {
    var date = eval('new Date(' + strDate.replace(/\d+(?=-[^-]+$)/,
        function (a) {
            return parseInt(a, 10) - 1
        }).match(/\d+/g) + ')')
    return date
}

// js 的 arguments 来实现重载
function DateMinus() {
    var sdate = new Date(arguments[0].replace(/-/g, "/"));
    var edate;
    if (arguments.length == 1) edate = new Date();
    else if (arguments.length == 2) edate = new Date(arguments[1].replace(/-/g, "/"));
    var days = sdate.getTime() - edate.getTime();
    var day = parseInt(days / (1000 * 60 * 60 * 24));
    return day;
}

// function DateMinus(sDate) {
//     var sdate = new Date(sDate.replace(/-/g, "/"));
//     var now = new Date();
//     var days = sdate.getTime() - now.getTime();
//     var day = parseInt(days / (1000 * 60 * 60 * 24));
//     return day;
// }
//
// function DateMinus(sDate, eDate) {
//     var sdate = new Date(sDate.replace(/-/g, "/"));
//     var edate = new Date(eDate.replace(/-/g, "/"));
//     var days = sdate.getTime() - edate.getTime();
//     var day = parseInt(days / (1000 * 60 * 60 * 24));
//     return day;
// }

function Format(now, mask) {
    var d = now
    var zeroize = function (value, length) {
        if (!length) length = 2
        value = String(value)
        for (var i = 0, zeros = ''; i < (length - value.length); i++) {
            zeros += '0'
        }
        return zeros + value
    }
    return mask.replace(/"[^"]*"|'[^']*'|\b(?:d{1,4}|m{1,4}|yy(?:yy)?|([hHMstT])\1?|[lLZ])\b/g, function ($0) {
        switch ($0) {
            case 'd':
                return d.getDate()
            case 'dd':
                return zeroize(d.getDate())
            case 'ddd':
                return ['Sun', 'Mon', 'Tue', 'Wed', 'Thr', 'Fri', 'Sat'][d.getDay()]
            case 'dddd':
                return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][d.getDay()]
            case 'M':
                return d.getMonth() + 1
            case 'MM':
                return zeroize(d.getMonth() + 1)
            case 'MMM':
                return ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'][d.getMonth()]
            case 'MMMM':
                return ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][d.getMonth()]
            case 'yy':
                return String(d.getFullYear()).substr(2)
            case 'yyyy':
                return d.getFullYear()
            case 'h':
                return d.getHours() % 12 || 12
            case 'hh':
                return zeroize(d.getHours() % 12 || 12)
            case 'H':
                return d.getHours()
            case 'HH':
                return zeroize(d.getHours())
            case 'm':
                return d.getMinutes()
            case 'mm':
                return zeroize(d.getMinutes())
            case 's':
                return d.getSeconds()
            case 'ss':
                return zeroize(d.getSeconds())
            case 'l':
                return zeroize(d.getMilliseconds(), 3)
            case 'L':
                var m = d.getMilliseconds()
                if (m > 99) m = Math.round(m / 10)
                return zeroize(m)
            case 'tt':
                return d.getHours() < 12 ? 'am' : 'pm'
            case 'TT':
                return d.getHours() < 12 ? 'AM' : 'PM'
            case 'Z':
                return d.toUTCString().match(/[A-Z]+$/)
            default:
                return $0.substr(1, $0.length - 2)
        }
    })
}