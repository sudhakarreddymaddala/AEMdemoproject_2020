(function($, $document) {
    "use strict";
    $document.on("dialog-ready", function() {
		
    if ($(".play-cmp-list--editor").length > 0){
		
		if ($(".method-countrySelection").find(".coral3-Select-label").text() == 'automatic') {
            var onloadval = $(".method-Selection").find("[value*='landing']");
            onloadval.hide();
        }        
	}
    });

})($, $(document));