/**
 * User: Krzysiek
 * Date: 07.11.12
 * Time: 21:43
 */

if(typeof(jstprecompileHandlebarsToolkit) == "undefined") {
    var jstprecompileHandlebarsToolkit = {};
}

jstprecompileHandlebarsToolkit.generate = function(namespace, classname, options) {
    if(!$.isPlainObject(options)) {
        throw 'Invalid options: ' + options;
    }
    var result = [];
    $('.' + classname).each(function(index) {
        var name = $(this).attr('id');

        result.push(namespace + '.' + name + '=Handlebars.template(' + Handlebars.precompile($(this).html().trim(), options) + ');');
    });
    return result.join('\n');
};
