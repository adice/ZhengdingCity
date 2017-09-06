var c=$(".middle_right_search").css("height");
var b=$(".middle_left_search").css("height");

var c_1=parseInt(c);
var b_1=parseInt(b);
if(c_1>b_1){
    $(".middle_left_search").css("height",c_1+58);
}
else {
    $(".middle_right_search").css("height",b_1);
}
