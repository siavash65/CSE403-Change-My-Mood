function changesrc(yt_id) {
    $('#yt-frame').attr('src', "http://www.youtube.com/embed/" + yt_id + "?fs=0");
    setSize($(document).width(), 0.80 * $(document).height());
    
    window.scrollTo(15, 20);
};

function fullScreen2() {
    var w = $(document).width();
    var h = $(document).height();
    $('#yt-frame').addClass('rotate');
    setSize(w, 1.75 * h);
    window.scrollBy(1000, 0);
}

function fullScreen() {
    var width = $(window).width(),
        height = $(window).height();
        
    $('#main').addClass('rotate');
    
    var angle = 90 * Math.PI / 180,
    sin   = Math.sin(angle),
    cos   = Math.cos(angle);
    
//    var width = $(window).width(),
//        height = $(window).height();
        

// (w,0) rotation
    var x1 = cos * width, y1 = sin * width;

// (0,h) rotation
    var x2 = -sin * height, y2 = cos * height;

// (w,h) rotation
    var x3 = cos * width - sin *height, y3 = sin * width + cos * height;

    var minX = Math.min(0, x1, x2, x3),
        maxX = Math.max(0, x1, x2, x3),
        minY = Math.min(0, y1, y2, y3),
        maxY = Math.max(0, y1, y2, y3);

    var rotatedWidth  = maxX - minX,
        rotatedHeight = maxY - minY;
        
    setWindowSize(rotatedWidth, rotatedHeight);
    setSize($(document).height(), $(document).width());
};

function close() {
$('#yt-frame').attr('src', '');
};

function setWindowSize(w, h) {
    $('#yt-frame').attr('width', w);
    $('#yt-frame').attr('height', h);
};

function setSize(w, h) {
    $('#yt-frame').attr('height', h);
    $('#yt-frame').attr('width', w);
};
