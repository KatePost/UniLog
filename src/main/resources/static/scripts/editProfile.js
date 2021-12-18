$.ajaxSetup ({
    // Disable caching of AJAX responses
    cache: false
});
$(document).ready(function(){
    $(".saveBtn").hide();
    $(".editBtn").click(function(){
        let inputId = "#" + $(this).val();
        $(inputId).prop("readonly", false);
        $(inputId).focus();
        $(this).hide();
        $(this).siblings(".saveBtn").show();
    });

    $(".saveBtn").click(function(){
        let inputId = $(this).val();
        $.post(location.pathname, { //"studentDetails"
            field: inputId,
            value: $("#" + inputId).val()
        }).done(function(data){
            if(data.success){
                $("#jqueryAlert").html(
                    "<div class=\"alert alert-success alert-dismissible fade show\" role=\"alert\">\n" +
                    "<span>" + data.message + "</span>\n" +
                    "<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                    "</div>\n"
                );
            } else {
                $("#jqueryAlert").html(
                    "<div class=\"alert alert-danger alert-dismissible fade show\" role=\"alert\">\n" +
                    "<span>" + data.message + "</span>\n" +
                    "<button type=\"button\" class=\"btn-close\" data-bs-dismiss=\"alert\" aria-label=\"Close\"></button>\n" +
                    "</div>\n"
                );
            }
            $("#" + inputId).parent().load(window.location + " #" + inputId);
        });
        $("#" + inputId).parent().load(window.location + " #" + inputId);

        $("#" + inputId).prop("readonly", true);
        $(this).hide();
        $(this).siblings(".editBtn").show();

    });
});
