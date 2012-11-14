//document.addEventListener("DOMContentLoaded", function () {
//    var tagline = document.querySelector("p.tagline");
//    tagline.innerText = "From Cloud9 IDE!";
//});

$(document).ready(function() {
var duration = 3000
var delay = (105)*duration/$(window).width()
var count = 0

function movePic(id) {
    var pic_id = id % 4;
    $("#main").append("<div id=\"id" + id + "\" class = \"imagediv\" > \
                <img src=\"images/image-" + pic_id + ".png\" class=\"myimage\"></img> \
                        </div>")   
    
    $("#id" + id).animate({left: $(window).width()}, duration, "linear", function(id){
            return function(){ $("#id" + id).remove(); }
        }(id));
}

window.setInterval(function(){
  /// call your function here
  movePic(count)
  count = count + 1
},delay);
});