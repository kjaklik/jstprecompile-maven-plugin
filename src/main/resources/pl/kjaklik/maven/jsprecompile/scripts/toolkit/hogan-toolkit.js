/**
 * User: Krzysiek
 * Date: 18.10.12
 * Time: 11:08
 */

if(typeof(jsprecompileHoganToolkit) == "undefined") {
    var jsprecompileHoganToolkit = {};
}

jsprecompileHoganToolkit.generate = function(namespace, classname) {
    var result = [];
    $('.' + classname).each(function(index) {
        var name = $(this).attr('id');

        result.push(namespace + '.' + name + '=new Hogan.Template(' + Hogan.compile($(this).html().trim(), { asString: true }) + ', \'' + name + '\');');
    });
    return result.join('\n');
};
