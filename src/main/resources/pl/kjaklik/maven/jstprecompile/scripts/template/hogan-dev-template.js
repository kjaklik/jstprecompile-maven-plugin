(function(namespace) {
    $.ajax({
        type: 'get',
        url: '{{filepath}}',
        async: false,
        dataType: 'html',
        success: function(data) {
            var e = $('<p>').html(data);
            $('.{{classname}}', e).each(function(index) {
                var name = $(this).attr('id');

                namespace[name] = Hogan.compile($(this).html().trim());
            });
        }
    });
})({{namespace}});