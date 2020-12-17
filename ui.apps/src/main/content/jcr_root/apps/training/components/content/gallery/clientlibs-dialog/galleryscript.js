(function($, $document) {
    "use strict";
	var gForValue=function(){
                    var sel=$(".plp-gallery-for").find("coral-select-item");
                    var temp;
                    if (sel.length != 0) {
                        sel.each(function(index) {
                            if($(this).attr("selected")){
                                temp=$(this).text();
                            }
                        })
                    }
                    return temp;
                };
    $document.on("dialog-ready", function() {
    if ($(".play-cmp-list--editor").length > 0){
		
		if ($(".gallery-for").find(".coral3-Select-label").text() == 'games') {
            var onloadval = $(".gallery-from").find("[value*='landing']");
            onloadval.hide();
        }
		var manualVideo = $(".list-option-listfrom-showhide-target");
        if ($(".gallery-for").find(".coral3-Select-label").text() == 'products' && $(".gallery-from").val() == 'manual') {
            manualVideo.show();
        } else {
            manualVideo.hide();
        }
        $(".gallery-from").find(".coral3-SelectList-item").bind('click', function() {
            var gFrom=$(this).text();
            if ($(".gallery-for").find(".coral3-Select-label").text() == 'products' && gFrom== 'manual') {
                manualVideo.show();
            } else {
                manualVideo.hide();
            }
        })
        $(".gallery-for").find(".coral3-SelectList-item").bind('click', function() {
            var selectedItem = $(this).text();
            var val = $(".gallery-from").find("[value*='landing']");
            if (selectedItem != 'products') {
                manualVideo.hide();
            }
            if (selectedItem == 'games') {
                val.hide();
            } else if (selectedItem == 'products' && $(".gallery-from").val() == 'manual') {
                manualVideo.show();
            } else {
                val.show();
            }

        })
		var productsManualList = $(".products-list-option-listfrom-showhide-target");
		if ($(".products-order-by").find(".coral3-Select-label").text() == 'Manual Order') {
            productsManualList.show();
        }

        if ($(".products-order-by").find(".coral3-Select-label").text() == 'Automatic Order') {
            productsManualList.hide();
        }

         $(".products-order-by").find(".coral3-SelectList-item").bind('click', function() {
            var selectedItem = $(this).text();
            if (selectedItem == 'Automatic Order') {
                productsManualList.hide();
            }
            if (selectedItem != 'Automatic Order') {
                productsManualList.show();
            } 

        })
        var category=$(".plp-category").parent();
        var videoPath=$(".plp-video-path").parent();
        var plpmanualVideo = $(".plp-list-option-listfrom-showhide-target");
        var gFromValue=function(){
            var sel=$(".plp-gallery-from").find("coral-select-item");
            var temp;
            if (sel.length != 0) {
                sel.each(function(index) {
                    if($(this).attr("selected")){
                        temp=$(this).text();
                    }
                })
            }
            return temp;
        };
         if (($(".plp-gallery-for").find(".coral3-Select-label").text()== 'video' || gForValue()=='video') && ($(".plp-gallery-from").val() == 'manual' || gFromValue() == 'manual')) {
            plpmanualVideo.show();
            category.hide();
            videoPath.hide();
        } else if(($(".plp-gallery-from").val() == 'manual' || gFromValue() == 'category')){
            plpmanualVideo.hide();
            category.show();
            videoPath.show();
        }
        else if(($(".plp-gallery-from").val() == 'manual' || gFromValue() == 'by date')){
            plpmanualVideo.hide();
            category.hide();
            videoPath.show();
        }
        $(".plp-gallery-from").find(".coral3-SelectList-item").bind('click', function() {
            var gFrom=$(this).text();
            if (gForValue() == 'video' && gFrom== 'manual') {
                $(".plp-list-option-listfrom-showhide-target").show();
                $(".plp-category").parent().hide();
                $(".plp-video-path").parent().hide();
            } else if(gFrom== 'category'){
                $(".plp-list-option-listfrom-showhide-target").hide();
				$(".plp-category").parent().show();
                $(".plp-video-path").parent().show();
            }else if(gFrom== 'by date'){
                $(".plp-list-option-listfrom-showhide-target").hide();
				$(".plp-category").parent().hide();
                $(".plp-video-path").parent().show();
            }
        })
        $(".plp-gallery-for").find(".coral3-SelectList-item").bind('click', function() {
            var selectedItem = $(this).text();
            if (selectedItem != 'video') {
                plpmanualVideo.hide();
                if($(".plp-gallery-from").val()== 'category'){
				$(".plp-category").parent().show();
                $(".plp-video-path").parent().show();
            }else if($(".plp-gallery-from").val()== 'by date'){
				category.hide();
                $(".plp-video-path").parent().show();
            }
            }
            if (selectedItem == 'video' && $(".plp-gallery-from").val() == 'manual') {
                var manualVideo = $(".plp-list-option-listfrom-showhide-target");
                manualVideo.show();
            }

        })
	}
    });
})($, $(document));