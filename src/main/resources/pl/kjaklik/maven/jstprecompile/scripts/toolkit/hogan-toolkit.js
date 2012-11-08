/**
 * User: Krzysiek
 * Date: 18.10.12
 * Time: 11:08
 */

if(typeof(jstprecompileHoganToolkit) == "undefined") {
    var jstprecompileHoganToolkit = {};
}

jstprecompileHoganToolkit.generate = function(namespace, classname, options) {
    if(!$.isPlainObject(options)) {
        throw 'Invalid options: ' + options;
    }
    options.asString = true;

    var result = [];
    $('.' + classname).each(function(index) {
        var name = $(this).attr('id');

        result.push(namespace + '.' + name + '=new Hogan.Template(' + Hogan.compile($(this).html().trim(), options) + ', \'' + name + '\');');
    });
    return result.join('\n');
};
