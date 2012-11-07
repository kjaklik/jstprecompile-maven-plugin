/**
 * User: Krzysiek
 * Date: 07.11.12
 * Time: 21:43
 */

if(typeof(jsprecompileHandlebarsToolkit) == "undefined") {
    var jsprecompileHandlebarsToolkit = {};
}

jsprecompileHandlebarsToolkit.generate = function(namespace, classname) {
    var known = {};
    var options = {
        knownHelpers: known,
        knownHelpersOnly: false
    };
    var result = [];
    $('.' + classname).each(function(index) {
        var name = $(this).attr('id');

        result.push(namespace + '.' + name + '=' + Handlebars.precompile($(this).html().trim(), options) + ';');
    });
    return result.join('\n');
};
