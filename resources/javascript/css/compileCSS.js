var compileCSS = function(toFile, toClassLoader, tlLess) 
{
    var lcCSS = tlLess ?
        compileFile(toFile, toClassLoader) : 
        readUrl(toFile, 'UTF-8').replace(/\r/g, '');
    
    try
    {
        return YAHOO.compressor.cssmin(lcCSS);
    }
    catch (ex)
    {
        return lcCSS;
    }
};