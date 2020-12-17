(function($, $document) {
    "use strict";
    $(document).on('dialog-ready', function(e) {
		var addRetailerBtn = $('.cq-Dialog').find('coral-multifield').children('button');
        var multifieldLimit = $('.cq-Dialog').find('coral-multifield').attr("data-maxRetailersAllowed");
        if (multifieldLimit) {
            $(addRetailerBtn).each(function(i) {
                $(this).click(function(e) {
                    var itemCount = 0;
                    $('.cq-Dialog').find('coral-multifield').children('coral-multifield-item').each(function(i) {
                        itemCount += 1;
                    });
                    if (itemCount == parseInt(multifieldLimit)) {
                        e.stopPropagation();
                        $(addRetailerBtn).hide();
                    }
                });
            });
        }
        $document.on("click", ".coral3-Icon--delete", function(e) {
            $(addRetailerBtn).show();
        });
    });

})($, $(document));