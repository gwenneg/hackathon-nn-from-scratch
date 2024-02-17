$(document).ready(function(){
    let next = "cross";
    $(".grid > div").click(function() {
        let mark = $("#" + next).clone();
        $(this).append(mark);
        mark.show();
        next = next === "cross" ? "circle" : "cross";
    })
});
